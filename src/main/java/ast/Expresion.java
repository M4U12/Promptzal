package ast;

import java.util.List;

public class Expresion {

    private List<Termino> terminos;

    public Expresion(List<Termino> terminos) {
        this.terminos = terminos;
    }

    public List<Termino> getTerminos() {
        return terminos;
    }
}
