package utilidades;

public class GraficadorAFD {

    private ManejadorArchivos manejador;

    public GraficadorAFD() {
        this.manejador = new ManejadorArchivos();
    }

    public void generarGrafo() {
        String codigoDOT = construirCodigoDOT();
        manejador.guardarArchivo("automata_promptzal.dot", codigoDOT);
        try {
            ProcessBuilder builder = new ProcessBuilder("dot", "-Tpng", "automata_promptzal.dot", "-o", "grafo_afd.png");
            builder.start();
            System.out.println("Imagen del autómata generada exitosamente (grafo_afd.png).");
        } catch (Exception e) {
            System.out.println("Error al generar la imagen. Verifica que Graphviz esté instalado. Detalles: " + e.getMessage());
        }
    }

    private String construirCodigoDOT() {
        StringBuilder dot = new StringBuilder();

        dot.append("digraph AFD {\n");
        dot.append("    rankdir=LR;\n");
        
        // formas y los estados de aceptación
        dot.append("    node [shape = doublecircle, fontname=\"Helvetica\"]; S1; S2; S3; S5; S12; S13; S14;\n");
        
        // estado trampa
        dot.append("    node [shape = circle, style=filled, fillcolor=red, fontcolor=white, fontname=\"Helvetica\"]; ERROR;\n");
        
        // círculo normal transparente para los demás
        dot.append("    node [shape = circle, style=\"\", fontcolor=black, fontname=\"Helvetica\"];\n"); 
        dot.append("    edge [fontname=\"Helvetica\", fontsize=10];\n\n");

        // punto de inicio
        dot.append("    inicio [shape=point];\n");
        dot.append("    inicio -> S0;\n\n");

        // transiciones normales
        dot.append("    S0 -> S1 [label=\" letra\"];\n");
        dot.append("    S0 -> S2 [label=\" '@'\"];\n");
        dot.append("    S0 -> S3 [label=\" dígito\"];\n");
        dot.append("    S0 -> S6 [label=\" \\\" \"];\n");
        dot.append("    S0 -> S7 [label=\" '/'\"];\n");
        dot.append("    S0 -> S11 [label=\" '-'\"];\n");
        dot.append("    S0 -> S14 [label=\" delimitador \\n operador\"];\n");
        dot.append("    S0 -> S0 [label=\" (espacios)\"];\n\n");

        dot.append("    S1 -> S1 [label=\" letra | dígito\"];\n");
        dot.append("    S2 -> S2 [label=\" letra\"];\n");
        dot.append("    S3 -> S3 [label=\" dígito\"];\n");
        dot.append("    S3 -> S4 [label=\" '.'\"];\n");
        dot.append("    S4 -> S5 [label=\" dígito\"];\n");
        dot.append("    S5 -> S5 [label=\" dígito\"];\n\n");

        dot.append("    S6 -> S6 [label=\" distinto de \\\" \"];\n");
        dot.append("    S6 -> S12 [label=\" \\\" \"];\n\n");

        dot.append("    S7 -> S8 [label=\" '/'\"];\n");
        dot.append("    S7 -> S9 [label=\" '*'\"];\n");
        dot.append("    S8 -> S8 [label=\" distinto de \\\\n\"];\n");
        dot.append("    S8 -> S0 [label=\" \\\\n\"];\n");
        dot.append("    S9 -> S9 [label=\" distinto de '*'\"];\n");
        dot.append("    S9 -> S10 [label=\" '*'\"];\n");
        dot.append("    S10 -> S9 [label=\" distinto de '/' y '*'\"];\n");
        dot.append("    S10 -> S10 [label=\" '*'\"];\n");
        dot.append("    S10 -> S0 [label=\" '/'\"];\n\n");

        dot.append("    S11 -> S13 [label=\" '>'\"];\n\n");

        // transiciones explícitas hacia el estado trampa 
        dot.append("    S0 -> ERROR [label=\" no válido\"];\n");
        dot.append("    S4 -> ERROR [label=\" distinto de dígito\"];\n");
        dot.append("    S7 -> ERROR [label=\" distinto de '/' o '*'\"];\n");
        dot.append("    S11 -> ERROR [label=\" distinto de '>'\"];\n");

        dot.append("}\n");
        return dot.toString();
    }
}
