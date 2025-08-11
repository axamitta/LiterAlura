package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.RespuestaLibros;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Principal {

    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu() {
        System.out.println("Por favor, escribe el nombre del libro que deseas buscar");
        var tituloDelLibro = teclado.nextLine();

        String tituloEncoded = URLEncoder.encode(tituloDelLibro, StandardCharsets.UTF_8);
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + tituloEncoded);

        System.out.println("Respuesta JSON de la API:");
        System.out.println(json);
        buscarLibro(json);


    }

    public void buscarLibro(String json) {
        var respuesta = conversor.obtenerDatos(json, RespuestaLibros.class);

        if (respuesta.resultados() != null && !respuesta.resultados().isEmpty()) {
            var libro = respuesta.resultados().get(0);
            System.out.println("Primer libro encontrado: " + libro.titulo());
            System.out.println("Autor principal: " + libro.autorPrincipal().nombre());
            System.out.println("Idioma principal: " + libro.idiomaPrincipal());
            System.out.println("Número de descargas: " + libro.numeroDescargas());
        } else {
            System.out.println("No se encontraron libros para esa búsqueda.");
        }
    }
}
