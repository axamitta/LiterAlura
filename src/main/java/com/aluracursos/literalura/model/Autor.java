package com.aluracursos.literalura.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;


    @Column(name = "fecha_fallecimiento")
    private String fechaFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libro> libros;

    public Autor(String nombre, String fechaNacimiento, String fechaFallecimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = (fechaNacimiento != null && !fechaNacimiento.isEmpty()) ? fechaNacimiento : "N/A";
        this.fechaFallecimiento = (fechaFallecimiento != null && !fechaFallecimiento.isEmpty()) ? fechaFallecimiento : "N/A";
    }
}


