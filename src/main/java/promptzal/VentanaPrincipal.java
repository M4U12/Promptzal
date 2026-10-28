package promptzal;

import analizador.LexerPromptZal;
import analizador.Parser;
import ast.Programa;
import semantico.AnalizadorSemantico;
import generador.Generador;
import modelos.ErrorCompilacion;
import modelos.Token;
import utilidades.ManejadorArchivos;
import utilidades.GeneradorReportes;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Element;
import javax.swing.JButton;
import java.awt.Color;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class VentanaPrincipal extends javax.swing.JFrame {

    private String rutaArchivoActual;
    private ManejadorArchivos manejador;
    private List<Token> ultimosTokens;
    private List<ErrorCompilacion> ultimosErrores;
    private Programa ultimoAST;
    private AnalizadorSemantico ultimoSemantico;

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
        String[] columnasErrores = {"ID", "Tipo", "Lexema/Carácter", "Descripción", "Fila", "Columna"};
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

        //Estilo de los Botones 
        Color colorBoton = new Color(77, 77, 77);
        JButton[] botones = {btnAbrir, btnCompilar, btnGuardar, btnReportes, btnAST};
        for (JButton btn : botones) {
            btn.setBackground(colorBoton);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
        }

        // Estilo de las Tablas 
        Color colorFondoTabla = new Color(60, 63, 65);
        Color colorTextoTabla = new Color(187, 187, 187);

        //Eliminar los bordes blancos de los paneles de desplazamiento
        jScrollPane2.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane3.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane4.setBorder(BorderFactory.createEmptyBorder());

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
                case "COMANDO_IA":
                    estiloAplicar = estiloComando;
                    break;
                case "CARGAR":
                    estiloAplicar = estiloFuncion;
                    break;
                case "DIRECTIVA":
                    estiloAplicar = estiloDirectiva;
                    break;
                case "CONECTOR":
                    estiloAplicar = estiloConector;
                    break;
                case "CADENA":
                    estiloAplicar = estiloCadena;
                    break;
                case "NUMERO":
                    estiloAplicar = estiloNumero;
                    break;
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
                case "ID":
                    estiloAplicar = estiloIdentificador;
                    break;
                case "COMENTARIO":
                    estiloAplicar = estiloComentario;
                    break;
            }
            try {
                int offsetAbsoluto = root.getElement(t.getFila() - 1).getStartOffset() + (t.getColumna() - 1);
                doc.setCharacterAttributes(offsetAbsoluto, t.getLexema().length(), estiloAplicar, true);
            } catch (Exception ignored) {
            }
        }

        for (ErrorCompilacion e : errores) {
            try {
                int offsetAbsoluto = root.getElement(e.getFila() - 1).getStartOffset() + (e.getColumna() - 1);
                doc.setCharacterAttributes(offsetAbsoluto, e.getLexema().length(), estiloError, true);
            } catch (Exception ignored) {
            }
        }
        for (Token c : comentarios) {
            try {
                int offsetAbsoluto = root.getElement(c.getFila() - 1).getStartOffset() + (c.getColumna() - 1);
                doc.setCharacterAttributes(offsetAbsoluto, c.getLexema().length(), estiloComentario, true);
            } catch (Exception ignored) {
            }
        }
    }
    
    private void mostrarPromptsGenerados(Map<String, String> prompts) {
        javax.swing.JDialog dialogo = new javax.swing.JDialog(this, "Prompts Generados", false);
        dialogo.setSize(700, 500);
        dialogo.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 14));
        textArea.setBackground(new Color(43, 43, 43));
        textArea.setForeground(new Color(169, 183, 198));
        
        for(Map.Entry<String, String> entry : prompts.entrySet()) {
            textArea.append("===================================================\n");
            textArea.append(" ARCHIVO: " + entry.getKey() + ".txt\n");
            textArea.append("===================================================\n\n");
            textArea.append(entry.getValue() + "\n\n");
        }
        
        dialogo.add(new JScrollPane(textArea));
        dialogo.setVisible(true);
    }

    private void mostrarArbolAST() {
        java.io.File imgFile = new java.io.File("reporte_ast.png");
        if (!imgFile.exists()) {
            javax.swing.JOptionPane.showMessageDialog(this, "La imagen no se encontró. Asegurate de haberla generado en el botón de 'Reportes'", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        javax.swing.JDialog dialogo = new javax.swing.JDialog(this, "Árbol de Sintaxis Abstracta (AST)", false);
        dialogo.setSize(900, 700);
        dialogo.setLocationRelativeTo(this);
        
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgFile.getAbsolutePath());
        icon.getImage().flush(); 
        
        javax.swing.JLabel label = new javax.swing.JLabel(icon);
        label.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        dialogo.add(new javax.swing.JScrollPane(label));
        dialogo.setVisible(true);
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
        btnCompilar = new javax.swing.JButton();
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
        btnAST = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnCompilar.setText("Compilar");
        btnCompilar.addActionListener(this::btnCompilarActionPerformed);

        btnAbrir.setText("Abrir archivo");
        btnAbrir.addActionListener(this::btnAbrirActionPerformed);

        btnReportes.setText("Reportes");
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

        btnAST.setText("Ver AST");
        btnAST.addActionListener(this::btnASTActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAbrir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCompilar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAST)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(btnCompilar)
                    .addComponent(btnAST))
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

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilarActionPerformed
        String codigo = txtEditor.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El editor está vacío.");
            return;
        }

        try {
            //Análisis Léxico
            StringReader reader = new StringReader(codigo);
            LexerPromptZal lexer = new LexerPromptZal(reader);
            while (true) {
                Token token = lexer.yylex();
                if (token == null || token.getTipo().equals("EOF")) {
                    break;
                }
            }

            //Coloreado
            aplicarColoresSintaxis(txtEditor, lexer.getTokens(), lexer.getErrores(), lexer.getComentarios());

            //Análisis Sintáctico (AST)
            Parser parser = new Parser(lexer.getTokens());
            Programa programaAST = parser.parsePrograma();

            //Análisis Semántico (Tabla de Símbolos)
            AnalizadorSemantico semantico = new AnalizadorSemantico(programaAST);
            semantico.analizar();

            //Guarda en variables globales para los reportes
            ultimoAST = programaAST;
            ultimoSemantico = semantico;
            ultimosTokens = lexer.getTokens();

            List<ErrorCompilacion> todosLosErrores = new ArrayList<>();
            todosLosErrores.addAll(lexer.getErrores());
            todosLosErrores.addAll(parser.getErroresSintacticos());
            todosLosErrores.addAll(semantico.getTabla().getErroresSemanticos());
            ultimosErrores = todosLosErrores;

            //Tabla Tokens
            DefaultTableModel modeloTokens = (DefaultTableModel) tablaTokens.getModel();
            modeloTokens.setRowCount(0);
            for (Token t : ultimosTokens) {
                modeloTokens.addRow(new Object[]{t.getId(), t.getLexema(), t.getTipo(), t.getFila(), t.getColumna()});
            }

            //Tabla Errores 
            DefaultTableModel modeloErrores = (DefaultTableModel) tablaErrores.getModel();
            modeloErrores.setRowCount(0);
            for (ErrorCompilacion e : ultimosErrores) {
                modeloErrores.addRow(new Object[]{e.getId(), e.getTipoError(), e.getLexema(), e.getDescripcion(), e.getFila(), e.getColumna()});
            }

            //Generación de Archivos .txt (Si no hay errores)
            if (ultimosErrores.isEmpty()) {
                Generador generador = new Generador(programaAST);
                Map<String, String> promptsFinales = generador.generar();
                generador.exportarArchivos(promptsFinales);
                mostrarPromptsGenerados(promptsFinales);
                JOptionPane.showMessageDialog(this, "Compilación exitosa. Código validado y archivos exportados.\nDa clic en 'Reportes' para ver el AST.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Se encontraron " + ultimosErrores.size() + " errores en la compilación.\nRevisa la tabla de errores.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error grave al ejecutar el analizador: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnCompilarActionPerformed

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
        if (ultimoAST == null || ultimoSemantico == null || ultimosErrores == null) {
            JOptionPane.showMessageDialog(this, "Primero compila el código para poder generar los reportes.");
            return;
        }

        try {
            GeneradorReportes generador = new GeneradorReportes();
            generador.generarReporteErroresHTML(ultimosErrores);
            generador.generarReporteSimbolosHTML(ultimoSemantico.getTabla().getSimbolos());
            generador.generarReporteAST(ultimoAST);

            JOptionPane.showMessageDialog(this, "Reporte de Errores, Tabla de Símbolos y AST generados correctamente.");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar los reportes: " + e.getMessage());
        }
    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnASTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnASTActionPerformed
        mostrarArbolAST();
    }//GEN-LAST:event_btnASTActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAST;
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnCompilar;
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
