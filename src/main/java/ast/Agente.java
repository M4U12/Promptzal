package ast;

import java.util.List;

public class Agente {

    private String nombre;
    private String contexto;
    private List<Variable> variables;
    private List<Comando> comandos;

    public Agente(String nombre, String contexto, List<Variable> variables, List<Comando> comandos) {
        this.nombre = nombre;
        this.contexto = contexto;
        this.variables = variables;
        this.comandos = comandos;
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
}
