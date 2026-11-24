package cl.suus.ms_ventas.service;

import cl.suus.ms_ventas.dto.ProductoMasVendidoResponse;
import cl.suus.ms_ventas.dto.ResumenVentasResponse;
import cl.suus.ms_ventas.model.EstadoBoleta;
import cl.suus.ms_ventas.repository.BoletaRepository;
import cl.suus.ms_ventas.repository.DetalleBoletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final BoletaRepository boletaRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;

    public ResumenVentasResponse obtenerResumen() {
        Long totalVendido = boletaRepository.sumarTotalExcluyendoEstado(EstadoBoleta.ANULADA);
        long cantidadBoletas = boletaRepository.countByEstadoNot(EstadoBoleta.ANULADA);
        long ticketPromedio = (cantidadBoletas > 0) ? (totalVendido / cantidadBoletas) : 0L;
        List<ProductoMasVendidoResponse> masVendidos =
                detalleBoletaRepository.findMasVendidosExcluyendoEstado(EstadoBoleta.ANULADA);

        return new ResumenVentasResponse(totalVendido, cantidadBoletas, ticketPromedio, masVendidos);
    }
}