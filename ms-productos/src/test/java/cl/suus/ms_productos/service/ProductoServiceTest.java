package cl.suus.ms_productos.service;

import cl.suus.ms_productos.dto.ProductoRequest;
import cl.suus.ms_productos.dto.ProductoResponse;
import cl.suus.ms_productos.dto.StockItemRequest;
import cl.suus.ms_productos.dto.StockOperacionRequest;
import cl.suus.ms_productos.exception.ConflictException;
import cl.suus.ms_productos.model.Categoria;
import cl.suus.ms_productos.model.Producto;
import cl.suus.ms_productos.repository.CategoriaRepository;
import cl.suus.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crear_retornaProductoConOferta_cuandoDatosSonValidos() {
        // Arrange
        Categoria categoria = Categoria.builder().id(1L).nombre("Perfumes Árabes").color("#5C3A21").build();
        ProductoRequest request = new ProductoRequest(
                "prd-010", "Sauvage Elixir", "Especiado intenso", "Dior",
                95000, 82000, 12, 3, "/img/sauvage.webp", 1L
        );

        when(productoRepository.existsByCodigoIgnoreCase("PRD-010")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        // Act
        ProductoResponse resultado = productoService.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("PRD-010", resultado.codigo());
        assertTrue(resultado.enOferta());
        assertEquals(82000, resultado.precioFinal());
        assertFalse(resultado.critico());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void crear_lanzaConflictException_cuandoPrecioOfertaNoEsMenorAlPrecioNormal() {
        // Arrange
        ProductoRequest request = new ProductoRequest(
                "PRD-011", "Santal 33", "Amaderado", "Le Labo",
                120000, 120000, 10, 3, null, 1L
        );
        when(productoRepository.existsByCodigoIgnoreCase("PRD-011")).thenReturn(false);

        // Act & Assert
        assertThrows(ConflictException.class, () -> productoService.crear(request));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_lanzaConflictException_cuandoCodigoYaExiste() {
        // Arrange
        ProductoRequest request = new ProductoRequest(
                "PRD-001", "Duplicado", "Desc", "Marca",
                50000, null, 10, 2, null, 1L
        );
        when(productoRepository.existsByCodigoIgnoreCase("PRD-001")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> productoService.crear(request));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void descontarStock_actualizaInventario_cuandoTodosTienenStockSuficiente() {
        // Arrange
        Producto p1 = Producto.builder().id(1L).nombre("Andrews").stock(10).build();
        Producto p2 = Producto.builder().id(2L).nombre("Power of You").stock(5).build();
        StockOperacionRequest request = new StockOperacionRequest(List.of(
                new StockItemRequest(1L, 3),
                new StockItemRequest(2L, 2)
        ));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(p2));

        // Act
        productoService.descontarStock(request);

        // Assert
        assertEquals(7, p1.getStock());
        assertEquals(3, p2.getStock());
        verify(productoRepository, times(2)).save(any(Producto.class));
    }

    @Test
    void descontarStock_noDescuentaNinguno_cuandoUnProductoNoTieneStockSuficiente() {
        // Arrange (operación todo o nada: p1 sí alcanza, p2 no alcanza)
        Producto p1 = Producto.builder().id(1L).nombre("Andrews").stock(10).build();
        Producto p2 = Producto.builder().id(2L).nombre("Oud Wood").stock(1).build();
        StockOperacionRequest request = new StockOperacionRequest(List.of(
                new StockItemRequest(1L, 2),
                new StockItemRequest(2L, 5)
        ));

        when(productoRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(p2));

        // Act & Assert
        assertThrows(ConflictException.class, () -> productoService.descontarStock(request));
        assertEquals(10, p1.getStock());
        assertEquals(1, p2.getStock());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void reponerStock_sumaUnidadesAlInventarioActual() {
        // Arrange
        Producto p1 = Producto.builder().id(1L).nombre("Andrews").stock(5).build();
        StockOperacionRequest request = new StockOperacionRequest(List.of(
                new StockItemRequest(1L, 3)
        ));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p1));

        // Act
        productoService.reponerStock(request);

        // Assert
        assertEquals(8, p1.getStock());
        verify(productoRepository, times(1)).save(p1);
    }
}