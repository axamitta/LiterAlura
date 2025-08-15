package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.model.RespuestaLibros;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> librosBuscados = new ArrayList<>();

    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository){
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;

    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libros por título
                    2 - Mostrar libros buscados
                    3 - Mostrar libros buscados por idioma
                    4 - Mostrar autores de los libros buscados
                    5 - Mostrar autores vivos en un determinado año
                    6 - Mostrar todos los libros almacenados en la base
                    
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    mostrarLibrosBuscadosPorIdioma();
                    break;
                case 4:
                    mostrarAutoresDeLibrosBuscados();
                    break;
                case 5:
                    mostrarAutoresVivosEnFecha();
                    break;
                case 6:
                    mostrarTodosLosLibrosDeLaBase();
                    break;


                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
        //System.out.println("Respuesta JSON de la API:");
        //System.out.println(json);

    }


    private RespuestaLibros getDatosLibro() {
        System.out.println("Por favor, escribe el nombre del libro que deseas buscar");
        var tituloDelLibro = teclado.nextLine();
        String tituloEncoded = URLEncoder.encode(tituloDelLibro, StandardCharsets.UTF_8);

        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + tituloEncoded);
        return conversor.obtenerDatos(json, RespuestaLibros.class);
    }

    private void buscarLibroPorTitulo() {
        var respuesta = getDatosLibro();

        if (respuesta.resultados() != null && !respuesta.resultados().isEmpty()) {
            respuesta.resultados().forEach(l -> {
                Autor autor = getAutor(l);
                autorRepository.save(autor);

                Libro libro = new Libro(
                        l.titulo(),
                        l.idiomaPrincipal(),
                        l.numeroDescargas(),
                        autor
                );
                libroRepository.save(libro);

                System.out.println(
                        "Libro: " + l.titulo() +
                                " | Autor: " + l.autorPrincipal().nombre() +
                                " | Idioma: " + l.idiomaPrincipal() +
                                " | Descargas: " + l.numeroDescargas() +
                                " ----------------------"
                );
                librosBuscados.add(l);
            });
            
        } else {
            System.out.println("No se encontraron libros para esa búsqueda.");
        }
    }

    private static Autor getAutor(DatosLibro l) {
        var autorPrincipal = l.autorPrincipal();
        String nombreAutor = autorPrincipal != null ? autorPrincipal.nombre() : "Autor desconocido";
        String fechaNacimiento = autorPrincipal != null && autorPrincipal.fechaNacimiento() != null
                ? String.valueOf(autorPrincipal.fechaNacimiento()) : "N/A";
        String fechaFallecimiento = autorPrincipal != null && autorPrincipal.fechaFallecimiento() != null
                ? String.valueOf(autorPrincipal.fechaFallecimiento()) : "N/A";

        Autor autor = new Autor(nombreAutor, fechaNacimiento, fechaFallecimiento);
        return autor;
    }


    private void mostrarLibrosBuscados() {
        if (librosBuscados.isEmpty()) {
            System.out.println("No has buscado ningún libro todavía.");
        } else {
            System.out.println("Libros buscados:");
            librosBuscados.forEach(l -> System.out.println(
                    "Libro: " + l.titulo() +
                            " - Autor: " + l.autorPrincipal().nombre() +
                            " - Idioma: " + l.idiomaPrincipal() +
                            " - Número de descargas: " + l.numeroDescargas()
            ));
        }
    }
    private void mostrarLibrosBuscadosPorIdioma() {
            System.out.println("Por favor, escribe el código del idioma para filtrar los libros guardados (ejemplo: en, es, fr):");
            String idiomaBuscado = teclado.nextLine().trim();

            if (idiomaBuscado.isEmpty()) {
                System.out.println("No ingresaste un idioma válido.");
                return;
            }

            List<Libro> librosPorIdioma = libroRepository.findByIdioma(idiomaBuscado);

            Optional.of(librosPorIdioma)
                    .filter(list -> !list.isEmpty())
                    .ifPresentOrElse(
                            list -> list.stream()
                                    .map(this::formateaLibro)
                                    .forEach(System.out::println),
                            () -> System.out.println("No se encontraron libros guardados para el idioma: " + idiomaBuscado)
                    );
        }
    private String formateaLibro(Libro libro) {
        String autor = (libro.getAutor() != null) ? libro.getAutor().getNombre() : "Autor desconocido";
        return String.format("Libro: %s | Autor: %s | Descargas: %d", libro.getTitulo(), autor, libro.getNumeroDescargas());
    }



    private void mostrarAutoresDeLibrosBuscados() {
        var autoresUnicos = libroRepository.findAll().stream()
                .map(Libro::getAutor)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(a ->
                                a.getNombre() == null ? "" : a.getNombre().toLowerCase(Locale.ROOT)))));

        if (autoresUnicos.isEmpty()) {
            System.out.println("No hay autores en los libros guardados.");
            return;
        }

        autoresUnicos.forEach(autor -> System.out.printf(
                "Autor: %s | Fecha de nacimiento: %s | Fecha de fallecimiento: %s%n",
                nullToNA(autor.getNombre()),
                nullToNA(autor.getFechaNacimiento()),
                nullToNA(autor.getFechaFallecimiento())
        ));
    }

    private String nullToNA(Object valor) {
        return valor != null ? valor.toString() : "N/A";
    }


    private void mostrarAutoresVivosEnFecha() {
        System.out.print("Introduce el año que quieres consultar (formato: yyyy): ");
        String añoStr = teclado.nextLine().trim();

        try {
            int añoConsulta = Integer.parseInt(añoStr);

            List<Autor> autores = autorRepository.findAll().stream()
                    .filter(a -> {
                        Integer nacimiento = parseAnioSeguro(a.getFechaNacimiento());
                        Integer fallecimiento = parseAnioSeguro(a.getFechaFallecimiento());

                        boolean nacidoAntes = nacimiento != null && nacimiento <= añoConsulta;
                        boolean fallecidoDespues = fallecimiento == null || fallecimiento >= añoConsulta;

                        return nacidoAntes && fallecidoDespues;
                    })
                    .toList();

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + añoConsulta);
            } else {
                System.out.println("Autores vivos en " + añoConsulta + ":");
                autores.forEach(autor ->
                        System.out.printf("Autor: %s | Nacimiento: %s | Fallecimiento: %s%n",
                                autor.getNombre(),
                                nullToNA(autor.getFechaNacimiento()),
                                autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "Vivo")
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Usa el formato yyyy.");
        }
    }
    private Integer parseAnioSeguro(String añoStr) {
        if (añoStr == null || añoStr.isBlank() || añoStr.equals("N/A")) return null;
        try {
            return Integer.parseInt(añoStr.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void mostrarTodosLosLibrosDeLaBase() {
        List<Libro> todosLosLibros = libroRepository.findAll();

        if (todosLosLibros.isEmpty()) {
            System.out.println("No hay libros guardados en la base de datos.");
            return;
        }

        System.out.println("Listado de todos los libros en la base de datos:");
        todosLosLibros.forEach(libro -> System.out.println(formateaLibro(libro)));
    }
}
