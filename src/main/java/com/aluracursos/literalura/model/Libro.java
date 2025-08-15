package com.aluracursos.literalura.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String idioma;

    @Column(name = "numero_descargas")
    private Integer numeroDescargas;

    // Relación con Autor (muchos libros pueden tener un mismo autor)
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;


    public Libro(String titulo, String idioma, Integer numeroDescargas, Autor autor) {
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
        this.autor = autor;
    }
}

