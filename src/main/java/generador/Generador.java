package generador;

import ast.*;
import java.util.HashMap;
import java.util.Map;

public class Generador {
    private Programa programaAST;

    public Generador(Programa programaAST) {
        this.programaAST = programaAST;
    }

    public Map<String, String> generar() {
        Map<String, String> promptsGenerados = new HashMap<>();
        if (programaAST == null) return promptsGenerados;

        //encabezado global (Directivas)
        StringBuilder encabezado = new StringBuilder();
        encabezado.append("# Prompt generado con PromptZal\n");
        for (Directiva d : programaAST.getDirectivas()) {
            String nombreDir = d.getNombre().replace("@", "");
            nombreDir = nombreDir.substring(0, 1).toUpperCase() + nombreDir.substring(1);
            encabezado.append(nombreDir).append(": ").append(d.getValor().replace("\"", "")).append("\n");
        }
        encabezado.append("\n");

        //prompt solo para los agentes en el bloque EJECUTAR
        for (String nombreEjecucion : programaAST.getEjecuciones()) {
            Agente agenteTarget = buscarAgente(nombreEjecucion);
            
            if (agenteTarget != null) {
                StringBuilder prompt = new StringBuilder();
                prompt.append(encabezado.toString());
                
                prompt.append("## Agente: ").append(agenteTarget.getNombre()).append("\n");
                prompt.append("Contexto: ").append(agenteTarget.getContexto().replace("\"", "")).append("\n");

                // Variables 
                if (!agenteTarget.getVariables().isEmpty()) {
                    prompt.append("Variables disponibles:\n");
                    for (Variable var : agenteTarget.getVariables()) {
                        prompt.append("- ").append(var.getNombre()).append(": ")
                              .append(evaluarExpresion(var.getValorExpresion(), false)).append("\n");
                    }
                }

                // Comandos 
                if (!agenteTarget.getComandos().isEmpty()) {
                    prompt.append("Instrucciones:\n");
                    int paso = 1;
                    for (Comando cmd : agenteTarget.getComandos()) {
                        prompt.append(paso).append(". ");
                        prompt.append(traducirComandoALenguajeNatural(cmd, agenteTarget));
                        prompt.append(" Guarda el resultado como ").append(cmd.getVariableDestino()).append(".\n");
                        paso++;
                    }
                }
                
                // Resultados Solicitados (Exportar)
                if (!programaAST.getExportaciones().isEmpty()) {
                    prompt.append("\n## Resultado solicitado\n");
                    prompt.append("Entrega únicamente el contenido de las siguientes variables:\n");
                    for (String exp : programaAST.getExportaciones()) {
                        prompt.append("- ").append(exp).append("\n");
                    }
                }

                promptsGenerados.put(agenteTarget.getNombre(), prompt.toString());
            }
        }

        return promptsGenerados;
    }

