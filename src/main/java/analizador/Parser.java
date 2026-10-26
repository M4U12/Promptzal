package analizador;

import modelos.ErrorLexico;
import modelos.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import ast.*;

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
    private Agente parseAgente() {
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

        return new Agente(nombre, contexto, variables, comandos);
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
        List<Variable> listaVariables = new ArrayList<>();

        if (tokenActual != null && tokenActual.getTipo().equals("VARIABLE")) {
            match("VARIABLE");
            String nombre = "";
            if (tokenActual.getTipo().equals("ID")) {
                nombre = tokenActual.getLexema();
            }
            match("ID");
            match("IGUAL");

            String valor = parseExpresion();

            listaVariables.add(new Variable(nombre, valor));
            listaVariables.addAll(parseVariables());
        }

        // Si entra por la rama Épsilon, simplemente devuelve la lista vacía
        return listaVariables;
    }

    // Regla: <comandos> -> <comando> <mas_comandos>
    private List<Comando> parseComandos() {
        List<Comando> listaComandos = new ArrayList<>();
        listaComandos.add(parseComando()); // El primer comando es obligatorio
        listaComandos.addAll(parseMasComandos());
        return listaComandos;
    }

    // Regla: <mas_comandos> -> <comando> <mas_comandos> | ε
    private List<Comando> parseMasComandos() {
        List<Comando> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("COMANDO_IA")) {
            lista.add(parseComando());
            lista.addAll(parseMasComandos());
        }
        return lista; // Si es Épsilon, devuelve la lista vacía
    }

    // Regla: <comando> -> COMANDO_IA <expresion> <conectores> FLECHA ID
    private Comando parseComando() {
        // atrapa el tipo de instrucción (PREGUNTAR, GENERAR, etc.)
        String tipoInstruccion = "";
        if (tokenActual != null && tokenActual.getTipo().equals("COMANDO_IA")) {
            tipoInstruccion = tokenActual.getLexema();
        }
        match("COMANDO_IA");
        
        // atrapa el argumento principal
        String expresionBase = parseExpresion();
        
        // atrapa todos los conectores extra
        List<Conector> conectores = parseConectores();
        
        match("FLECHA");
        
        // atrapa dónde se guarda el resultado
        String variableDestino = "";
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            variableDestino = tokenActual.getLexema();
        }
        match("ID");
        
        // retorna el nodo Comando ya armado
        return new Comando(tipoInstruccion, expresionBase, conectores, variableDestino);
    }

    // Regla: <conectores> -> CONECTOR <expresion> <conectores> | ε
    private List<Conector> parseConectores() {
        List<Conector> lista = new ArrayList<>();
        if (tokenActual != null && tokenActual.getTipo().equals("CONECTOR")) {
            // atrapa la palabra (SOBRE, DESDE, EN...)
            String tipo = tokenActual.getLexema(); 
            match("CONECTOR");
            
            // atrapa el valor que acompaña al conector
            String expresion = parseExpresion(); 
            
            lista.add(new Conector(tipo, expresion));
            lista.addAll(parseConectores());
        }
        return lista;
    }

    // Regla: <expresion> -> <termino> <mas_terminos>
    private String parseExpresion() {
        String termino = parseTermino();
        String masTerminos = parseMasTerminos();
        // concatena las partes en un solo texto (por ejemplo "100" + " palabras")
        return termino + masTerminos; 
    }

    // Regla: <mas_terminos> -> MAS <termino> <mas_terminos> | ε
    private String parseMasTerminos() {
        if (tokenActual != null && tokenActual.getTipo().equals("MAS")) {
            match("MAS");
            String termino = parseTermino();
            String masTerminos = parseMasTerminos();
            // se le agrega el ' + ' literal para que el texto sea legible
            return " + " + termino + masTerminos;
        }
        return "";
    }

    // Regla: <termino> -> CADENA | ID | NUMERO <unidad> | <cargar>
    private String parseTermino() {
        if (tokenActual == null) return "";

        String tipo = tokenActual.getTipo();
        String valor = "";

        if (tipo.equals("CADENA") || tipo.equals("ID")) {
            valor = tokenActual.getLexema();
            match(tipo);
        } else if (tipo.equals("NUMERO")) {
            valor = tokenActual.getLexema();
            match("NUMERO");
            valor += parseUnidad(); // se le pega la unidad si es que trae
        } else if (tipo.equals("CARGAR")) {
            valor = parseCargar();
        } else {
            manejarErrorSintactico("CADENA, ID, NUMERO o CARGAR");
        }
        return valor;
    }

    // Regla: <unidad> -> ID | ε
    private String parseUnidad() {
        if (tokenActual != null && tokenActual.getTipo().equals("ID")) {
            String unidad = tokenActual.getLexema();
            match("ID");
            return " " + unidad; 
        }
        return "";
    }

    // Regla: <cargar> -> CARGAR PAR_A CADENA PAR_C
    private String parseCargar() {
        match("CARGAR");
        match("PAR_A");
        
        String archivo = "";
        // soporta la regla flexible (ID o CADENA)
        if (tokenActual != null && (tokenActual.getTipo().equals("CADENA") || tokenActual.getTipo().equals("ID"))) {
            archivo = tokenActual.getLexema();
            if (tokenActual.getTipo().equals("CADENA")) match("CADENA");
            else match("ID");
        } else {
            manejarErrorSintactico("CADENA o ID");
        }
        
        match("PAR_C");
        return "CARGAR(" + archivo + ")"; // Guarda la instrucción como texto
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
