package utilidades;

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
        
    }
}
