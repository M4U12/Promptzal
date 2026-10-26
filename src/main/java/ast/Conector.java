package ast;

public class Conector {

    private String tipoConector; // Ej: "SOBRE", "EN", "DESDE"
    private String expresion;    // Ej: "ventas"

    public Conector(String tipoConector, String expresion) {
        this.tipoConector = tipoConector;
        this.expresion = expresion;
    }

    public String getTipoConector() {
        return tipoConector;
    }

    public String getExpresion() {
        return expresion;
    }
}
