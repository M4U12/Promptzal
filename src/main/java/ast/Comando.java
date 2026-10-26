package ast;

import java.util.List;

public class Comando {

    private String tipoInstruccion;
    private Expresion expresionBase;
    private List<Conector> conectores;
    private String variableDestino;
    private int fila;
    private int columna;

    public Comando(String tipoInstruccion, Expresion expresionBase, List<Conector> conectores, String variableDestino, int fila, int columna) {
        this.tipoInstruccion = tipoInstruccion;
        this.expresionBase = expresionBase;
        this.conectores = conectores;
        this.variableDestino = variableDestino;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipoInstruccion() {
        return tipoInstruccion;
    }

    public Expresion getExpresionBase() {
        return expresionBase;
    }

    public List<Conector> getConectores() {
        return conectores;
    }

    public String getVariableDestino() {
        return variableDestino;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
