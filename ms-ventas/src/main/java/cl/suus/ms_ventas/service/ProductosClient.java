package cl.suus.ms_ventas.service;

import cl.suus.ms_ventas.dto.ProductoExternoDto;
import cl.suus.ms_ventas.dto.StockItemDto;
import cl.suus.ms_ventas.dto.StockOperacionDto;
import cl.suus.ms_ventas.exception.ApiError;
import cl.suus.ms_ventas.exception.ConflictException;
import cl.suus.ms_ventas.exception.ResourceNotFoundException;
import cl.suus.ms_ventas.exception.ServiceUnavailableException;
import cl.suus.ms_ventas.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductosClient {

    private final RestClient productosRestClient;
    private final JwtService jwtService;

    public ProductoExternoDto obtenerProducto(Long productoId) {
        try {
            ProductoExternoDto producto = productosRestClient.get()
                    .uri("/api/v1/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoExternoDto.class);

            if (producto == null) {
                throw new ResourceNotFoundException("Producto no encontrado con id: " + productoId);
            }
            return producto;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productoId);
        } catch (ResourceAccessException | HttpServerErrorException ex) {
            log.error("Error de comunicación con ms-productos al consultar producto {}: {}", productoId, ex.getMessage());
            throw new ServiceUnavailableException("Servicio de productos no disponible. Intente nuevamente más tarde.");
        }
    }

    public void descontarStock(List<StockItemDto> items) {
        String tokenInterno = jwtService.generarTokenInterno();
        try {
            productosRestClient.post()
                    .uri("/api/v1/productos/stock/descontar")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenInterno)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new StockOperacionDto(items))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict ex) {
            throw new ConflictException(extraerMensajeRemoto(ex, "Stock insuficiente para completar la compra"));
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(extraerMensajeRemoto(ex, "Uno o más productos no existen"));
        } catch (ResourceAccessException | HttpServerErrorException ex) {
            log.error("Error de comunicación con ms-productos al descontar stock: {}", ex.getMessage());
            throw new ServiceUnavailableException("Servicio de productos no disponible. Intente nuevamente más tarde.");
        }
    }

    public void reponerStock(List<StockItemDto> items) {
        String tokenInterno = jwtService.generarTokenInterno();
        try {
            productosRestClient.post()
                    .uri("/api/v1/productos/stock/reponer")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenInterno)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new StockOperacionDto(items))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(extraerMensajeRemoto(ex, "No se encontraron los productos para reponer stock"));
        } catch (ResourceAccessException | HttpServerErrorException ex) {
            log.error("Error de comunicación con ms-productos al reponer stock: {}", ex.getMessage());
            throw new ServiceUnavailableException("Servicio de productos no disponible para reponer el stock.");
        }
    }

    private String extraerMensajeRemoto(HttpClientErrorException ex, String defecto) {
        try {
            ApiError errorRemoto = ex.getResponseBodyAs(ApiError.class);
            if (errorRemoto != null && errorRemoto.mensaje() != null && !errorRemoto.mensaje().isBlank()) {
                return errorRemoto.mensaje();
            }
        } catch (Exception ignored) {
            // Si la respuesta remota no coincide con ApiError, se usa el mensaje por defecto
        }
        return defecto;
    }
}