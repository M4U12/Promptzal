/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package promptzal;

import analizador.LexerPromptZal;
import analizador.Parser;
import ast.Programa;
import modelos.ErrorCompilacion;
import modelos.Token;
import utilidades.ManejadorArchivos;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelos.Tipos;
import utilidades.GeneradorReportes;
/*import utilidades.GraficadorAFD;*/
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Element;
import java.awt.Color;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import semantico.AnalizadorSemantico;

/**
 *
 * @author ACER
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private String rutaArchivoActual;
    private ManejadorArchivos manejador;
    private List<Token> ultimosTokens;
    private List<ErrorCompilacion> ultimosErrores;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        manejador = new ManejadorArchivos();
        configurarTablas();
        aplicarTemaOscuro();
    }

    public void configurarTablas() {
        String[] columnasTokens = {"ID", "Lexema", "Tipo", "Fila", "Columna"};
        tablaTokens.setModel(new DefaultTableModel(null, columnasTokens));

        String[] columnasErrores = {"ID", "Carácter/Lexema", "Descripción", "Fila", "Columna"};
        tablaErrores.setModel(new DefaultTableModel(null, columnasErrores));
    }

    private void aplicarTemaOscuro() {
        // Fondo general de la ventana y paneles
        Color fondoPrincipal = new Color(43, 43, 43);
        this.getContentPane().setBackground(fondoPrincipal);
        jPanel1.setBackground(fondoPrincipal);
        jPane2.setBackground(fondoPrincipal);

        // Estilo del Editor
        txtEditor.setBackground(new Color(30, 30, 30));
        txtEditor.setForeground(new Color(169, 183, 198)); // Texto por defecto gris claro
        txtEditor.setCaretColor(Color.WHITE); // Hace que el cursor titilante sea blanco

        // 3. Estilo de los Botones 
        Color colorBoton = new Color(77, 77, 77);
        JButton[] botones = {btnAbrir, btnAnalizar, btnGuardar, btnReportes};
        for (JButton btn : botones) {
            btn.setBackground(colorBoton);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false); 
            btn.setBorderPainted(false);
        }

        // Estilo de las Tablas 
        Color colorFondoTabla = new Color(60, 63, 65);
        Color colorTextoTabla = new Color(187, 187, 187);

        // 5. Eliminar los bordes blancos feos de los paneles de desplazamiento
        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        jScrollPane3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        jScrollPane4.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        // Estilizar el panel de pestañas 
        tabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        tabbedPane.setBackground(new Color(60, 63, 65)); // Fondo de las pestañas inactivas
        tabbedPane.setForeground(Color.WHITE); // Texto de las pestañas

        // Tabla Tokens
        tablaTokens.setBackground(colorFondoTabla);
        tablaTokens.setForeground(colorTextoTabla);
        tablaTokens.setGridColor(new Color(80, 80, 80));
        tablaTokens.getTableHeader().setBackground(fondoPrincipal);
        tablaTokens.getTableHeader().setForeground(Color.WHITE);

        // Tabla Errores
        tablaErrores.setBackground(colorFondoTabla);
        tablaErrores.setForeground(colorTextoTabla);
        tablaErrores.setGridColor(new Color(80, 80, 80));
        tablaErrores.getTableHeader().setBackground(fondoPrincipal);
        tablaErrores.getTableHeader().setForeground(Color.WHITE);
    }

    public void aplicarColoresSintaxis(JTextPane editor, List<Token> tokens, List<ErrorCompilacion> errores, List<Token> comentarios) {
        StyledDocument doc = editor.getStyledDocument();

        // --- DEFINICIÓN DE LA PALETA DE COLORES ---
        Style estiloNormal = editor.addStyle("Normal", null);
        StyleConstants.setForeground(estiloNormal, new Color(169, 183, 198)); // Gris base

        Style estiloReservada = editor.addStyle("Reservada", null);
        StyleConstants.setForeground(estiloReservada, new Color(204, 120, 50)); // Naranja óxido
        StyleConstants.setBold(estiloReservada, true);

        Style estiloComando = editor.addStyle("Comando", null);
        StyleConstants.setForeground(estiloComando, new Color(86, 175, 239)); // Azul celeste

        Style estiloDirectiva = editor.addStyle("Directiva", null);
        StyleConstants.setForeground(estiloDirectiva, new Color(224, 196, 108)); // Amarillo pálido
        StyleConstants.setItalic(estiloDirectiva, true);

        Style estiloFuncion = editor.addStyle("Funcion", null);
        StyleConstants.setForeground(estiloFuncion, new Color(152, 118, 170)); // Morado suave

        Style estiloConector = editor.addStyle("Conector", null);
        StyleConstants.setForeground(estiloConector, new Color(224, 112, 164)); // Rosado

        Style estiloCadena = editor.addStyle("Cadena", null);
        StyleConstants.setForeground(estiloCadena, Color.white); // Verde bosque

        Style estiloNumero = editor.addStyle("Numero", null);
        StyleConstants.setForeground(estiloNumero, new Color(104, 151, 187)); // Azul acero

        Style estiloSimbolo = editor.addStyle("Simbolo", null);
        StyleConstants.setForeground(estiloSimbolo, new Color(204, 120, 50));

        Style estiloIdentificador = editor.addStyle("Identificador", null);
        StyleConstants.setForeground(estiloIdentificador, new Color(106, 135, 89));

        Style estiloComentario = editor.addStyle("Comentario", null);
        StyleConstants.setForeground(estiloComentario, new Color(140, 140, 140));
        StyleConstants.setItalic(estiloComentario, true);

        Style estiloError = editor.addStyle("Error", null);
        StyleConstants.setForeground(estiloError, Color.red);

        // Limpia el formato actual
        doc.setCharacterAttributes(0, doc.getLength(), estiloNormal, true);

        //Pinta cada token
        Element root = doc.getDefaultRootElement();
        for (Token t : tokens) {
            Style estiloAplicar = estiloNormal;

            switch (t.getTipo()) {
                case "AGENTE":
                case "CONTEXTO":
                case "VARIABLE":
                case "EJECUTAR":
                case "EXPORTAR":
                    estiloAplicar = estiloReservada;
                    break;

                // Comandos IA y Funciones
                case "COMANDO_IA":
                    estiloAplicar = estiloComando;
                    break;
                case "CARGAR":
                    estiloAplicar = estiloFuncion;
                    break;

                // Directivas
                case "DIRECTIVA":
                    estiloAplicar = estiloDirectiva;
                    break;

                // Conectores
                case "CONECTOR":
                    estiloAplicar = estiloConector;
                    break;

                // Cadenas
                case "CADENA":
                    estiloAplicar = estiloCadena;
                    break;

                // Números
                case "NUMERO":
                    estiloAplicar = estiloNumero;
                    break;

                // Símbolos y operadores 
                case "FLECHA":
                case "IGUAL":
                case "MAS":
                case "LLAVE_A":
                case "LLAVE_C":
                case "PAR_A":
                case "PAR_C":
                case "COMA":
                    estiloAplicar = estiloSimbolo;
                    break;

                // Identificadores y comentarios
                case "ID":
                    estiloAplicar = estiloIdentificador;
                    break;
                case "COMENTARIO":
                    estiloAplicar = estiloComentario;
                    break;
            }
            for (ErrorCompilacion e : errores) {
                try {
                    int offsetInicioLinea = root.getElement(e.getFila() - 1).getStartOffset();
                    int offsetAbsoluto = offsetInicioLinea + (e.getColumna() - 1);
                    doc.setCharacterAttributes(offsetAbsoluto, e.getLexema().length(), estiloError, true);
                } catch (Exception ex) {
                }
            }
            for (Token c : comentarios) {
                try {
                    int offsetInicioLinea = root.getElement(c.getFila() - 1).getStartOffset();
                    int offsetAbsoluto = offsetInicioLinea + (c.getColumna() - 1);
                    doc.setCharacterAttributes(offsetAbsoluto, c.getLexema().length(), estiloComentario, true);
                } catch (Exception ex) {
                }
            }

            try {
                int offsetInicioLinea = root.getElement(t.getFila() - 1).getStartOffset();
                int offsetAbsoluto = offsetInicioLinea + (t.getColumna() - 1);
                doc.setCharacterAttributes(offsetAbsoluto, t.getLexema().length(), estiloAplicar, true);
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGuardar = new javax.swing.JButton();
        btnAnalizar = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTokens = new javax.swing.JTable();
        jPane2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaErrores = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnAnalizar.setText("Analizar");
        btnAnalizar.addActionListener(this::btnAnalizarActionPerformed);

        btnAbrir.setText("Abrir archivo");
        btnAbrir.addActionListener(this::btnAbrirActionPerformed);

        btnReportes.setText("ReportesHTML");
        btnReportes.addActionListener(this::btnReportesActionPerformed);

        tablaTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Lexema", "Tipo", "Fila", "Columna"
            }
        ));
        jScrollPane2.setViewportView(tablaTokens);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Tokens", jPanel1);

        tablaErrores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Lexema", "Descripcion", "Fila", "Columna"
            }
        ));
        jScrollPane3.setViewportView(tablaErrores);

        javax.swing.GroupLayout jPane2Layout = new javax.swing.GroupLayout(jPane2);
        jPane2.setLayout(jPane2Layout);
        jPane2Layout.setHorizontalGroup(
            jPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        jPane2Layout.setVerticalGroup(
            jPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
        );

        tabbedPane.addTab("Errores", jPane2);

        jScrollPane4.setViewportView(txtEditor);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAbrir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAnalizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar)
                .addGap(18, 18, 18)
                .addComponent(btnReportes)
                .addGap(15, 15, 15))
            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane4)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnAbrir)
                    .addComponent(btnReportes)
                    .addComponent(btnAnalizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos PromptZal (*.pz)", "pz");
        chooser.setFileFilter(filtro);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            rutaArchivoActual = archivo.getAbsolutePath();
            String contenido = manejador.leerArchivo(rutaArchivoActual);

            if (contenido != null) {
                txtEditor.setText(contenido);
            } else {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo. Verifica que sea .pz");
            }
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnAnalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarActionPerformed
        String codigo = txtEditor.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El editor está vacío.");
            return;
        }

        try {
            StringReader reader = new StringReader(codigo);
            LexerPromptZal lexer = new LexerPromptZal(reader);

            // Lee todos los tokens hasta el EOF
            while (true) {
                Token token = lexer.yylex();
                if (token == null || token.getTipo().equals("EOF")) {
                    break;
                }
            }

            // Pinta los colores 
            aplicarColoresSintaxis(txtEditor, lexer.getTokens(), lexer.getErrores(), lexer.getComentarios());

            // Llenar tabla de Tokens
            DefaultTableModel modeloTokens = (DefaultTableModel) tablaTokens.getModel();
            modeloTokens.setRowCount(0);
            for (Token t : lexer.getTokens()) {
                modeloTokens.addRow(new Object[]{t.getId(), t.getLexema(), t.getTipo(), t.getFila(), t.getColumna()});
            }

            // Ejecución del análisis sintáctico con los tokens obtenidos
            Parser parser = new Parser(lexer.getTokens());
            Programa programaAST = parser.parsePrograma();

            //Ejecución del análisis semántico (Tabla de Símbolos)
            AnalizadorSemantico semantico = new AnalizadorSemantico(programaAST);
            semantico.analizar();

            //Combinación de todos los errores (Léxicos, Sintácticos y Semánticos)
            List<ErrorCompilacion> todosLosErrores = new ArrayList<>();
            todosLosErrores.addAll(lexer.getErrores());
            todosLosErrores.addAll(parser.getErroresSintacticos());
            todosLosErrores.addAll(semantico.getTabla().getErroresSemanticos());
            
            ultimosTokens = lexer.getTokens(); 
            ultimosErrores = todosLosErrores;
            
            // Guardado para reportes HTML
            ultimosTokens = lexer.getTokens(); 
            ultimosErrores = todosLosErrores; 
            
            if (parser.getErroresSintacticos().isEmpty() && semantico.getTabla().getErroresSemanticos().isEmpty()) {
                generador.Generador generador = new generador.Generador(programaAST);
                Map<String, String> promptsFinales = generador.generar();              
                System.out.println("\n========== PROMPTS GENERADOS ==========\n");
                for (String agente : promptsFinales.keySet()) {
                    System.out.println(promptsFinales.get(agente));
                }
            } else {
                System.out.println("No se generó código debido a que hay errores en el archivo.");
            }

            //Llenado de la tabla de Errores con la lista combinada
            DefaultTableModel modeloErrores = (DefaultTableModel) tablaErrores.getModel();
            modeloErrores.setRowCount(0);
            for (ErrorCompilacion e : todosLosErrores) {
                modeloErrores.addRow(new Object[]{e.getId(), e.getLexema(), e.getDescripcion(), e.getFila(), e.getColumna()});
            }

            if (!todosLosErrores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se encontraron " + todosLosErrores.size() + " errores (Léxicos y/o Sintácticos).", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Análisis Léxico y Sintáctico exitoso. No hay errores.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar el analizador: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnAnalizarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (rutaArchivoActual != null) {
            manejador.guardarArchivo(rutaArchivoActual, txtEditor.getText());
            JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente.");
        } else {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos PromptZal (*.pz)", "pz");
            chooser.setFileFilter(filtro);
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                String ruta = archivo.getAbsolutePath();
                if (!ruta.endsWith(".pz")) {
                    ruta += ".pz";
                }
                rutaArchivoActual = ruta;
                manejador.guardarArchivo(rutaArchivoActual, txtEditor.getText());
                JOptionPane.showMessageDialog(this, "Archivo creado y guardado exitosamente.");
            }
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        if (ultimosTokens == null || ultimosErrores == null) {
            JOptionPane.showMessageDialog(this, "Primero debes analizar el código para poder generar los reportes.");
            return;
        }

        GeneradorReportes generador = new GeneradorReportes();
        generador.generarReporteTokensHTML(ultimosTokens);
        generador.generarReporteErroresHTML(ultimosErrores);
        generador.generarReporteEstadisticasHTML(ultimosTokens, ultimosErrores);
        /*GraficadorAFD graficador = new utilidades.GraficadorAFD();
        graficador.generarGrafo();*/

        JOptionPane.showMessageDialog(this, "¡Reportes HTML e Imagen del Autómata (grafo_afd.png) generados exitosamente en la carpeta del proyecto!");
    }//GEN-LAST:event_btnReportesActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnAnalizar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnReportes;
    private javax.swing.JPanel jPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tablaErrores;
    private javax.swing.JTable tablaTokens;
    private javax.swing.JTextPane txtEditor;
    // End of variables declaration//GEN-END:variables
}
