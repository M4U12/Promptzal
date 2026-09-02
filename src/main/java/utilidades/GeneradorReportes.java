package utilidades;

import java.util.ArrayList;
import java.util.List;
import modelos.ErrorLexico;
import modelos.Token;

public class GeneradorReportes {
    private ManejadorArchivos manejador;
    
    public GeneradorReportes() {
        this.manejador = new ManejadorArchivos();
    }
    
    public void generarReporteTokensHTML(List<Token> tokens) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Reporte de Tokens</title>");
        html.append("<style>table {width: 80%; border-collapse: collapse; margin: 20px auto;} ");
        html.append("th, td {border: 1px solid black; padding: 8px; text-align: center;} ");
        html.append("th {background-color: #4CAF50; color: white;}</style></head><body>");
        
        html.append("<h2 style='text-align:center;'>Reporte de Tokens - PromptZal</h2>");
        html.append("<table>");
        html.append("<tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

        for (Token t : tokens) {
            html.append("<tr>")
                .append("<td>").append(t.getId()).append("</td>")
                .append("<td>").append(t.getLexema()).append("</td>")
                .append("<td>").append(t.getTipo()).append("</td>")
                .append("<td>").append(t.getFila()).append("</td>")
                .append("<td>").append(t.getColumna()).append("</td>")
                .append("</tr>");
        }
        
        html.append("</table></body></html>");

        manejador.guardarArchivo("reporte_tokens.html", html.toString());
    }

    public void generarReporteErroresHTML(List<ErrorLexico> errores) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Reporte de Errores</title>");
        html.append("<style>table {width: 80%; border-collapse: collapse; margin: 20px auto;} ");
        html.append("th, td {border: 1px solid black; padding: 8px; text-align: center;} ");
        html.append("th {background-color: #f44336; color: white;}</style></head><body>");
        
        html.append("<h2 style='text-align:center;'>Reporte de Errores Léxicos - PromptZal</h2>");
        
        if (errores.isEmpty()) {
            html.append("<h3 style='text-align:center; color: green;'>¡Felicidades! No se encontraron errores léxicos en el archivo.</h3>");
        } else {
            html.append("<table>");
            html.append("<tr><th>#</th><th>Carácter/Lexema</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>");

            for (ErrorLexico e : errores) {
                html.append("<tr>")
                    .append("<td>").append(e.getId()).append("</td>")
                    .append("<td>").append(e.getLexema()).append("</td>")
                    .append("<td>").append(e.getDescripcion()).append("</td>")
                    .append("<td>").append(e.getFila()).append("</td>")
                    .append("<td>").append(e.getColumna()).append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
        }
        
        html.append("</body></html>");

        manejador.guardarArchivo("reporte_errores.html", html.toString());
    }
    
    public void generarReporteEstadisticasHTML(List<Token> tokens, List<ErrorLexico> errores) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Reporte de Estadísticas</title>");
        html.append("<style>body { font-family: Arial, sans-serif; text-align: center; } ");
        html.append("table {width: 50%; border-collapse: collapse; margin: 20px auto;} ");
        html.append("th, td {border: 1px solid black; padding: 8px;} ");
        html.append("th {background-color: #2196F3; color: white;}</style></head><body>");
        
        html.append("<h2>Reporte de Estadísticas - PromptZal</h2>");
        
        int totalTokens = tokens.size();
        int totalErrores = errores.size();
        int totalLineas = 1;
        
        // calcular la línea máxima analizada
        for (Token t : tokens) {
            if (t.getFila() > totalLineas) totalLineas = t.getFila();
        }
        for (ErrorLexico e : errores) {
            if (e.getFila() > totalLineas) totalLineas = e.getFila();
        }

        html.append("<div style='margin: 20px; font-size: 16px;'>");
        html.append("<p><b>Total de Tokens:</b> ").append(totalTokens).append("</p>");
        html.append("<p><b>Total de Errores Léxicos:</b> ").append(totalErrores).append("</p>");
        html.append("<p><b>Total de Líneas Analizadas:</b> ").append(totalLineas).append("</p>");
        html.append("</div>");

        // frecuencia de tokens usando listas paralelas
        if (totalTokens > 0) {
            List<String> tipos = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();

            for (Token t : tokens) {
                String tipoActual = t.getTipo();
                int indice = tipos.indexOf(tipoActual);

                if (indice == -1) {
                    // si el tipo no existe en la lista, lo agrega con cantidad 1
                    tipos.add(tipoActual);
                    cantidades.add(1);
                } else {
                    // si ya existe, se obtiene su cantidad actual, le suma 1 y la actualiza
                    int cantidadActual = cantidades.get(indice);
                    cantidades.set(indice, cantidadActual + 1);
                }
            }
            
            html.append("<table>");
            html.append("<tr><th>Tipo de Token</th><th>Cantidad (Frecuencia)</th></tr>");
            
            for (int i = 0; i < tipos.size(); i++) {
                html.append("<tr>")
                    .append("<td>").append(tipos.get(i)).append("</td>")
                    .append("<td>").append(cantidades.get(i)).append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
        }
        
        html.append("</body></html>");
        manejador.guardarArchivo("reporte_estadisticas.html", html.toString());
    }
}
