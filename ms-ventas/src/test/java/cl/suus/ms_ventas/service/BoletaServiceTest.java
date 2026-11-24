package cl.suus.ms_ventas.service;

import cl.suus.ms_ventas.dto.BoletaRequest;
import cl.suus.ms_ventas.dto.BoletaResponse;
import cl.suus.ms_ventas.dto.ItemCompraRequest;
import cl.suus.ms_ventas.dto.ProductoExternoDto;
import cl.suus.ms_ventas.dto.StockItemDto;
import cl.suus.ms_ventas.exception.ConflictException;
import cl.suus.ms_ventas.model.Boleta;
import cl.suus.ms_ventas.model.DetalleBoleta;
import cl.suus.ms_ventas.model.EstadoBoleta;
import cl.suus.ms_ventas.repository.BoletaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ProductosClient productosClient;

    @InjectMocks
    private BoletaService boletaService;

    private BoletaRequest crearRequestEjemplo() {
        return new BoletaRequest(
                "Pedro", "González", "pedro@gmail.com",
                "Av. Mapocho 4120", "Depto 402", "RM", "Quinta Normal",
                "Dejar en conserjería",
                List.of(new ItemCompraRequest(1L, 2))
        );
    }

    @Test
    void registrarCompra_calculaTotalConPrecioOfertaYDescuentaStock() {
        // Arrange
        ProductoExternoDto perfume = new ProductoExternoDto(
                1L, "PRD-001", "Andrews Eau de Parfum", 50000, 45000, 45000, true, 15
        );
        when(productosClient.obtenerProducto(1L)).thenReturn(perfume);
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> {
            Boleta b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        // Act
        BoletaResponse response = boletaService.registrarCompra(crearRequestEjemplo(), 3L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(90000, response.total()); // 2 x $45.000 (precio oferta)
        assertEquals(EstadoBoleta.PAGADA, response.estado());
        assertEquals(3L, response.usuarioId());
        assertEquals(1, response.detalles().size());

        verify(productosClient, times(1)).descontarStock(List.of(new StockItemDto(1L, 2)));
        verify(productosClient, never()).reponerStock(anyList());
    }

    @Test
    void registrarCompra_ejecutaCompensacionDeStock_cuandoFallaGuardarEnBaseDeDatos() {
        // Arrange
        ProductoExternoDto perfume = new ProductoExternoDto(
                1L, "PRD-001", "Andrews Eau de Parfum", 50000, 45000, 45000, true, 15
        );
        when(productosClient.obtenerProducto(1L)).thenReturn(perfume);
        when(boletaRepository.save(any(Boleta.class))).thenThrow(new RuntimeException("Fallo simulado de BD"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> boletaService.registrarCompra(crearRequestEjemplo(), 3L));

        verify(productosClient, times(1)).descontarStock(List.of(new StockItemDto(1L, 2)));
        verify(productosClient, times(1)).reponerStock(List.of(new StockItemDto(1L, 2)));
    }

    @Test
    void cambiarEstado_aAnulada_reponeStockEnMsProductos() {
        // Arrange
        Boleta boleta = Boleta.builder()
                .id(1L).fecha(LocalDateTime.now()).estado(EstadoBoleta.PAGADA).total(90000)
                .clienteNombre("Pedro").clienteApellidos("González").clienteCorreo("pedro@gmail.com")
                .calle("Av. Mapocho 4120").region("RM").comuna("Quinta Normal")
                .build();
        boleta.agregarDetalle(DetalleBoleta.builder()
                .id(10L).productoId(1L).nombreProducto("Andrews").cantidad(2)
                .precioUnitario(45000).subtotal(90000).build());

        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boleta));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BoletaResponse response = boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA);

        // Assert
        assertEquals(EstadoBoleta.ANULADA, response.estado());
        verify(productosClient, times(1)).reponerStock(List.of(new StockItemDto(1L, 2)));
    }

    @Test
    void cambiarEstado_lanzaConflictException_cuandoBoletaYaEstaAnulada() {
        // Arrange
        Boleta boletaAnulada = Boleta.builder()
                .id(1L).estado(EstadoBoleta.ANULADA).total(90000).build();
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaAnulada));

        // Act & Assert
        assertThrows(ConflictException.class,
                () -> boletaService.cambiarEstado(1L, EstadoBoleta.ENVIADA));

        verify(productosClient, never()).reponerStock(anyList());
        verify(boletaRepository, never()).save(any());
    }
}