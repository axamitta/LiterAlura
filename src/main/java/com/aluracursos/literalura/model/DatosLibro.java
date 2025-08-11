package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DatosLibro(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DatosAutor> autores,
        @JsonAlias("languages") List<String> idiomas,
        @JsonAlias("download_count") Integer numeroDescargas
        ) {
    public DatosAutor autorPrincipal() {
        return autores != null && !autores.isEmpty() ? autores.get(0) : null;
    }

    public String idiomaPrincipal() {
        return idiomas != null && !idiomas.isEmpty() ? idiomas.get(0) : null;
    }
}
