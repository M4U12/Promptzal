package semantico;

import modelos.ErrorCompilacion;
import java.util.ArrayList;
import java.util.List;

public class TablaSimbolos {

    private List<Simbolo> simbolos;
    private List<ErrorCompilacion> erroresSemanticos;

    public TablaSimbolos() {
        this.simbolos = new ArrayList<>();
        this.erroresSemanticos = new ArrayList<>();
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }

    public List<ErrorCompilacion> getErroresSemanticos() {
        return erroresSemanticos;
    }

    // registrar un agente
    public void registrarAgente(String nombre, int fila, int columna) {
        if (existeAgente(nombre)) {
            // no pueden haber dos agentes con el mismo nombre
            agregarError(nombre, "El Agente '" + nombre + "' ya fue declarado anteriormente.", fila, columna);
        } else {
            simbolos.add(new Simbolo(nombre, "AGENTE", "GLOBAL", fila, columna));
        }
    }

    // registrar una variable dentro de un agente
    public void registrarVariable(String nombre, String ambitoAgente, int fila, int columna) {
        if (existeVariableEnAmbito(nombre, ambitoAgente)) {
            // REGLA SEMÁNTICA: Variables duplicadas en el mismo agente
            agregarError(nombre, "La variable '" + nombre + "' ya está declarada en el agente '" + ambitoAgente + "'.", fila, columna);
        } else {
            simbolos.add(new Simbolo(nombre, "VARIABLE", ambitoAgente, fila, columna));
        }
    }

    // verificar si una variable fue declarada antes de usarse
    public boolean verificarUsoVariable(String nombre, String ambitoAgente, int fila, int columna) {
        if (!existeVariableEnAmbito(nombre, ambitoAgente)) {
            // REGLA SEMÁNTICA: Uso de variable no declarada
            agregarError(nombre, "La variable '" + nombre + "' no ha sido declarada en el agente '" + ambitoAgente + "'.", fila, columna);
            return false;
        }
        return true;
    }

    public boolean existeAgente(String nombre) {
        for (Simbolo s : simbolos) {
            if (s.getTipo().equals("AGENTE") && s.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeVariableEnAmbito(String nombre, String ambito) {
        for (Simbolo s : simbolos) {
            if (s.getTipo().equals("VARIABLE") && s.getNombre().equals(nombre) && s.getAmbito().equals(ambito)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean existeVariableGlobalmente(String nombre) {
        for (Simbolo s : simbolos) {
            if (s.getTipo().equals("VARIABLE") && s.getNombre().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    private void agregarError(String lexema, String descripcion, int fila, int columna) {
        erroresSemanticos.add(new ErrorCompilacion(
                erroresSemanticos.size() + 1,
                lexema,
                "SEMÁNTICO",
                descripcion,
                fila,
                columna
        ));
    }
}
