package promptzal;

import utilidades.ManejadorArchivos;
import analizador.AnalizadorLexico;
import modelos.ErrorLexico;
import modelos.Token;
import java.util.Scanner;

public class Promptzal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ManejadorArchivos manejador = new ManejadorArchivos();

        System.out.println("=========================================");
        System.out.println("   ANALIZADOR LÉXICO - PROMPTZAL");
        System.out.println("=========================================");
        
        System.out.print("Ingrese la ruta del archivo .pz: ");
        String rutaArchivo = scanner.nextLine();

        // limpia comillas por si el usuario arrastra el archivo a la consola
        rutaArchivo = rutaArchivo.replace("\"", "");

        String codigoFuente = manejador.leerArchivo(rutaArchivo);

        if (codigoFuente != null) {
            System.out.println("\nArchivo cargado correctamente. Iniciando análisis...\n");
            
            AnalizadorLexico analizador = new AnalizadorLexico(codigoFuente);
            analizador.analizar();

            System.out.println("----- TOKENS RECONOCIDOS -----");
            for (Token t : analizador.getTokens()) {
                System.out.println(t.obtenerDetalles());
            }

            System.out.println("\n----- ERRORES ENCONTRADOS -----");
            if (analizador.getErrores().isEmpty()) {
                System.out.println("No se detectaron errores léxicos.");
            } else {
                for (ErrorLexico e : analizador.getErrores()) {
                    System.out.println(e.obtenerDetalles());
                }
            }

            System.out.println("\nGenerando reportes HTML...");
            manejador.generarReporteTokensHTML(analizador.getTokens());
            manejador.generarReporteErroresHTML(analizador.getErrores());
            
            System.out.println("\nAnálisis finalizado con éxito");
        }
        
        scanner.close();
    }
}
