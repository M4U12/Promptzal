package analizador;
import modelos.ErrorLexico;
import modelos.Tipos;
import modelos.Token;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {
    private List<Token> tokens;
    private List<ErrorLexico> errores;
    private int contadorTokens;
    private int contadorErrores;
    private String codigoFuente;
    private int posicionActual;
    private int filaActual;
    private int columnaActual;
    
    public AnalizadorLexico(String codigoFuente){
        this.codigoFuente = codigoFuente;
        tokens = new ArrayList<>();
        errores = new ArrayList<>();
        this.contadorTokens = 1;
        this.contadorErrores = 1;
        this.posicionActual = 0;
        this.filaActual = 1;
        this.columnaActual = 1;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }
    
    public char obtenerCaracter(){
        if (posicionActual < codigoFuente.length()){
            return codigoFuente.charAt(posicionActual);
        }
        return '\0'; //caracter nulo, indica el fin del archivo
    }
    
    public void avanzar(){
        char actual = obtenerCaracter();
        if (actual == '\n'){ //por si es salto de linea pasa a la siguiente fila
            filaActual++;
            columnaActual = 1; //vuelve al inicio 
        } else {
            columnaActual ++;
        }
        posicionActual++; //para que siempre avanance
    }
    
    
    
    public void analizar() {
        int estado = 0;
        StringBuilder lexemaActual = new StringBuilder();
        int filaInicio = filaActual;
        int colInicio = columnaActual;

        // para permitir que el analizador procese el carácter '\0' (fin de archivo)
        while (posicionActual <= codigoFuente.length()) {
            char actual = obtenerCaracter();

            // si es el estado inicial y llega al final del archivo, acaba
            if (actual == '\0' && estado == 0) {
                break;
            }

            switch (estado) {
                case 0: // 0: inicial
                    // ignorar espacios en blanco y saltos de línea
                    if (actual == ' ' || actual == '\t' || actual == '\n' || actual == '\r') {
                        avanzar();
                        continue;
                    }
                    if (actual == '\0') break; // seguro contra ciclos infinitos al final
                    
                    filaInicio = filaActual;
                    colInicio = columnaActual;
                    lexemaActual.setLength(0);

                    if (esLetra(actual)) {
                        lexemaActual.append(actual);
                        estado = 1;
                        avanzar();
                    } else if (actual == '@') {
                        lexemaActual.append(actual);
                        estado = 2;
                        avanzar();
                    } else if (esDigito(actual)) {
                        lexemaActual.append(actual);
                        estado = 3;
                        avanzar();
                    } else if (actual == '"') {
                        estado = 6;
                        avanzar();
                    } else if (actual == '/') {
                        lexemaActual.append(actual);
                        estado = 7;
                        avanzar();
                    } else if (actual == '-') {
                        lexemaActual.append(actual);
                        estado = 11;
                        avanzar();
                    } else if (esDelimitador(actual)) { //estado de aceptación directa
                        tokens.add(new Token(contadorTokens++, String.valueOf(actual), Tipos.DELIMITADOR, filaInicio, colInicio));
                        avanzar();
                    } else if (actual == '+' || actual == '=') { //estado de aceptación directa
                        tokens.add(new Token(contadorTokens++, String.valueOf(actual), Tipos.OPERADOR, filaInicio, colInicio));
                        avanzar();
                    } else {
                        // registra, avanza y sigue en estado 0
                        errores.add(new ErrorLexico(contadorErrores++, String.valueOf(actual), "Carácter no reconocido", filaInicio, colInicio));
                        avanzar();
                    }
                    break;

                case 1: // estado 1: identificadores y palabras clave
                    if (actual != '\0' && (esLetra(actual) || esDigito(actual))) {
                        lexemaActual.append(actual);
                        avanzar();
                    } else {
                        // estado de aceptacion, vuelve a estado 0
                        String lexema = lexemaActual.toString();
                        tokens.add(new Token(contadorTokens++, lexema, clasificarPalabra(lexema), filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 2: // 2: directivas, ya identificó el @
                    if (actual != '\0' && esLetra(actual)) {
                        lexemaActual.append(actual);
                        avanzar();
                    } else {
                        tokens.add(new Token(contadorTokens++, lexemaActual.toString(), Tipos.DIRECTIVA, filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 3: // 3: enteros
                    if (actual != '\0' && esDigito(actual)) {
                        lexemaActual.append(actual);
                        avanzar();
                    } else if (actual == '.') {
                        lexemaActual.append(actual);
                        estado = 4;
                        avanzar();
                    } else {
                        tokens.add(new Token(contadorTokens++, lexemaActual.toString(), Tipos.LITERAL_ENTERO, filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 4: // 4: punto decimal detectado
                    if (actual != '\0' && esDigito(actual)) {
                        lexemaActual.append(actual);
                        estado = 5;
                        avanzar();
                    } else {
                        // error de recuperacion: decimal incompleto 
                        errores.add(new ErrorLexico(contadorErrores++, lexemaActual.toString(), "Número decimal mal formado", filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 5: // 5: numeros decimales
                    if (actual != '\0' && esDigito(actual)) {
                        lexemaActual.append(actual);
                        avanzar();
                    } else {
                        tokens.add(new Token(contadorTokens++, lexemaActual.toString(), Tipos.LITERAL_DECIMAL, filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 6: // 6: cadenas de texto
                    if (actual == '\0' || actual == '\n') {
                        lexemaActual.append(actual);
                        errores.add(new ErrorLexico(contadorErrores++, "\"" + lexemaActual.toString(), "Cadena sin cerrar", filaInicio, colInicio));
                        estado = 0;
                    } else if (actual == '"') {
                        tokens.add(new Token(contadorTokens++, lexemaActual.toString(), Tipos.LITERAL_CADENA, filaInicio, colInicio));
                        avanzar();
                        estado = 0;
                    } else {
                        lexemaActual.append(actual);
                        avanzar();
                    }
                    break;

                case 7: // 7: diagonal detectada
                    if (actual == '/') {
                        lexemaActual.append(actual);
                        estado = 8;
                        avanzar();
                    } else if (actual == '*') {
                        lexemaActual.append(actual);
                        estado = 9;
                        avanzar();
                    } else {
                        errores.add(new ErrorLexico(contadorErrores++, lexemaActual.toString(), "Carácter no reconocido (se esperaba '/' o '*')", filaInicio, colInicio));
                        estado = 0;
                    }
                    break;

                case 8: // 8: comentario de linea
                    if (actual == '\n' || actual == '\0') {
                        // termina el comentario
                        estado = 0;
                    } else {
                        avanzar();
                    }
                    break;

                case 9: // 9: comentario de bloque
                    if (actual == '\0') {
                        errores.add(new ErrorLexico(contadorErrores++, lexemaActual.toString(), "Comentario de bloque sin cerrar", filaInicio, colInicio));
                        estado = 0;
                    } else if (actual == '*') {
                        estado = 10;
                        avanzar();
                    } else {
                        avanzar();
                    }
                    break;

                case 10: // 10: posible fin de comentario
                    if (actual == '\0') {
                        errores.add(new ErrorLexico(contadorErrores++, lexemaActual.toString(), "Comentario de bloque sin cerrar", filaInicio, colInicio));
                        estado = 0;
                    } else if (actual == '/') {
                        avanzar();
                        estado = 0; // comentario cerrado
                    } else if (actual == '*') {
                        avanzar(); 
                    } else {
                        estado = 9; 
                        avanzar();
                    }
                    break;

                case 11: // 11: guion detectado
                    if (actual == '>') {
                        lexemaActual.append(actual);
                        tokens.add(new Token(contadorTokens++, lexemaActual.toString(), Tipos.CONECTOR, filaInicio, colInicio));
                        avanzar();
                        estado = 0;
                    } else {
                        errores.add(new ErrorLexico(contadorErrores++, lexemaActual.toString(), "Se esperaba '>' después de '-'", filaInicio, colInicio));
                        estado = 0;
                    }
                    break;
            }
        }
    }
   
    public boolean esLetra(char c){
        return ( c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    
    public boolean esDigito(char c){
        return c >= '0' && c <= '9';
    }
    
    public boolean esDelimitador(char c) {
        return c == '{' || c == '}' || c == '(' || c == ')' || c == ',';
    }
    
    public String clasificarPalabra(String lexema) {
        // palabras reservadas
        if (lexema.equals("AGENTE") || lexema.equals("contexto") || 
            lexema.equals("variable") || lexema.equals("EJECUTAR") || 
            lexema.equals("EXPORTAR")) {
            return Tipos.RESERVADA;
        }
        
        // comandos de IA
        if (lexema.equals("PREGUNTAR") || lexema.equals("GENERAR") || 
            lexema.equals("RESUMIR") || lexema.equals("ANALIZAR") || 
            lexema.equals("TRADUCIR") || lexema.equals("CLASIFICAR") || 
            lexema.equals("EXTRAER")) {
            return Tipos.COMANDO_IA;
        }
        
        //funcion
        if (lexema.equals("CARGAR")) {
            return Tipos.FUNCION;
        }
        
        // conectores que son palabras
        if (lexema.equals("SOBRE") || lexema.equals("DESDE") || 
            lexema.equals("EN") || lexema.equals("COMO")) {
            return Tipos.CONECTOR;
        }
        
        // si no es ninguna palabra clave del lenguaje, por descarte es un nombre de variable o agente
        return Tipos.IDENTIFICADOR;
    }
}
