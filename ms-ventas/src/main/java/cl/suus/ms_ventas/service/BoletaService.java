package cl.suus.ms_ventas.service;

import cl.suus.ms_ventas.dto.*;
import cl.suus.ms_ventas.exception.ConflictException;
import cl.suus.ms_ventas.exception.ResourceNotFoundException;
import cl.suus.ms_ventas.model.Boleta;
import cl.suus.ms_ventas.model.DetalleBoleta;
import cl.suus.ms_ventas.model.EstadoBoleta;
import cl.suus.ms_ventas.repository.BoletaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final ProductosClient productosClient;

    public BoletaResponse registrarCompra(BoletaRequest request, Long usuarioId) {
        // 1. Agrupar cantidades si un mismo productoId viene repetido
        Map<Long, Integer> cantidadesPorProducto = new LinkedHashMap<>();
        for (ItemCompraRequest item : request.items()) {
            cantidadesPorProducto.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        // 2. Consultar precio real actual en ms-productos y calcular total en el backend
        Boleta boleta = Boleta.builder()
                .fecha(LocalDateTime.now())
                .estado(EstadoBoleta.PAGADA)
                .usuarioId(usuarioId)
                .clienteNombre(request.nombre().trim())
                .clienteApellidos(request.apellidos().trim())
                .clienteCorreo(request.correo().trim().toLowerCase())
                .calle(request.calle().trim())
                .departamento(request.departamento())
                .region(request.region().trim())
                .comuna(request.comuna().trim())
                .indicaciones(request.indicaciones())
                .build();

        List<StockItemDto> itemsStock = new ArrayList<>();
        int totalCalculado = 0;

        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Long productoId = entry.getKey();
            int cantidad = entry.getValue();

            ProductoExternoDto producto = productosClient.obtenerProducto(productoId);
            int precioUnitario = obtenerPrecioVigente(producto);
            int subtotal = precioUnitario * cantidad;
            totalCalculado += subtotal;

            boleta.agregarDetalle(DetalleBoleta.builder()
                    .productoId(producto.id())
                    .nombreProducto(producto.nombre())
                    .cantidad(cantidad)
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build());

            itemsStock.add(new StockItemDto(producto.id(), cantidad));
        }
        boleta.setTotal(totalCalculado);

        // 3. Solicitar descuento de stock en una sola operación a ms-productos
        productosClient.descontarStock(itemsStock);

        // 4. Guardar boleta; si falla tras descontar, ejecutar acción de compensación
        try {
            Boleta guardada = boletaRepository.save(boleta);
            log.info("Boleta registrada exitosamente: id={}, total={}, usuarioId={}",
                    guardada.getId(), guardada.getTotal(), guardada.getUsuarioId());
            return BoletaResponse.from(guardada);
        } catch (Exception ex) {
            log.error("Error al guardar boleta tras descontar stock. Ejecutando compensación...", ex);
            try {
                productosClient.reponerStock(itemsStock);
                log.info("Compensación exitosa: stock repuesto en ms-productos.");
            } catch (Exception compEx) {
                log.error("FALLO CRÍTICO en compensación de stock: {}", compEx.getMessage());
            }
            throw ex;
        }
    }

    public List<BoletaResponse> listarTodas() {
        return boletaRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(BoletaResponse::from)
                .toList();
    }

    public BoletaResponse obtenerPorId(Long id) {
        return BoletaResponse.from(buscarEntidad(id));
    }

    public List<BoletaResponse> listarPorUsuario(Long usuarioId) {
        return boletaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(BoletaResponse::from)
                .toList();
    }

    @Transactional
    public BoletaResponse cambiarEstado(Long id, EstadoBoleta nuevoEstado) {
        Boleta boleta = buscarEntidad(id);

        if (boleta.getEstado() == EstadoBoleta.ANULADA) {
            throw new ConflictException("La boleta #" + id + " ya está anulada y no puede cambiar de estado");
        }

        if (nuevoEstado == EstadoBoleta.ANULADA) {
            List<StockItemDto> itemsAReponer = boleta.getDetalles()
                    .stream()
                    .map(d -> new StockItemDto(d.getProductoId(), d.getCantidad()))
                    .toList();
            productosClient.reponerStock(itemsAReponer);
            log.info("Boleta #{} anulada: stock repuesto en ms-productos.", id);
        }

        boleta.setEstado(nuevoEstado);
        return BoletaResponse.from(boletaRepository.save(boleta));
    }

    private int obtenerPrecioVigente(ProductoExternoDto producto) {
        if (producto.precioFinal() != null) {
            return producto.precioFinal();
        }
        if (producto.precioOferta() != null && producto.precioOferta() < producto.precio()) {
            return producto.precioOferta();
        }
        return producto.precio();
    }

    private Boleta buscarEntidad(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleta no encontrada con id: " + id));
    }
}