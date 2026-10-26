package modelos;

public class ErrorCompilacion {
    private int id;
    private String lexema;
    private String tipoError;
    private String descripcion;
    private int fila;
    private int columna;
    
    public ErrorCompilacion(int id, String lexema, String tipoError, String descripcion, int fila, int columna) {
        this.id = id;
        this.lexema = lexema;
        this.tipoError = tipoError;
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

    public String getTipoError() {
        return tipoError;
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
