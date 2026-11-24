package cl.suus.ms_productos.service;

import cl.suus.ms_productos.dto.CategoriaRequest;
import cl.suus.ms_productos.dto.CategoriaResponse;
import cl.suus.ms_productos.exception.ConflictException;
import cl.suus.ms_productos.exception.ResourceNotFoundException;
import cl.suus.ms_productos.model.Categoria;
import cl.suus.ms_productos.repository.CategoriaRepository;
import cl.suus.ms_productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByOrderByIdAsc()
                .stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = buscarEntidad(id);
        return CategoriaResponse.from(categoria);
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        String nombreLimpio = request.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ConflictException("Ya existe una categoría con el nombre: " + nombreLimpio);
        }

        Categoria categoria = Categoria.builder()
                .nombre(nombreLimpio)
                .descripcion(request.descripcion())
                .color(request.color())
                .build();

        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada: id={}, nombre={}", guardada.getId(), guardada.getNombre());
        return CategoriaResponse.from(guardada);
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidad(id);
        String nombreLimpio = request.nombre().trim();

        categoriaRepository.findByNombreIgnoreCase(nombreLimpio)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("Ya existe otra categoría con el nombre: " + nombreLimpio);
                });

        categoria.setNombre(nombreLimpio);
        categoria.setDescripcion(request.descripcion());
        categoria.setColor(request.color());

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarEntidad(id);

        if (productoRepository.existsByCategoriaId(id)) {
            throw new ConflictException("No se puede eliminar la categoría porque tiene productos asociados");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoría eliminada: id={}", id);
    }

    private Categoria buscarEntidad(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}