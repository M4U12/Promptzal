package generador;

import ast.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Generador {

    private Programa programaAST;

    public Generador(Programa programaAST) {
        this.programaAST = programaAST;
    }

    /**
     Clave (Key) = Nombre del agente ejecutado Valor
     (Value) = El texto final del prompt generado para ese agente
     */
    public Map<String, String> generar() {
        Map<String, String> promptsGenerados = new HashMap<>();
        if (programaAST == null) {
            return promptsGenerados;
        }

        // encabezado de directivas (se repite en todos los prompts)
        String encabezadoDirectivas = generarDirectivas();

        // prompt solo para los agentes en el bloque EJECUTAR
        for (String nombreEjecucion : programaAST.getEjecuciones()) {
            Agente agenteTarget = buscarAgente(nombreEjecucion);

            if (agenteTarget != null) {
                StringBuilder prompt = new StringBuilder();
                prompt.append(encabezadoDirectivas);
                prompt.append("=== INICIO DEL PROMPT PARA EL AGENTE '").append(agenteTarget.getNombre().toUpperCase()).append("' ===\n\n");

                // Contexto
                prompt.append("[CONTEXTO Y ROL]\n");
                prompt.append("Actúa como: ").append(agenteTarget.getContexto().replace("\"", "")).append("\n\n");

                // Variables (Datos de referencia)
                if (!agenteTarget.getVariables().isEmpty()) {
                    prompt.append("[DATOS DE REFERENCIA]\n");
                    prompt.append("Considera la siguiente información para tus respuestas:\n");
                    for (Variable var : agenteTarget.getVariables()) {
                        prompt.append("- ").append(var.getNombre()).append(": ")
                                .append(evaluarExpresion(var.getValorExpresion())).append("\n");
                    }
                    prompt.append("\n");
                }

                //Comandos 
                if (!agenteTarget.getComandos().isEmpty()) {
                    prompt.append("[INSTRUCCIONES A EJECUTAR]\n");
                    int paso = 1;
                    for (Comando cmd : agenteTarget.getComandos()) {
                        prompt.append(paso).append(". Acción: ").append(cmd.getTipoInstruccion()).append("\n");
                        prompt.append("   Objetivo: ").append(evaluarExpresion(cmd.getExpresionBase())).append("\n");

                        // Procesa los conectores (SOBRE, EN, DESDE, etc.)
                        for (Conector con : cmd.getConectores()) {
                            prompt.append("   - ").append(con.getTipoConector()).append(" ")
                                    .append(evaluarExpresion(con.getExpresion())).append("\n");
                        }
                        prompt.append("   (Guarda el resultado de este paso bajo el nombre: '").append(cmd.getVariableDestino()).append("')\n\n");
                        paso++;
                    }
                }

                prompt.append("=== FIN DEL PROMPT ===\n");
                promptsGenerados.put(agenteTarget.getNombre(), prompt.toString());
            }
        }

        return promptsGenerados;
    }
    
    // crea los archivos físicos .txt
    public void exportarArchivos(Map<String, String> promptsGenerados) {
        if (programaAST.getExportaciones().isEmpty()) return;

        for (String nombreExportar : programaAST.getExportaciones()) {
            // busca si el agente que pidieron exportar realmente fue generado
            if (promptsGenerados.containsKey(nombreExportar)) {
                String contenido = promptsGenerados.get(nombreExportar);
                String nombreArchivo = nombreExportar + ".txt";

                try (FileWriter writer = new FileWriter(nombreArchivo)) {
                    writer.write(contenido);
                    System.out.println("ÉXITO: Se exportó el archivo '" + nombreArchivo + "' correctamente.");
                } catch (IOException e) {
                    System.out.println("ERROR: No se pudo crear el archivo " + nombreArchivo);
                }
            }
        }
    }

    private String generarDirectivas() {
        if (programaAST.getDirectivas().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[CONFIGURACIÓN DEL MODELO]\n");
        for (Directiva d : programaAST.getDirectivas()) {
            sb.append(d.getNombre()).append(": ").append(d.getValor()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private Agente buscarAgente(String nombre) {
        for (Agente ag : programaAST.getAgentes()) {
            if (ag.getNombre().equals(nombre)) {
                return ag;
            }
        }
        return null;
    }

    // Traduce la lista de términos a texto natural legible
    private String evaluarExpresion(Expresion expr) {
        if (expr == null || expr.getTerminos().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < expr.getTerminos().size(); i++) {
            Termino t = expr.getTerminos().get(i);

            if (t.getTipo().equals("CADENA")) {
                // Le quita las comillas 
                sb.append(t.getValor().replace("\"", ""));
            } else if (t.getTipo().equals("NUMERO")) {
                sb.append(t.getValor());
                if (t.getUnidad() != null && !t.getUnidad().isEmpty()) {
                    sb.append(" ").append(t.getUnidad());
                }
            } else if (t.getTipo().equals("ID")) {
                sb.append("{variable: ").append(t.getValor()).append("}");
            } else if (t.getTipo().equals("CARGAR")) {
                sb.append("[Cargar contenido del archivo: ").append(t.getValor()).append("]");
            }

            // Agrega un espacio para separar los términos si se usó el signo '+'
            if (i < expr.getTerminos().size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
