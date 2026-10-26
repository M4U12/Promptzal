package ast;

import java.util.List;

public class Comando {

    private String tipoInstruccion; // Ej: "PREGUNTAR", "RESUMIR"
    private String expresionBase;   // Ej: "Cuales son las tendencias"
    private List<Conector> conectores;
    private String variableDestino; // Ej: "tendencias" (lo que va después de la flecha ->)

    public Comando(String tipoInstruccion, String expresionBase, List<Conector> conectores, String variableDestino) {
        this.tipoInstruccion = tipoInstruccion;
        this.expresionBase = expresionBase;
        this.conectores = conectores;
        this.variableDestino = variableDestino;
    }

    public String getTipoInstruccion() {
        return tipoInstruccion;
    }

    public String getExpresionBase() {
        return expresionBase;
    }

    public List<Conector> getConectores() {
        return conectores;
    }

    public String getVariableDestino() {
        return variableDestino;
    }
}
