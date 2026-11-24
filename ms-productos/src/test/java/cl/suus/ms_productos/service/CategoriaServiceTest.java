package cl.suus.ms_productos.service;

import cl.suus.ms_productos.dto.CategoriaRequest;
import cl.suus.ms_productos.dto.CategoriaResponse;
import cl.suus.ms_productos.exception.ConflictException;
import cl.suus.ms_productos.model.Categoria;
import cl.suus.ms_productos.repository.CategoriaRepository;
import cl.suus.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void crear_retornaCategoria_cuandoNombreEsUnico() {
        // Arrange
        CategoriaRequest request = new CategoriaRequest("Perfumes Cítricos", "Notas frescas", "#D4A373");
        when(categoriaRepository.existsByNombreIgnoreCase("Perfumes Cítricos")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(4L);
            return c;
        });

        // Act
        CategoriaResponse resultado = categoriaService.crear(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(4L, resultado.id());
        assertEquals("Perfumes Cítricos", resultado.nombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void eliminar_lanzaConflictException_cuandoCategoriaTieneProductosAsociados() {
        // Arrange
        Categoria categoria = Categoria.builder().id(1L).nombre("Perfumes Árabes").build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> categoriaService.eliminar(1L));
        verify(categoriaRepository, never()).delete(any());
    }
}