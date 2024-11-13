package com.cristhianreyes.desafio.principal;

import com.cristhianreyes.desafio.model.Datos;
import com.cristhianreyes.desafio.model.DatosLibros;
import com.cristhianreyes.desafio.service.ConsumoAPI;
import com.cristhianreyes.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/" ;
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    
    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        //TOP 10 libros descargados
        System.out.println("TOP 10 libros descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
                .limit(10)
                .map(l ->l.titulo().toUpperCase())
                .forEach(System.out::println);


        //Busqueda por nombre
        System.out.println("Ingrese el nombre del libro");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="+tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json,Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l ->l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()){
            System.out.println("libro Encontrado ");
            System.out.println(libroBuscado.get());
        } else {
            System.out.println("Libro no encontrado");
        }

        //Trabajando con estadisticas
        DoubleSummaryStatistics est= datos.resultados().stream()
                .filter(d ->d.numeroDescargas()>0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDescargas));
        System.out.println("Cantidad media de Descargas: "+est.getAverage());
        System.out.println("Cantidad maxima de Descargas: "+est.getMax());
        System.out.println("Cantidad minima de Descargas: "+est.getMin());
        System.out.println("Cantidad registros evaluados: "+est.getCount());



    }
}
