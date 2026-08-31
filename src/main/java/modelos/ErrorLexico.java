package modelos;

public class ErrorLexico {
    private int id;
    private String lexema;
    private String descripcion;
    private int fila;
    private int columna;
    
    public ErrorLexico(int id, String lexema, String descripcion, int fila, int columna) {
        this.id = id;
        this.lexema = lexema;
        this.descripcion = descripcion;
        this.fila = fila;
        this.columna = columna;
    }
    
    public int getId() {
        return id;
    }

    public String getLexema() {
        return lexema;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public String obtenerDetalles() {
        return "ErrorLexico { " +
                "ID: " + id +
                ", Carácter: '" + lexema + '\'' +
                ", Problema: '" + descripcion + '\'' +
                ", Fila: " + fila +
                ", Columna: " + columna +
                " }";
    }
}
