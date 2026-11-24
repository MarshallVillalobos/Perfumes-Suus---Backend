package cl.suus.ms_productos.config;

import cl.suus.ms_productos.model.Categoria;
import cl.suus.ms_productos.model.Producto;
import cl.suus.ms_productos.repository.CategoriaRepository;
import cl.suus.ms_productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0 || productoRepository.count() > 0) {
            return;
        }

        Categoria arabes = categoriaRepository.save(Categoria.builder()
                .nombre("Perfumes Árabes")
                .descripcion("Fragancias intensas de origen oriental con notas amaderadas y especiadas.")
                .color("#5C3A21")
                .build());

        Categoria nicho = categoriaRepository.save(Categoria.builder()
                .nombre("Perfumes de Nicho")
                .descripcion("Creaciones exclusivas de casas perfumeras independientes.")
                .color("#A67B5B")
                .build());

        Categoria autor = categoriaRepository.save(Categoria.builder()
                .nombre("Perfumes de Autor")
                .descripcion("Composiciones icónicas con sello olfativo distintivo.")
                .color("#3D2514")
                .build());

        productoRepository.saveAll(List.of(
                Producto.builder()
                        .codigo("PRD-001")
                        .nombre("Andrews Eau de Parfum")
                        .descripcion("Fragancia exclusiva con notas amaderadas y toques cítricos, diseñada para durar todo el día.")
                        .marca("Suus Signature")
                        .precio(50000)
                        .precioOferta(45000)
                        .stock(15)
                        .stockCritico(5)
                        .imagenUrl("/img/Andrews-1-IA.webp")
                        .categoria(arabes)
                        .build(),
                Producto.builder()
                        .codigo("PRD-002")
                        .nombre("Power of You")
                        .descripcion("Aroma intenso que combina notas dulces y orientales para una presencia inconfundible y magnética.")
                        .marca("Armani")
                        .precio(65000)
                        .precioOferta(null)
                        .stock(8)
                        .stockCritico(5)
                        .imagenUrl("/img/armani-power-of-you-edp-90-ml-silk-perfumes.webp")
                        .categoria(nicho)
                        .build(),
                Producto.builder()
                        .codigo("PRD-003")
                        .nombre("Oud Wood Intense")
                        .descripcion("Fragancia profunda y exótica, centrada en el rico aroma de la madera de oud, complementado con especias cálidas.")
                        .marca("Tom Ford")
                        .precio(85000)
                        .precioOferta(74990)
                        .stock(0)
                        .stockCritico(3)
                        .imagenUrl("/img/oud-wood-intense-1.jpg")
                        .categoria(arabes)
                        .build(),
                Producto.builder()
                        .codigo("PRD-004")
                        .nombre("Santal 33")
                        .descripcion("Un aroma icónico e inconfundible, con notas de cardamomo, iris, violeta y ambroxan, que evoca el espíritu del oeste americano.")
                        .marca("Le Labo")
                        .precio(120000)
                        .precioOferta(null)
                        .stock(3)
                        .stockCritico(5)
                        .imagenUrl("/img/D_NQ_NP_798570-MLA82944418030_032025-O.webp")
                        .categoria(autor)
                        .build()
        ));

        log.info("Datos iniciales cargados en suus_productos: 3 categorías y 4 perfumes.");
    }
}