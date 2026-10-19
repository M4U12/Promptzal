/*package utilidades;

import analizador.AnalizadorLexico;
import java.util.ArrayList;
import java.util.List;

public class GraficadorAFD {

    private ManejadorArchivos manejador;
    private List<Transicion> transiciones;
    private List<String> estadosAceptacion;

    public GraficadorAFD() {
        this.manejador = new ManejadorArchivos();
        this.transiciones = new ArrayList<>();
        this.estadosAceptacion = new ArrayList<>();
        configurarAutomata();
    }

    private void configurarAutomata() {
        estadosAceptacion.add("S1");
        estadosAceptacion.add("S2");
        estadosAceptacion.add("S3");
        estadosAceptacion.add("S5");
        estadosAceptacion.add("S12");
        estadosAceptacion.add("S13");
        estadosAceptacion.add("S14");

        transiciones.add(new Transicion("S0", "S1", " letra"));
        transiciones.add(new Transicion("S0", "S2", " '@'"));
        transiciones.add(new Transicion("S0", "S3", " dígito"));
        transiciones.add(new Transicion("S0", "S6", " \\\" "));
        transiciones.add(new Transicion("S0", "S7", " '/'"));
        transiciones.add(new Transicion("S0", "S11", " '-'"));

        // S14 lee dinámicamente los delimitadores y operadores
        transiciones.add(new Transicion("S0", "S14", " Delimitadores: " + AnalizadorLexico.DELIMITADORES_VALIDOS + "\\n Operadores: " + AnalizadorLexico.OPERADORES_VALIDOS));

        transiciones.add(new Transicion("S0", "S0", " (espacios)"));

        transiciones.add(new Transicion("S1", "S1", " letra | dígito"));
        transiciones.add(new Transicion("S2", "S2", " letra"));
        transiciones.add(new Transicion("S3", "S3", " dígito"));
        transiciones.add(new Transicion("S3", "S4", " '.'"));
        transiciones.add(new Transicion("S4", "S5", " dígito"));
        transiciones.add(new Transicion("S5", "S5", " dígito"));

        transiciones.add(new Transicion("S6", "S6", " distinto de \\\" "));
        transiciones.add(new Transicion("S6", "S12", " \\\" "));

        transiciones.add(new Transicion("S7", "S8", " '/'"));
        transiciones.add(new Transicion("S7", "S9", " '*'"));
        transiciones.add(new Transicion("S8", "S8", " distinto de \\\\n"));
        transiciones.add(new Transicion("S8", "S0", " \\\\n"));
        transiciones.add(new Transicion("S9", "S9", " distinto de '*'"));
        transiciones.add(new Transicion("S9", "S10", " '*'"));
        transiciones.add(new Transicion("S10", "S9", " distinto de '/' y '*'"));
        transiciones.add(new Transicion("S10", "S10", " '*'"));
        transiciones.add(new Transicion("S10", "S0", " '/'"));

        transiciones.add(new Transicion("S11", "S13", " '>'"));

        transiciones.add(new Transicion("S0", "ERROR", " no válido"));
        transiciones.add(new Transicion("S4", "ERROR", " distinto de dígito"));
        transiciones.add(new Transicion("S7", "ERROR", " distinto de '/' o '*'"));
        transiciones.add(new Transicion("S11", "ERROR", " distinto de '>'"));
    }

    public void generarGrafo() {
        String codigoDOT = construirCodigoDOT();
        manejador.guardarArchivo("automata_promptzal.dot", codigoDOT);
        try {
            ProcessBuilder builder = new ProcessBuilder("dot", "-Tpng", "automata_promptzal.dot", "-o", "grafo_afd.png");
            Process process = builder.start();
            process.waitFor();
            System.out.println("Imagen del autómata generada exitosamente (grafo_afd.png).");
        } catch (Exception e) {
            System.out.println("Error al generar la imagen. Detalles: " + e.getMessage());
        }
    }

    private String construirCodigoDOT() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph AFD {\n");
        dot.append("    rankdir=LR;\n");

        // 1. Generar la Leyenda Dinámica leyendo del Lexer
        String contenidoLeyenda = "Tabla de Símbolos (Validados post-estado 1 y 2):\\n\\n"
                + "Directivas: " + String.join(", ", AnalizadorLexico.DIRECTIVAS_VALIDAS) + "\\n"
                + "Reservadas: " + String.join(", ", AnalizadorLexico.PALABRAS_RESERVADAS) + "\\n"
                + "Comandos IA: " + String.join(", ", AnalizadorLexico.COMANDOS_IA) + "\\n"
                + "Funciones: " + String.join(", ", AnalizadorLexico.FUNCIONES) + "\\n"
                + "Conectores: " + String.join(", ", AnalizadorLexico.CONECTORES);

        dot.append("    leyenda [shape=note, label=\"").append(contenidoLeyenda).append("\", fontname=\"Helvetica\", fontsize=10, style=filled, fillcolor=lightyellow];\n\n");

        // 2. Nodos normales
        dot.append("    node [shape = doublecircle, fontname=\"Helvetica\"]; ");
        for (String estado : estadosAceptacion) {
            dot.append(estado).append("; ");
        }
        dot.append("\n");

        dot.append("    node [shape = circle, style=filled, fillcolor=red, fontcolor=white, fontname=\"Helvetica\"]; ERROR;\n");
        dot.append("    node [shape = circle, style=\"\", fontcolor=black, fontname=\"Helvetica\"];\n");
        dot.append("    edge [fontname=\"Helvetica\", fontsize=10];\n\n");

        dot.append("    inicio [shape=point];\n");
        dot.append("    inicio -> S0;\n\n");

        // 3. Flechas
        for (Transicion t : transiciones) {
            dot.append("    ")
                    .append(t.origen)
                    .append(" -> ")
                    .append(t.destino)
                    .append(" [label=\" ")
                    .append(t.etiqueta)
                    .append("\"];\n");
        }

        dot.append("}\n");
        return dot.toString();
    }
}
*/