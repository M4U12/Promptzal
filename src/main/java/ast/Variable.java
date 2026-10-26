package ast;

public class Variable {

    private String nombre;
    private Expresion valorExpresion; 
    private int fila;
    private int columna;

    public Variable(String nombre, Expresion valorExpresion, int fila, int columna) {
        this.nombre = nombre;
        this.valorExpresion = valorExpresion;
        this.fila = fila;
        this.columna = columna;
    }

    public String getNombre() {
        return nombre;
    }

    public Expresion getValorExpresion() {
        return valorExpresion;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