    private String traducirComandoALenguajeNatural(Comando cmd, Agente agenteActual) {
        boolean esGenerar = cmd.getTipoInstruccion().equals("GENERAR");
        // manda la bandera 'esGenerar' para que aplique la excepción
        String base = evaluarExpresion(cmd.getExpresionBase(), esGenerar);
        
        String sobre = "", desde = "", en = "", como = "";
        for (Conector c : cmd.getConectores()) {
            if (c.getTipoConector().equals("SOBRE")) sobre = evaluarExpresion(c.getExpresion(), false);
            else if (c.getTipoConector().equals("DESDE")) desde = evaluarExpresion(c.getExpresion(), false);
            else if (c.getTipoConector().equals("EN")) en = evaluarExpresion(c.getExpresion(), false);
            else if (c.getTipoConector().equals("COMO")) como = evaluarExpresion(c.getExpresion(), false);
        }

        StringBuilder trad = new StringBuilder();
        switch (cmd.getTipoInstruccion()) {
            case "PREGUNTAR":
                trad.append("Responde a la pregunta: \"").append(base).append("\"");
                if (!sobre.isEmpty()) trad.append(" basándote en la información de ").append(sobre).append(".");
                break;
            case "ANALIZAR":
                trad.append("Analiza ").append(base);
                if (!sobre.isEmpty()) trad.append(" enfocándote en ").append(sobre).append(".");
                break;
            case "GENERAR":
                trad.append("Genera ").append(base);
                if (!desde.isEmpty()) trad.append(" a partir de ").append(desde).append(".");
                if (!como.isEmpty()) trad.append(" Entrega el resultado en formato ").append(como).append(".");
                break;
            case "RESUMIR":
                trad.append("Resume ").append(base);
                if (!en.isEmpty()) {
                    String limiteReal = resolverValorVariable(en, agenteActual);
                    trad.append(" en un máximo de ").append(limiteReal).append(".");
                }
                break;
            case "CLASIFICAR":
                trad.append("Clasifica ").append(base);
                if (!en.isEmpty()) trad.append(" en las siguientes categorías: ").append(en).append(".");
                break;
            case "EXTRAER":
                trad.append("Extrae ").append(base);
                if (!desde.isEmpty()) trad.append(" de la información provista en ").append(desde).append(".");
                break;
            case "TRADUCIR":
                trad.append("Traduce ").append(base);
                if (!en.isEmpty()) trad.append(" al idioma ").append(en).append(".");
                break;
            default:
                trad.append(cmd.getTipoInstruccion()).append(" ").append(base).append(".");
        }
        return trad.toString().replaceAll("\\s+", " ");
    }

    private String evaluarExpresion(Expresion expr, boolean esGenerarBase) {
        if (expr == null || expr.getTerminos().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < expr.getTerminos().size(); i++) {
            Termino t = expr.getTerminos().get(i);
            
            if (t.getTipo().equals("CADENA")) {
                sb.append(t.getValor().replace("\"", ""));
            } 
            else if (t.getTipo().equals("NUMERO")) {
                sb.append(t.getValor());
                if (t.getUnidad() != null && !t.getUnidad().isEmpty()) {
                    sb.append(" ").append(t.getUnidad());
                }
            } 
            else if (t.getTipo().equals("ID")) {
                // Excepción del GENERAR
                if (esGenerarBase && i == 0) {
                    sb.append(t.getValor()); // Se inyecta como texto plano 
                } else {
                    sb.append(t.getValor()); 
                }
            }
            else if (t.getTipo().equals("CARGAR")) {
                // Se reemplazan las comillas internas que trae para que no queden dobles
                String archivo = t.getValor().replace("CARGAR(", "").replace(")", "").replace("\"", "");
                sb.append("el contenido del archivo \"").append(archivo).append("\" (adjuntar el archivo)");
            }

            if (i < expr.getTerminos().size() - 1) {
                sb.append(" ");
            }
        }
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
    
    // exportar a .txt 
    public void exportarArchivos(Map<String, String> promptsGenerados) {
        if (programaAST.getExportaciones().isEmpty()) return;

        for (String nombreExportar : programaAST.getExportaciones()) {
            if (promptsGenerados.containsKey(nombreExportar)) {
                String contenido = promptsGenerados.get(nombreExportar);
                String nombreArchivo = nombreExportar + ".txt";

                try (java.io.FileWriter writer = new java.io.FileWriter(nombreArchivo)) {
                    writer.write(contenido);
                    System.out.println("ÉXITO: Se exportó el archivo '" + nombreArchivo + "' correctamente.");
                } catch (java.io.IOException e) {
                    System.out.println("ERROR: No se pudo crear el archivo " + nombreArchivo);
                }
            }
        }
    }
    
    private String resolverValorVariable(String posibleVariable, Agente agente) {
        if (agente == null) return posibleVariable;
        for (Variable var : agente.getVariables()) {
            if (var.getNombre().equals(posibleVariable)) {
                if (!var.getValorExpresion().getTerminos().isEmpty()) {
                    // Devuelve el valor numérico limpio de la variable
                    return var.getValorExpresion().getTerminos().get(0).getValor();
                }
            }
        }
        return posibleVariable; 
    }
}