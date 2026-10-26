package ast;

public class Conector {

    private String tipoConector;
    private Expresion expresion;

    public Conector(String tipoConector, Expresion expresion) {
        this.tipoConector = tipoConector;
        this.expresion = expresion;
    }

    public String getTipoConector() {
        return tipoConector;
    }

    public Expresion getExpresion() {
        return expresion;
    }
}
