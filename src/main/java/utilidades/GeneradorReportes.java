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
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Tokens</title></head><body>");
        
        html.append("<h2>Reporte de Tokens</h2>");
        html.append("<table border='1'>");
        html.append("<tr><th>ID</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

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
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Errores</title></head><body>");
        
        html.append("<h2>Reporte de Errores Léxicos</h2>");
        
        if (errores.isEmpty()) {
            html.append("<p>No se encontraron errores léxicos.</p>");
        } else {
            html.append("<table border='1'>");
            html.append("<tr><th>ID</th><th>Carácter/Lexema</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>");

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
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Estadísticas</title></head><body>");
        
        html.append("<h2>Estadísticas de Análisis</h2>");
        
        html.append("<ul>");
        html.append("<li>Total de Tokens: ").append(tokens.size()).append("</li>");
        html.append("<li>Total de Errores: ").append(errores.size()).append("</li>");
        html.append("</ul>");

        if (!tokens.isEmpty()) {
            List<String> tipos = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();

            for (Token t : tokens) {
                String tipoActual = t.getTipo();
                int indice = tipos.indexOf(tipoActual);

                if (indice == -1) {
                    tipos.add(tipoActual);
                    cantidades.add(1);
                } else {
                    cantidades.set(indice, cantidades.get(indice) + 1);
                }
            }
            
            html.append("<h3>Frecuencia de Tokens</h3>");
            html.append("<table border='1'>");
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