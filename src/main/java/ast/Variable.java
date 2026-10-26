package ast;

public class Variable {

    private String nombre;
    private String valor; // guarda la expresión ya evaluada en texto

    public Variable(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
