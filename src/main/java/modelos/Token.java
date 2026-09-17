package modelos;

public class Token {
    private int id;
    private String lexema;
    private String tipo;
    private int fila;
    private int columna;
    
    public Token(int id, String lexema, String tipo, int fila, int col){
        this.id = id;
        this.lexema = lexema;
        this.tipo = tipo;
        this.fila = fila;
        this.columna = col;
    }
    
    public int getId() {
        return id;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    
    public String obtenerasdlles() {
        return "Token { " +
                "ID: " + id +
                ", Lexema: '" + lexema + '\'' +
                ", Tipo: '" + tipo + '\'' +
                ", Fila: " + fila +
                ", Columna: " + columna +
                " }";
    }
}

