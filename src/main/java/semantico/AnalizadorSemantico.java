package semantico;

import ast.*;
import modelos.ErrorCompilacion;
import java.util.List;

public class AnalizadorSemantico {

    private Programa programaAST;
    private TablaSimbolos tabla;

    public AnalizadorSemantico(Programa programaAST) {
        this.programaAST = programaAST;
        this.tabla = new TablaSimbolos();
    }

    public TablaSimbolos getTabla() {
        return tabla;
    }

    public void analizar() {
        if (programaAST == null) {
            return;
        }

        // recorre todos los agentes 
        for (Agente agente : programaAST.getAgentes()) {

            // registra el agente (lanza error si está duplicado)
            tabla.registrarAgente(agente.getNombre(), agente.getFila(), agente.getColumna());
            String ambito = agente.getNombre();

            // revisa las variables de este agente
            for (Variable var : agente.getVariables()) {
                // antes de registrar la variable, revisa que su valor sea valido.
                // por ejemplo, variable total = ventas + 100; (verifica que ventas exista)
                validarExpresion(var.getValorExpresion(), ambito);

                // la registra
                tabla.registrarVariable(var.getNombre(), ambito, var.getFila(), var.getColumna());
            }

            // revisa los comandos de este agente
            for (Comando cmd : agente.getComandos()) {
                // valida la expresion principal (con la excepción de GENERAR)
                validarExpresionComando(cmd, ambito);

                // validamos todo lo que venga en los conectores (SOBRE, EN, etc)
                for (Conector conector : cmd.getConectores()) {
                    validarExpresion(conector.getExpresion(), ambito);
                }

                // el resultado (-> variable) crea una variable nueva o sobreescribe una
                // la registra para que los comandos de abajo la puedan usar.
                if (!tabla.existeVariableEnAmbito(cmd.getVariableDestino(), ambito)) {
                    tabla.registrarVariable(cmd.getVariableDestino(), ambito, cmd.getFila(), cmd.getColumna());
                }
            }
        }

        for (String nombreExportar : programaAST.getExportaciones()) {
            if (!tabla.existeVariableGlobalmente(nombreExportar)) {
                agregarErrorEspecial(nombreExportar, "Se intenta EXPORTAR la variable '" + nombreExportar + "' pero no ha sido generada por ningún agente.");
            }
        }
    }

    //para expresiones normales
    private void validarExpresion(Expresion expr, String ambito) {
        if (expr == null || expr.getTerminos() == null) {
            return;
        }

        for (Termino t : expr.getTerminos()) {
            // solo importan los IDs, si es una CADENA o un NUMERO, está perfecto.
            if (t.getTipo().equals("ID")) {
                tabla.verificarUsoVariable(t.getValor(), ambito, t.getFila(), t.getColumna());
            }
        }
    }

    // metodo especial para comandos (Por la regla del GENERAR del PDF)
    private void validarExpresionComando(Comando cmd, String ambito) {
        if (cmd.getExpresionBase() == null || cmd.getExpresionBase().getTerminos() == null) {
            return;
        }

        List<Termino> terminos = cmd.getExpresionBase().getTerminos();
        boolean esGenerar = cmd.getTipoInstruccion().equals("GENERAR");

        for (int i = 0; i < terminos.size(); i++) {
            Termino t = terminos.get(i);
            if (t.getTipo().equals("ID")) {
                // si es GENERAR, el primer termino no es una variable
                if (esGenerar && i == 0) {
                    continue; // lo salta, no da error.
                }
                tabla.verificarUsoVariable(t.getValor(), ambito, t.getFila(), t.getColumna());
            }
        }
    }

    private void agregarErrorEspecial(String lexema, String descripcion) {
        tabla.getErroresSemanticos().add(new ErrorCompilacion(
                tabla.getErroresSemanticos().size() + 1,
                lexema,
                "SEMÁNTICO",
                descripcion,
                0,
                0
        ));
    }
}
