package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :año AND (a.fechaFallecimiento IS NULL OR a.fechaFallecimiento >= :año)")
    List<Autor> findAutoresVivosEnAño(@Param("año") Integer año);;
}
