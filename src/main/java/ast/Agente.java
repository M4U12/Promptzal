package ast;

import java.util.List;

public class Agente {

    private String nombre;
    private String contexto;
    private List<Variable> variables;
    private List<Comando> comandos;
    private int fila;
    private int columna;

    public Agente(String nombre, String contexto, List<Variable> variables, List<Comando> comandos, int fila, int columna) {
        this.nombre = nombre;
        this.contexto = contexto;
        this.variables = variables;
        this.comandos = comandos;
        this.fila = fila;
        this.columna = columna;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContexto() {
        return contexto;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Comando> getComandos() {
        return comandos;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
