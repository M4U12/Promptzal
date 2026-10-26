package analizador;

import ast.*;
import modelos.ErrorCompilacion;
import modelos.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Parser {

    private List<Token> tokens;
    private int puntero;
    private Token tokenActual;
    private List<ErrorCompilacion> erroresSintacticos;

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

    public List<ErrorCompilacion> getErroresSintacticos() {
        return erroresSintacticos;
    }

    private void avanzar() {
        puntero++;
        if (puntero < tokens.size()) {
            tokenActual = tokens.get(puntero);
        } else {
            tokenActual = new Token(0, "EOF", "EOF", 0, 0);
        }
    }

    private boolean match(String tipoEsperado) {
        if (tokenActual != null && tokenActual.getTipo().equals(tipoEsperado)) {
            avanzar();
            return true;
        } else {
            manejarErrorSintactico(tipoEsperado);
            return false;
        }
    }

    private void manejarErrorSintactico(String tipoEsperado) {
        String descripcion = "Se esperaba [" + tipoEsperado + "] pero se encontró [" + (tokenActual != null ? tokenActual.getLexema() : "null") + "]";
        int filaError = tokenActual != null ? tokenActual.getFila() : 0;
        int colError = tokenActual != null ? tokenActual.getColumna() : 0;
        String lexemaError = tokenActual != null ? tokenActual.getLexema() : "EOF";

        erroresSintacticos.add(new ErrorCompilacion(
                erroresSintacticos.size() + 1,
                lexemaError,
                "SINTÁCTICO",
                descripcion,
                filaError,
                colError
        ));

        while (tokenActual != null && !TOKENS_SINCRONIZACION.contains(tokenActual.getTipo()) && !tokenActual.getTipo().equals("EOF")) {
            avanzar();
        }
    }

    // Regla: <programa> -> <directivas> <agentes> <ejecuciones> <exportar> EOF
    public Programa parsePrograma() {
        List<Directiva> directivas = parseDirectivas();
        List<Agente> agentes = parseAgentes();
        List<String> ejecuciones = parseEjecuciones();
        List<String> exportaciones = parseExportar();
        match("EOF");

        return new Programa(directivas, agentes, ejecuciones, exportaciones);
    }

    // Regla: <directivas> -> DIRECTIVA CADENA <directivas> | ε
    private List<Directiva> parseDirectivas() {
        List<Directiva> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("DIRECTIVA")) {
            String nombre = tokenActual.getLexema();
            match("DIRECTIVA");

            String valor = "";
            if (tokenActual != null && tokenActual.getTipo().equals("CADENA")) {
                valor = tokenActual.getLexema();
            }
            match("CADENA");

            lista.add(new Directiva(nombre, valor));
            lista.addAll(parseDirectivas());
        }
        return lista;
    }

    // Regla: <agentes> -> <agente> <mas_agentes>
    private List<Agente> parseAgentes() {
        List<Agente> lista = new ArrayList<>();
        Agente ag = parseAgente();
        if (ag != null) {
            lista.add(ag);
        }
        lista.addAll(parseMasAgentes());
        return lista;
    }

    // Regla: <mas_agentes> -> <agente> <mas_agentes> | ε
    private List<Agente> parseMasAgentes() {
        List<Agente> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("AGENTE")) {
            Agente ag = parseAgente();
            if (ag != null) {
                lista.add(ag);
            }
            lista.addAll(parseMasAgentes());
        }
        return lista;
    }

    // Regla: <agente> -> AGENTE ID LLAVE_A <contexto> <variables> <comandos> LLAVE_C
    private Agente parseAgente() {
        int fila = tokenActual != null ? tokenActual.getFila() : 0;
        int columna = tokenActual != null ? tokenActual.getColumna() : 0;

        match("AGENTE");

        String nombre = "";
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            nombre = tokenActual.getLexema();
        }
        match("ID");

        match("LLAVE_A");

        String contexto = parseContexto();
        List<Variable> variables = parseVariables();
        List<Comando> comandos = parseComandos();

        match("LLAVE_C");

        // Si falló catastróficamente al recuperar el nombre, devolvemos null, sino armamos el nodo
        if (nombre.isEmpty()) {
            return null;
        }
        return new Agente(nombre, contexto, variables, comandos, fila, columna);
    }

    // Regla: <contexto> -> CONTEXTO IGUAL CADENA
    private String parseContexto() {
        match("CONTEXTO");
        match("IGUAL");

        String valor = "";
        if (tokenActual != null && tokenActual.getTipo().equals("CADENA")) {
            valor = tokenActual.getLexema();
        }
        match("CADENA");

        return valor;
    }

    // Regla: <variables> -> VARIABLE ID IGUAL <expresion> <variables> | ε
    private List<Variable> parseVariables() {
        List<Variable> lista = new ArrayList<>();

        if (tokenActual != null && tokenActual.getTipo().equals("VARIABLE")) {
            int fila = tokenActual.getFila();
            int columna = tokenActual.getColumna();

            match("VARIABLE");

            String nombre = "";
            if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
                nombre = tokenActual.getLexema();
            }
            match("ID");

            match("IGUAL");

            Expresion valorExpresion = parseExpresion();

            if (!nombre.isEmpty()) {
                lista.add(new Variable(nombre, valorExpresion, fila, columna));
            }

            lista.addAll(parseVariables());
        }
        return lista;
    }

    // Regla: <comandos> -> <comando> <mas_comandos>
    private List<Comando> parseComandos() {
        List<Comando> lista = new ArrayList<>();
        Comando cmd = parseComando();
        if (cmd != null) {
            lista.add(cmd);
        }
        lista.addAll(parseMasComandos());
        return lista;
    }

    // Regla: <mas_comandos> -> <comando> <mas_comandos> | ε
    private List<Comando> parseMasComandos() {
        List<Comando> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("COMANDO_IA")) {
            Comando cmd = parseComando();
            if (cmd != null) {
                lista.add(cmd);
            }
            lista.addAll(parseMasComandos());
        }
        return lista;
    }

    // Regla: <comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
    private Comando parseComando() {
        int fila = tokenActual != null ? tokenActual.getFila() : 0;
        int columna = tokenActual != null ? tokenActual.getColumna() : 0;

        String tipoInstruccion = "";
        if (tokenActual != null && tokenActual.getTipo().equals("COMANDO_IA")) {
            tipoInstruccion = tokenActual.getLexema();
        }
        match("COMANDO_IA");

        Expresion expresionBase = parseExpresion();
        List<Conector> conectores = parseConectores();

        match("FLECHA");

        String variableDestino = "";
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            variableDestino = tokenActual.getLexema();
        }
        match("ID");

        if (tipoInstruccion.isEmpty()) {
            return null;
        }
        return new Comando(tipoInstruccion, expresionBase, conectores, variableDestino, fila, columna);
    }

    // Regla: <conectores> -> CONECTOR <expresion> <conectores> | ε
    private List<Conector> parseConectores() {
        List<Conector> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("CONECTOR")) {
            String tipo = tokenActual.getLexema();
            match("CONECTOR");

            Expresion expresion = parseExpresion();

            lista.add(new Conector(tipo, expresion));
            lista.addAll(parseConectores());
        }
        return lista;
    }

    // Regla: <expresion> -> <termino> <mas_terminos>
    private Expresion parseExpresion() {
        List<Termino> listaTerminos = new ArrayList<>();
        Termino termino = parseTermino();
        if (termino != null) {
            listaTerminos.add(termino);
        }

        listaTerminos.addAll(parseMasTerminos());
        return new Expresion(listaTerminos);
    }

    // Regla: <mas_terminos> -> MAS <termino> <mas_terminos> | ε
    private List<Termino> parseMasTerminos() {
        List<Termino> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("MAS")) {
            match("MAS");
            Termino termino = parseTermino();
            if (termino != null) {
                lista.add(termino);
            }
            lista.addAll(parseMasTerminos());
        }
        return lista;
    }

    // Regla: <termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
    private Termino parseTermino() {
        if (tokenActual == null) {
            return null;
        }

        String tipo = tokenActual.getTipo();
        String valor = "";
        String unidad = "";
        int fila = tokenActual.getFila();
        int columna = tokenActual.getColumna();

        if (tipo.equals("CADENA") || tipo.equals("ID")) {
            valor = tokenActual.getLexema();
            match(tipo);
        } else if (tipo.equals("NUMERO")) {
            valor = tokenActual.getLexema();
            match("NUMERO");
            unidad = parseUnidad();
        } else if (tipo.equals("CARGAR")) {
            valor = parseCargar();
        } else {
            manejarErrorSintactico("CADENA, ID, NUMERO o CARGAR");
            return null;
        }

        return new Termino(tipo, valor, unidad, fila, columna);
    }

    // Regla: <unidad> -> ID | ε
    private String parseUnidad() {
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            String unidad = tokenActual.getLexema();
            match("ID");
            return unidad;
        }
        return "";
    }

    // Regla: <cargar> -> CARGAR PAR_A CADENA PAR_C
    private String parseCargar() {
        match("CARGAR");
        match("PAR_A");

        String archivo = "";
        // Corregido: La gramática estricta del proyecto solo permite CADENA
        if (tokenActual != null && tokenActual.getTipo().equals("CADENA")) {
            archivo = tokenActual.getLexema();
            match("CADENA");
        } else {
            manejarErrorSintactico("CADENA");
        }

        match("PAR_C");
        return "CARGAR(" + archivo + ")";
    }

    // Regla: <ejecuciones> -> EJECUTAR ID <mas_ejecuciones>
    private List<String> parseEjecuciones() {
        List<String> lista = new ArrayList<>();
        match("EJECUTAR");

        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            lista.add(tokenActual.getLexema());
        }
        match("ID");

        lista.addAll(parseMasEjecuciones());
        return lista;
    }

    // Regla: <mas_ejecuciones> -> EJECUTAR ID <mas_ejecuciones> | ε
    private List<String> parseMasEjecuciones() {
        List<String> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("EJECUTAR")) {
            match("EJECUTAR");
            if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
                lista.add(tokenActual.getLexema());
            }
            match("ID");
            lista.addAll(parseMasEjecuciones());
        }
        return lista;
    }

    // Regla: <exportar> -> EXPORTAR ID <mas_ids>
    private List<String> parseExportar() {
        List<String> lista = new ArrayList<>();
        match("EXPORTAR");

        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            lista.add(tokenActual.getLexema());
        }
        match("ID");

        lista.addAll(parseMasIds());
        return lista;
    }

    // Regla: <mas_ids> -> COMA ID <mas_ids> | ε
    private List<String> parseMasIds() {
        List<String> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("COMA")) {
            match("COMA");
            if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
                lista.add(tokenActual.getLexema());
            }
            match("ID");
            lista.addAll(parseMasIds());
        }
        return lista;
    }
}
