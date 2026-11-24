package cl.suus.ms_productos.service;

import cl.suus.ms_productos.dto.ProductoRequest;
import cl.suus.ms_productos.dto.ProductoResponse;
import cl.suus.ms_productos.dto.StockItemRequest;
import cl.suus.ms_productos.dto.StockOperacionRequest;
import cl.suus.ms_productos.exception.ConflictException;
import cl.suus.ms_productos.exception.ResourceNotFoundException;
import cl.suus.ms_productos.model.Categoria;
import cl.suus.ms_productos.model.Producto;
import cl.suus.ms_productos.repository.CategoriaRepository;
import cl.suus.ms_productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public List<ProductoResponse> listar(Long categoriaId, String texto) {
        String textoFiltro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return productoRepository.buscarConFiltros(categoriaId, textoFiltro)
                .stream()
                .map(ProductoResponse::from)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        return ProductoResponse.from(buscarEntidad(id));
    }

    public List<ProductoResponse> listarOfertas() {
        return productoRepository.findOfertas()
                .stream()
                .map(ProductoResponse::from)
                .toList();
    }

    public List<ProductoResponse> listarCriticos() {
        return productoRepository.findCriticos()
                .stream()
                .map(ProductoResponse::from)
                .toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        String codigoLimpio = request.codigo().trim().toUpperCase();
        if (productoRepository.existsByCodigoIgnoreCase(codigoLimpio)) {
            throw new ConflictException("Ya existe un producto con el código: " + codigoLimpio);
        }

        validarPrecioOferta(request.precio(), request.precioOferta());
        Categoria categoria = buscarCategoria(request.categoriaId());

        Producto producto = Producto.builder()
                .codigo(codigoLimpio)
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion())
                .marca(request.marca())
                .precio(request.precio())
                .precioOferta(request.precioOferta())
                .stock(request.stock())
                .stockCritico(request.stockCritico())
                .imagenUrl(request.imagenUrl())
                .categoria(categoria)
                .build();

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());
        return ProductoResponse.from(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarEntidad(id);
        String codigoLimpio = request.codigo().trim().toUpperCase();

        productoRepository.findByCodigoIgnoreCase(codigoLimpio)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("Ya existe otro producto con el código: " + codigoLimpio);
                });

        validarPrecioOferta(request.precio(), request.precioOferta());
        Categoria categoria = buscarCategoria(request.categoriaId());

        producto.setCodigo(codigoLimpio);
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setMarca(request.marca());
        producto.setPrecio(request.precio());
        producto.setPrecioOferta(request.precioOferta());
        producto.setStock(request.stock());
        producto.setStockCritico(request.stockCritico());
        producto.setImagenUrl(request.imagenUrl());
        producto.setCategoria(categoria);

        return ProductoResponse.from(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarEntidad(id);
        productoRepository.delete(producto);
        log.info("Producto eliminado: id={}", id);
    }

    /**
     * Operación atómica (todo o nada): primero valida que TODOS los productos
     * existan y tengan stock suficiente antes de descontar alguno.
     */
    @Transactional
    public void descontarStock(StockOperacionRequest request) {
        Map<Long, Integer> cantidadesPorProducto = agruparCantidades(request.items());
        Map<Producto, Integer> productosValidados = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Producto producto = buscarEntidad(entry.getKey());
            int solicitado = entry.getValue();

            if (producto.getStock() < solicitado) {
                throw new ConflictException(
                        "Stock insuficiente para '" + producto.getNombre()
                                + "'. Disponible: " + producto.getStock()
                                + ", solicitado: " + solicitado
                );
            }
            productosValidados.put(producto, solicitado);
        }

        for (Map.Entry<Producto, Integer> entry : productosValidados.entrySet()) {
            Producto p = entry.getKey();
            p.setStock(p.getStock() - entry.getValue());
            productoRepository.save(p);
        }
        log.info("Stock descontado correctamente para {} producto(s)", productosValidados.size());
    }

    /**
     * Reponer stock al anular una boleta o ejecutar compensación desde ms-ventas.
     */
    @Transactional
    public void reponerStock(StockOperacionRequest request) {
        Map<Long, Integer> cantidadesPorProducto = agruparCantidades(request.items());
        Map<Producto, Integer> productosValidados = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Producto producto = buscarEntidad(entry.getKey());
            productosValidados.put(producto, entry.getValue());
        }

        for (Map.Entry<Producto, Integer> entry : productosValidados.entrySet()) {
            Producto p = entry.getKey();
            p.setStock(p.getStock() + entry.getValue());
            productoRepository.save(p);
        }
        log.info("Stock repuesto correctamente para {} producto(s)", productosValidados.size());
    }

    private Map<Long, Integer> agruparCantidades(List<StockItemRequest> items) {
        Map<Long, Integer> mapa = new LinkedHashMap<>();
        for (StockItemRequest item : items) {
            mapa.merge(item.productoId(), item.cantidad(), Integer::sum);
        }
        return mapa;
    }

    private void validarPrecioOferta(Integer precio, Integer precioOferta) {
        if (precioOferta != null && precioOferta >= precio) {
            throw new ConflictException("El precio de oferta debe ser menor al precio normal");
        }
    }

    private Producto buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private Categoria buscarCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId));
    }
}