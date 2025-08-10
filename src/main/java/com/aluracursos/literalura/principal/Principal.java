package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

public class Principal {

    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void ejecutar() {
        String json = consumoApi.obtenerDatos(URL_BASE);
        // Aquí conviertes el json a los objetos que hayas definido, por ejemplo:
        // Libro[] libros = conversor.obtenerDatos(json, Libro[].class);
        System.out.println("Respuesta JSON de la API:");
        System.out.println(json); // Para probar que trae la info
    }
}
