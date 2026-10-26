package semantico;

public class Simbolo {

    private String nombre;
    private String tipo;   // "AGENTE" o "VARIABLE"
    private String ambito; // "GLOBAL" (para agentes) o el nombre del agente (para variables)
    private int fila;
    private int columna;

    public Simbolo(String nombre, String tipo, String ambito, int fila, int columna) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.ambito = ambito;
        this.fila = fila;
        this.columna = columna;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getAmbito() {
        return ambito;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
