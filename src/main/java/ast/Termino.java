package ast;

public class Termino {

    private String tipo; // "ID", "CADENA", "NUMERO", "CARGAR"
    private String valor; //si escribe 100 palabras, guarda "100"
    private String unidad; // guarda "palabras"
    private int fila;
    private int columna;

    public Termino(String tipo, String valor, String unidad, int fila, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
