package utilidades;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ManejadorArchivos {
    
    public String leerArchivo(String ruta) {
        if (!ruta.endsWith(".pz")) {
            System.out.println("Archivo inválido. El analizador solo acepta archivos '.pz'.");
            return null;
        }
        try {
            return new String(Files.readAllBytes(Paths.get(ruta)));
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return null;
        }
    }

    public void guardarArchivo(String nombreArchivo, String contenido) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(contenido);
            System.out.println("Se generó exitosamente el archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el reporte HTML: " + e.getMessage());
        }
    }
}
