package cl.suus.ms_productos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 100)
    private String marca;

    @Column(nullable = false)
    private Integer precio;

    @Column(name = "precio_oferta")
    private Integer precioOferta;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_critico")
    private Integer stockCritico;

    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}