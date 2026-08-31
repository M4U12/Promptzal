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
    
    
    
    public void analizar(){
        while (posicionActual < codigoFuente.length()){
            char actual = obtenerCaracter();
            
            // ignorar saltos de línea y espacios en blanco
            if (actual == ' ' || actual == '\t' || actual == '\n' || actual == '\r'){ 
                avanzar();
                continue; 
            }
            
            // guarda las coordenadas de inicio del token
            int filaInicio = filaActual;
            int columnaInicio = columnaActual;
            
            if (esDelimitador(actual)) {
                procesarDelimitador(actual, filaInicio, columnaInicio);
            } 
            else if (actual == '+' || actual == '=' || actual == '-') {
                procesarOperadorOConector(actual, filaInicio, columnaInicio);
            } 
            else if (actual == '@') {
                procesarDirectiva(filaInicio, columnaInicio);
            } 
            else if (esLetra(actual)) {
                procesarPalabra(filaInicio, columnaInicio);
            } 
            else if (esDigito(actual)) {
                procesarNumero(filaInicio, columnaInicio);
            } 
            else if (actual == '/') {
                procesarComentarios(filaInicio, columnaInicio);
            } 
            else if (actual == '"') {
                procesarCadena(filaInicio, columnaInicio);
            } 
            // si no es nada de lo anterior
            else {
                errores.add(new ErrorLexico(contadorErrores++, String.valueOf(actual), "Carácter no reconocido", filaInicio, columnaInicio));
                avanzar();
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
    
    public void procesarDelimitador(char actual, int fila, int col) {
        String lexema = String.valueOf(actual);
        tokens.add(new Token(contadorTokens++, lexema, Tipos.DELIMITADOR, fila, col));
        avanzar();
    }
    
    public void procesarOperadorOConector(char actual, int fila, int col) {
        if (actual == '+' || actual == '=') {
            tokens.add(new Token(contadorTokens++, String.valueOf(actual), Tipos.OPERADOR, fila, col));
            avanzar();
        } else if (actual == '-') {
            avanzar(); // ve el siguiente carácter
            if (obtenerCaracter() == '>') {
                tokens.add(new Token(contadorTokens++, "->", Tipos.CONECTOR, fila, col));
                avanzar(); 
            } else {
                errores.add(new ErrorLexico(contadorErrores++, "-", "Se esperaba '>' después de '-'", fila, col));
            }
        }
    }

    public void procesarDirectiva(int fila, int col) {
        StringBuilder lexemaBuilder = new StringBuilder();
        lexemaBuilder.append(obtenerCaracter()); // guarda la '@'
        avanzar();
        
        while (posicionActual < codigoFuente.length() && esLetra(obtenerCaracter())) {
            lexemaBuilder.append(obtenerCaracter());
            avanzar();
        }
        tokens.add(new Token(contadorTokens++, lexemaBuilder.toString(), Tipos.DIRECTIVA, fila, col));
    }

    public void procesarPalabra(int fila, int col) {
        StringBuilder lexemaBuilder = new StringBuilder();
        
        while (posicionActual < codigoFuente.length() && 
              (esLetra(obtenerCaracter()) || esDigito(obtenerCaracter()))) {
            lexemaBuilder.append(obtenerCaracter());
            avanzar();
        }
        
        String lexema = lexemaBuilder.toString();
        String tipoToken = clasificarPalabra(lexema); 
        tokens.add(new Token(contadorTokens++, lexema, tipoToken, fila, col));
    }

    public void procesarNumero(int fila, int col) {
        StringBuilder lexemaBuilder = new StringBuilder();
        boolean tienePunto = false;

        while (posicionActual < codigoFuente.length() && 
              (esDigito(obtenerCaracter()) || obtenerCaracter() == '.')) {
            
            char c = obtenerCaracter();
            if (c == '.') {
                if (tienePunto) break; // si ya tenía un punto, rompe el ciclo
                tienePunto = true;
            }
            
            lexemaBuilder.append(c);
            avanzar();
        }

        String lexema = lexemaBuilder.toString();
        String tipoToken = tienePunto ? Tipos.LITERAL_DECIMAL : Tipos.LITERAL_ENTERO;
        
        if (lexema.endsWith(".")) {
            errores.add(new ErrorLexico(contadorErrores++, lexema, "Número decimal mal formado", fila, col));
        } else {
            tokens.add(new Token(contadorTokens++, lexema, tipoToken, fila, col));
        }
    }

    public void procesarComentarios(int fila, int col) {
        avanzar(); // avanza para ver que sigue después de '/'
        char siguiente = obtenerCaracter();
        
        if (siguiente == '/') { // comentario de línea
            avanzar(); 
            while (posicionActual < codigoFuente.length() && obtenerCaracter() != '\n') {
                avanzar();
            }
        } 
        else if (siguiente == '*') { // comentario de bloque
            avanzar(); 
            boolean comentarioCerrado = false;
            
            while (posicionActual < codigoFuente.length()) {
                if (obtenerCaracter() == '*' && 
                    posicionActual + 1 < codigoFuente.length() && 
                    codigoFuente.charAt(posicionActual + 1) == '/') {
                    
                    avanzar(); // consume '*'
                    avanzar(); // consume '/'
                    comentarioCerrado = true;
                    break;
                }
                avanzar(); 
            } 
            if (!comentarioCerrado) {
                errores.add(new ErrorLexico(contadorErrores++, "/*", "Comentario de bloque sin cerrar", fila, col));
            }
        } 
        else { // diagonal suelta
            errores.add(new ErrorLexico(contadorErrores++, "/", "Carácter no reconocido", fila, col));
        }
    }

    public void procesarCadena(int fila, int col) {
        StringBuilder lexemaBuilder = new StringBuilder();
        lexemaBuilder.append(obtenerCaracter()); // guarda la primera comilla
        avanzar();
        
        boolean cadenaCerrada = false;
        
        while (posicionActual < codigoFuente.length()) {
            char c = obtenerCaracter();
            lexemaBuilder.append(c);
            avanzar();
            
            if (c == '"') {
                cadenaCerrada = true;
                break; 
            }
            if (c == '\n') { // no se permiten saltos de línea dentro de la cadena
                break; 
            }
        }
        
        if (cadenaCerrada) {
            tokens.add(new Token(contadorTokens++, lexemaBuilder.toString(), Tipos.LITERAL_CADENA, fila, col));
        } else {
            errores.add(new ErrorLexico(contadorErrores++, lexemaBuilder.toString(), "Cadena sin cerrar", fila, col));
        }
    }
}
