package utilidades;

import java.util.ArrayList;
import java.util.List;
import modelos.ErrorLexico;
import modelos.Token;

public class GeneradorReportes {
    private ManejadorArchivos manejador;
    
    private final String CSS_BASE = "<style>"
            + "body { font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f7f6; color: #333; margin: 0; padding: 30px; }"
            + ".container { max-width: 900px; margin: 0 auto; background: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.08); }"
            + "h2 { text-align: center; color: #2c3e50; margin-bottom: 25px; text-transform: uppercase; letter-spacing: 1.5px; font-size: 24px; border-bottom: 2px solid #ecf0f1; padding-bottom: 15px; }"
            + "table { width: 100%; border-collapse: collapse; margin-top: 15px; border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0,0,0,0.05); }"
            + "th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eeeeee; }"
            + "th { color: white; text-transform: uppercase; font-size: 14px; font-weight: 600; letter-spacing: 0.5px; }"
            + "tr:hover { background-color: #f8f9fa; transition: background-color 0.3s ease; }"
            + ".text-center { text-align: center; }"
            + "</style>";

    public GeneradorReportes() {
        this.manejador = new ManejadorArchivos();
    }
    
    public void generarReporteTokensHTML(List<Token> tokens) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Tokens</title>");
        html.append(CSS_BASE);
        html.append("<style>th { background-color: #27ae60; }</style>"); // Verde 
        html.append("</head><body><div class='container'>");
        
        html.append("<h2>Reporte de Tokens - PromptZal</h2>");
        html.append("<table>");
        html.append("<tr><th class='text-center'>#</th><th>Lexema</th><th>Tipo</th><th class='text-center'>Fila</th><th class='text-center'>Columna</th></tr>");

        for (Token t : tokens) {
            html.append("<tr>")
                .append("<td class='text-center'><strong>").append(t.getId()).append("</strong></td>")
                .append("<td><code style='color: #d35400; background: #fdf2e9; padding: 2px 6px; border-radius: 4px;'>").append(t.getLexema()).append("</code></td>")
                .append("<td>").append(t.getTipo()).append("</td>")
                .append("<td class='text-center'>").append(t.getFila()).append("</td>")
                .append("<td class='text-center'>").append(t.getColumna()).append("</td>")
                .append("</tr>");
        }
        
        html.append("</table></div></body></html>");

        manejador.guardarArchivo("reporte_tokens.html", html.toString());
    }

    public void generarReporteErroresHTML(List<ErrorLexico> errores) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Errores</title>");
        html.append(CSS_BASE);
        html.append("<style>th { background-color: #e74c3c; }</style>"); // Rojo 
        html.append("</head><body><div class='container'>");
        
        html.append("<h2>Reporte de Errores Léxicos - PromptZal</h2>");
        
        if (errores.isEmpty()) {
            html.append("<div style='text-align:center; padding: 40px; background: #eafaf1; border-radius: 8px; border: 1px solid #a3e4d7;'>");
            html.append("<h3 style='color: #27ae60; margin: 0;'>✓ No se encontraron errores léxicos en el archivo.</h3>");
            html.append("</div>");
        } else {
            html.append("<table>");
            html.append("<tr><th class='text-center'>#</th><th>Carácter/Lexema</th><th>Descripción</th><th class='text-center'>Fila</th><th class='text-center'>Columna</th></tr>");

            for (ErrorLexico e : errores) {
                html.append("<tr>")
                    .append("<td class='text-center'><strong>").append(e.getId()).append("</strong></td>")
                    .append("<td><code style='color: #c0392b; background: #fadbd8; padding: 2px 6px; border-radius: 4px;'>").append(e.getLexema()).append("</code></td>")
                    .append("<td>").append(e.getDescripcion()).append("</td>")
                    .append("<td class='text-center'>").append(e.getFila()).append("</td>")
                    .append("<td class='text-center'>").append(e.getColumna()).append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");
        }
        
        html.append("</div></body></html>");

        manejador.guardarArchivo("reporte_errores.html", html.toString());
    }
    
    public void generarReporteEstadisticasHTML(List<Token> tokens, List<ErrorLexico> errores) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'><title>Reporte de Estadísticas</title>");
        html.append(CSS_BASE);
        html.append("<style>");
        html.append("th { background-color: #2980b9; }"); // Azul 
        html.append(".stats-grid { display: flex; justify-content: space-between; gap: 20px; margin-bottom: 30px; }");
        html.append(".card { flex: 1; background: #fff; padding: 20px; border-radius: 8px; text-align: center; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #e0e0e0; border-top: 4px solid #2980b9; }");
        html.append(".card h3 { margin: 0; color: #7f8c8d; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }");
        html.append(".card p { margin: 10px 0 0; font-size: 32px; font-weight: bold; color: #2c3e50; }");
        html.append("</style>");
        html.append("</head><body><div class='container'>");
        
        html.append("<h2>Estadísticas de Análisis - PromptZal</h2>");
        
        int totalTokens = tokens.size();
        int totalErrores = errores.size();
        int totalLineas = 1;
        
        for (Token t : tokens) {
            if (t.getFila() > totalLineas) totalLineas = t.getFila();
        }
        for (ErrorLexico e : errores) {
            if (e.getFila() > totalLineas) totalLineas = e.getFila();
        }


        html.append("<div class='stats-grid'>");
        html.append("<div class='card'><h3>Total de Tokens</h3><p style='color:#27ae60;'>").append(totalTokens).append("</p></div>");
        html.append("<div class='card'><h3>Total de Errores</h3><p style='color:#e74c3c;'>").append(totalErrores).append("</p></div>");
        html.append("<div class='card'><h3>Líneas Analizadas</h3><p style='color:#f39c12;'>").append(totalLineas).append("</p></div>");
        html.append("</div>");

        if (totalTokens > 0) {
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
            
            html.append("<h3 style='color: #34495e; border-bottom: 2px solid #ecf0f1; padding-bottom: 10px; margin-top: 30px;'>Frecuencia de Tokens</h3>");
            html.append("<table>");
            html.append("<tr><th>Tipo de Token</th><th class='text-center'>Cantidad (Frecuencia)</th></tr>");
            
            for (int i = 0; i < tipos.size(); i++) {
                html.append("<tr>")
                    .append("<td><strong>").append(tipos.get(i)).append("</strong></td>")
                    .append("<td class='text-center'><span style='background: #ebedef; padding: 4px 12px; border-radius: 12px; font-weight: bold;'>").append(cantidades.get(i)).append("</span></td>")
                    .append("</tr>");
            }
            html.append("</table>");
        }
        
        html.append("</div></body></html>");
        manejador.guardarArchivo("reporte_estadisticas.html", html.toString());
    }
}