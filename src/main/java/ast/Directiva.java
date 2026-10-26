package ast;

public class Directiva {

    private String nombre; //"@modelo"
    private String valor;  //"\"gpt-4\""

    public Directiva(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getValor() {
        return valor;
    }
}
