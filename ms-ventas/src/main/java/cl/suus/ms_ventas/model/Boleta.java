package cl.suus.ms_ventas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoBoleta estado;

    @Column(nullable = false)
    private Integer total;

    @Column(name = "usuario_id")
    private Long usuarioId;

    // Copia de datos del cliente al momento de la compra
    @Column(name = "cliente_nombre", nullable = false, length = 50)
    private String clienteNombre;

    @Column(name = "cliente_apellidos", nullable = false, length = 100)
    private String clienteApellidos;

    @Column(name = "cliente_correo", nullable = false, length = 100)
    private String clienteCorreo;

    // Copia de datos de envío al momento de la compra
    @Column(nullable = false, length = 200)
    private String calle;

    @Column(length = 50)
    private String departamento;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 100)
    private String comuna;

    @Column(length = 300)
    private String indicaciones;

    @Builder.Default
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleBoleta> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleBoleta detalle) {
        detalles.add(detalle);
        detalle.setBoleta(this);
    }
}