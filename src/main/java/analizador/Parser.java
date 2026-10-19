package analizador;

import modelos.ErrorLexico;
import modelos.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Parser {

    private List<Token> tokens;
    private int puntero;
    private Token tokenActual;
    private List<ErrorLexico> erroresSintacticos; // Lista para almacenar los errores detectados

    // Conjunto de tokens que permiten retomar el análisis tras un error
    private static final List<String> TOKENS_SINCRONIZACION = Arrays.asList(
            "AGENTE", "LLAVE_C", "COMANDO_IA", "VARIABLE", "EJECUTAR", "EXPORTAR", "EOF"
    );

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.puntero = 0;
        this.erroresSintacticos = new ArrayList<>();
        if (!tokens.isEmpty()) {
            this.tokenActual = tokens.get(0);
        }
    }

    public List<ErrorLexico> getErroresSintacticos() {
        return erroresSintacticos;
    }

    // Avanza el puntero al siguiente token de la lista
    private void avanzar() {
        puntero++;
        if (puntero < tokens.size()) {
            tokenActual = tokens.get(puntero);
        } else {
            // Generación de un token EOF artificial para prevenir desbordamientos
            tokenActual = new Token(0, "EOF", "EOF", 0, 0);
        }
    }

    // Verifica si el token actual coincide con el esperado, si no, invoca el modo pánico
    private boolean match(String tipoEsperado) {
        if (tokenActual != null && tokenActual.getTipo().equals(tipoEsperado)) {
            avanzar();
            return true;
        } else {
            manejarErrorSintactico(tipoEsperado);
            return false;
        }
    }

    // Registra el error y descarta tokens hasta encontrar uno de sincronización
    private void manejarErrorSintactico(String tipoEsperado) {
        String descripcion = "Se esperaba [" + tipoEsperado + "] pero se encontró [" + tokenActual.getLexema() + "]";

        erroresSintacticos.add(new ErrorLexico(
                erroresSintacticos.size() + 1,
                tokenActual.getLexema(),
                "SINTÁCTICO: " + descripcion,
                tokenActual.getFila(),
                tokenActual.getColumna()
        ));

        // Modo Pánico: avanza hasta encontrar un token seguro o el fin de archivo
        while (tokenActual != null && !TOKENS_SINCRONIZACION.contains(tokenActual.getTipo()) && !tokenActual.getTipo().equals("EOF")) {
            avanzar();
        }
    }

    // Regla: <programa> -> <directivas> <agentes> <ejecuciones> <exportar> EOF
    public void parsePrograma() {
        parseDirectivas();
        parseAgentes();
        parseEjecuciones();
        parseExportar();
        match("EOF");
    }

    // Regla: <directivas> -> DIRECTIVA CADENA <directivas> | ε
    private void parseDirectivas() {
        if (tokenActual != null && tokenActual.getTipo().equals("DIRECTIVA")) {
            match("DIRECTIVA");
            match("CADENA");
            parseDirectivas();
        }
    }

    // Regla: <agentes> -> <agente> <mas_agentes>
    private void parseAgentes() {
        parseAgente();
        parseMasAgentes();
    }

    // Regla: <mas_agentes> -> <agente> <mas_agentes> | ε
    private void parseMasAgentes() {
        if (tokenActual != null && tokenActual.getTipo().equals("AGENTE")) {
            parseAgente();
            parseMasAgentes();
        }
    }

    // Regla: <agente> -> AGENTE ID LLAVE_A <contexto> <variables> <comandos> LLAVE_C
    private void parseAgente() {
        match("AGENTE");
        match("ID");
        match("LLAVE_A");
        parseContexto();
        parseVariables();
        parseComandos();
        match("LLAVE_C");
    }

    // Regla: <contexto> -> CONTEXTO IGUAL CADENA
    private void parseContexto() {
        match("CONTEXTO");
        match("IGUAL");
        match("CADENA");
    }

    // Regla: <variables> -> VARIABLE ID IGUAL <expresion> <variables> | ε
    private void parseVariables() {
        if (tokenActual != null && tokenActual.getTipo().equals("VARIABLE")) {
            match("VARIABLE");
            match("ID");
            match("IGUAL");
            parseExpresion();
            parseVariables();
        }
    }

    // Regla: <comandos> -> <comando> <mas_comandos>
    private void parseComandos() {
        parseComando();
        parseMasComandos();
    }

    // Regla: <mas_comandos> -> <comando> <mas_comandos> | ε
    private void parseMasComandos() {
        if (tokenActual != null && tokenActual.getTipo().equals("COMANDO_IA")) {
            parseComando();
            parseMasComandos();
        }
    }

    // Regla: <comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
    private void parseComando() {
        match("COMANDO_IA");
        parseExpresion();
        parseConectores();
        match("FLECHA");
        match("ID");
    }

    // Regla: <conectores> -> CONECTOR <expresion> <conectores> | ε
    private void parseConectores() {
        if (tokenActual != null && tokenActual.getTipo().equals("CONECTOR")) {
            match("CONECTOR");
            parseExpresion();
            parseConectores();
        }
    }

    // Regla: <expresion> -> <termino> <mas_terminos>
    private void parseExpresion() {
        parseTermino();
        parseMasTerminos();
    }

    // Regla: <mas_terminos> -> MAS <termino> <mas_terminos> | ε
    private void parseMasTerminos() {
        if (tokenActual != null && tokenActual.getTipo().equals("MAS")) {
            match("MAS");
            parseTermino();
            parseMasTerminos();
        }
    }

    // Regla: <termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
    private void parseTermino() {
        if (tokenActual == null) {
            return;
        }

        String tipo = tokenActual.getTipo();

        if (tipo.equals("CADENA")) {
            match("CADENA");
        } else if (tipo.equals("ID")) {
            match("ID");
        } else if (tipo.equals("NUMERO")) {
            match("NUMERO");
            parseUnidad();
        } else if (tipo.equals("CARGAR")) {
            parseCargar();
        } else {
            manejarErrorSintactico("CADENA, ID, NUMERO o CARGAR");
        }
    }

    // Regla: <unidad> -> ID | ε
    private void parseUnidad() {
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            match("ID");
        }
    }

    // Regla: <cargar> -> CARGAR PAR_A CADENA PAR_C
    private void parseCargar() {
        match("CARGAR");
        match("PAR_A");
        match("CADENA");
        match("PAR_C");
    }

    // Regla: <ejecuciones> -> EJECUTAR ID <mas_ejecuciones>
    private void parseEjecuciones() {
        match("EJECUTAR");
        match("ID");
        parseMasEjecuciones();
    }

    // Regla: <mas_ejecuciones> -> EJECUTAR ID <mas_ejecuciones> | ε
    private void parseMasEjecuciones() {
        if (tokenActual != null && tokenActual.getTipo().equals("EJECUTAR")) {
            match("EJECUTAR");
            match("ID");
            parseMasEjecuciones();
        }
    }

    // Regla: <exportar> -> EXPORTAR ID <mas_ids>
    private void parseExportar() {
        match("EXPORTAR");
        match("ID");
        parseMasIds();
    }

    // Regla: <mas_ids> -> COMA ID <mas_ids> | ε
    private void parseMasIds() {
        if (tokenActual != null && tokenActual.getTipo().equals("COMA")) {
            match("COMA");
            match("ID");
            parseMasIds();
        }
    }
}
