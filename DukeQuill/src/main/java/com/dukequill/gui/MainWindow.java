package com.dukequill.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.ECFieldF2m;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

import javax.print.DocFlavor.STRING;
import javax.print.attribute.standard.JobKOctets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import com.dukequill.analyzer.AccentChecker;
import com.dukequill.analyzer.AccentViolations;
import com.dukequill.analyzer.MorphAnalyzer;
import com.dukequill.analyzer.SpellChecker;
import com.dukequill.analyzer.SpellErrors;
import com.dukequill.lexer.Token;
import com.dukequill.rules.RuleEngine;
import com.dukequill.rules.RuleViolation;

import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Lexer;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public class MainWindow extends JFrame {

    // Componentes de la barra de menú 
    private JMenuBar menuBar;
    private JMenu herramientas;
    private JMenu verMenu;
    private JMenu archivoMenu;
    private JMenuItem menuItem;
    private JMenuItem toggleTabsItem;
    private JMenuItem exportMenuItem;
    private JMenuItem openMenuItem;

    // Área de texto principal 
    private JTextPane inputArea;
    private javax.swing.Timer delayTimer;
    private javax.swing.text.Highlighter.HighlightPainter errorPainter;
    private javax.swing.text.Highlighter.HighlightPainter rulePainter;

    // Panel de palabras ignoradas 
    private JPanel ignoredPanel;
    private DefaultListModel<String> ignoredListModel;
    private JList<String> ignoredList;
    private JTextField wordField;
    private JButton addButton;
    private JButton removeButton;

    // Tablas de resultados 
    private JTable errorTable;
    private DefaultTableModel errorModel;
    private JTable ruleTable;
    private DefaultTableModel ruleModel;
    private JTabbedPane tabs;

    // Layout principal 
    private JSplitPane mainSplitPane;
    private JSplitPane splitPane;
    private JScrollPane scrollPane;

    // Botones del panel inferior 
    private JButton analyzeBtn;
    private JButton exportBtn;

    // Lógica del corrector 
    private Dictionary dictionary;
    private SpellChecker checker;
    private RuleEngine ruleEngine;
    private MorphAnalyzer morphAnalyzer;
    private Lexer lexer;
    private AccentChecker accentChecker;

    // Listas de resultados 
    private List<Token> tokens;
    private List<SpellErrors> errors;

    // Mapas para tooltips 
    private HashMap<Integer, List<String>> suggestionMap;
    private HashMap<Integer, Integer> wordLengthMap;
    private HashMap<Integer, String> ruleMessageMap;
    private HashMap<Integer, Integer> ruleLengthMap;

    public MainWindow() throws Exception{
        setTitle("DukeQuill");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initLogic();
        initMenuBar();
        initIgnoredPanel();
        initTextArea();
        initTabs();
        initLayout();
        initButtonPanel();
        initListener();
    }

     private void analyzeText() throws Exception{
        String input = inputArea.getText();

        List<Token> tokens = lexer.analyze(input);
        List<SpellErrors> errors = checker.check(tokens);        
        List<RuleViolation> violations = ruleEngine.check(tokens);
        List<AccentViolations> accentErros = accentChecker.check(input);

        loadViolations(violations);
        loadErrors(errors);
        highlightErrors(tokens, errors, violations, accentErros);
    }

    private void loadViolations(List<RuleViolation> violations){
        ruleModel.setRowCount(0);
        for(RuleViolation v : violations){
            ruleModel.addRow(new Object[]{ v.getLine(), v.getPosition(), v.getRuleName(), v.getMessage() });
        }
    }

    private void loadErrors(List<SpellErrors> errors){
        errorModel.setRowCount(0);
        for(SpellErrors e : errors){
         errorModel.addRow(new Object[]{ e.getLine(), e.getPosition(), e.getLexeme() });
        }
    }

    private void highlightErrors(List<Token> tokens, List<SpellErrors> errors, List<RuleViolation> violations, List<AccentViolations> accentErrors) {

        suggestionMap = new HashMap<Integer, List<String>>();  
        wordLengthMap = new HashMap<Integer, Integer>();
        ruleMessageMap = new HashMap<>();
        ruleLengthMap = new HashMap<>();

        javax.swing.text.Highlighter highlighter = inputArea.getHighlighter();
        highlighter.removeAllHighlights();
        
        //String text = inputArea.getText();
        String text = inputArea.getText().replace("\r\n", "\n").replace("\r", "\n");
        for(RuleViolation v : violations) {
            int pos = getAbsolutePosition(text, v.getLine(), v.getPosition());
            try {
                ruleMessageMap.put(pos, v.getMessage());
                ruleLengthMap.put(pos, v.getLexeme().length());
                highlighter.addHighlight(pos, pos + v.getLexeme().length(), rulePainter);
            } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

        Set<Integer> accentHighlighted = new HashSet<>();
        for(AccentViolations a : accentErrors){
            int rawPos = a.getFromPos();
            Token tokenMatch = null;

            for(Token t : tokens){
                if(t.getType() == com.dukequill.lexer.TokenType.WORD){
                    int tStart = getAbsolutePosition(text, t.getLine(), t.getPosition());
                    int tEnd = tStart + t.getLexeme().length();
                    if(rawPos >= tStart && rawPos < tEnd){
                        tokenMatch = t;
                        break;
                    }
                }
            }

            if(tokenMatch != null){
                int start = getAbsolutePosition(text, tokenMatch.getLine(), tokenMatch.getPosition());
                if(!accentHighlighted.contains(start)){
                    accentHighlighted.add(start);
                    try{
                        highlighter.addHighlight(start, start + tokenMatch.getLexeme().length(), rulePainter);
                        ruleMessageMap.put(start, a.getMessage() + " (sugerencia: " + a.getSuggestedReplacements() + ")");
                        ruleLengthMap.put(start, tokenMatch.getLexeme().length());
                    }catch (Exception ex){ ex.printStackTrace(); }
                }
            }
        }

        for(SpellErrors e : errors) {
            String word = e.getLexeme();
            int index = getAbsolutePosition(text, e.getLine(), e.getPosition());    
            if(index >= 0) {
                try {
                    boolean isAccentError = accentErrors.stream()
                        .anyMatch(a -> a.getOriginalText().equals(word));

                    if(!isAccentError) {
                        highlighter.addHighlight(index, index + word.length(), errorPainter);
                    }
                    List<String> suggestions = checker.getSuggestions(word);
                    suggestionMap.put(index, suggestions);
                    wordLengthMap.put(index, word.length());

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void addWordSorted(String word) {
        if(ignoredListModel.contains(word)){
            JOptionPane.showMessageDialog(this, "La palabra ya se añadio al diccionario");
            return;
        }
        int index = 0;
        while(index < ignoredListModel.size() && 
            ignoredListModel.get(index).compareToIgnoreCase(word) < 0) {
            index++;
        }
        ignoredListModel.add(index, word);
    }

    private void initLogic() throws Exception {
        morphAnalyzer = new MorphAnalyzer();
        lexer = new Lexer();
        dictionary = new Dictionary();
        dictionary.loadDictionary();
        checker = new SpellChecker(dictionary, morphAnalyzer);
        ruleEngine = new RuleEngine(morphAnalyzer);
        accentChecker = new AccentChecker();
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();

        openMenuItem = new JMenuItem("Abrir archivo");

        archivoMenu = new JMenu("Archivo");
        exportMenuItem = new JMenuItem("Exportar Resultados");
        archivoMenu.add(openMenuItem);
        archivoMenu.addSeparator();
        archivoMenu.add(exportMenuItem);
        menuBar.add(archivoMenu);
       
        herramientas = new JMenu("Herramientas");
        menuItem = new JMenuItem("Diccionario personalizado");
        java.net.URL iconUrl = getClass().getResource("/icons/quill.png");
        ImageIcon icon = new ImageIcon(iconUrl);
        Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        menuItem.setIcon(new ImageIcon(scaled));
        herramientas.add(menuItem);
        menuBar.add(herramientas);

        verMenu = new JMenu("Ver tablas de errores");
        toggleTabsItem = new JMenuItem("Mostrar/ocultar tablas");
        verMenu.add(toggleTabsItem);
        menuBar.add(verMenu);

        setJMenuBar(menuBar);
    }

    private void initIgnoredPanel() {
        ignoredListModel = new DefaultListModel<>();
        ignoredList = new JList<>(ignoredListModel);

        wordField = new JTextField();
        addButton = new JButton("Agregar");
        removeButton = new JButton("Eliminar");

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsRow.add(addButton);
        buttonsRow.add(removeButton);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(wordField, BorderLayout.CENTER);
        inputPanel.add(buttonsRow, BorderLayout.SOUTH);

        ignoredPanel = new JPanel(new BorderLayout());
        ignoredPanel.setBorder(BorderFactory.createTitledBorder("Palabras ignoradas"));
        ignoredPanel.add(new JScrollPane(ignoredList), BorderLayout.CENTER);
        ignoredPanel.add(inputPanel, BorderLayout.SOUTH);
    }

    private void initTextArea() {
        inputArea = new JTextPane(){ 
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e){
                int pos = viewToModel2D(e.getPoint());
                if(suggestionMap != null){
                    for(Map.Entry<Integer, List<String>> entry : suggestionMap.entrySet()){
                        int start = entry.getKey();
                        int length = wordLengthMap.getOrDefault(start, 0);              
                        
                        if(pos >=  start && pos < start + length){
                            return " ¿Quisiste decir? " +  String.join(", ", entry.getValue());
                        }
                    }
                }

                if(ruleMessageMap != null){
                    for(Map.Entry<Integer, String> entry : ruleMessageMap.entrySet()){
                        int start = entry.getKey();
                        int length = ruleLengthMap.getOrDefault(start, 0);
                        if(pos >= start && pos < start + length){
                            return entry.getValue();
                        }
                    }
                }
                return null; 
            }
        };

        inputArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        javax.swing.ToolTipManager.sharedInstance().registerComponent(inputArea);
        javax.swing.ToolTipManager.sharedInstance().registerComponent(inputArea);
        javax.swing.ToolTipManager.sharedInstance().setInitialDelay(100);
        javax.swing.ToolTipManager.sharedInstance().setDismissDelay(5000);
        inputArea.setToolTipText("Texto");
        
        // Painters para el subrayado de errores
        errorPainter = new WavyUnderlinePainter(Color.RED);
        rulePainter = new WavyUnderlinePainter(Color.BLUE);

        // Timer para analisis en tiempo real con delay para evitar crasheos
        delayTimer = new javax.swing.Timer(500, e -> {
            try {
                analyzeText();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        delayTimer.setRepeats(false);
    }

    private void initTabs(){
        tabs = new JTabbedPane();
        tabs.setVisible(false);
        
        String[] errorCols = {"Linea", "Posicion", "Error", "Contexto"};
        errorModel = new DefaultTableModel(errorCols, 0);
        errorTable = new JTable(errorModel);
        tabs.addTab("Syntax Errors", new JScrollPane(errorTable));

        String[] ruleCols = {"Línea", "Posición", "Regla", "Detalle"};
        ruleModel = new DefaultTableModel(ruleCols, 0);
        ruleTable = new JTable(ruleModel);
        tabs.addTab("Reglas de puntuación", new JScrollPane(ruleTable));
    }

    private void initLayout(){ 
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Escribe aquí tu texto"));
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, tabs);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.3);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, null);
        mainSplitPane.setResizeWeight(1.0);
        mainSplitPane.setDividerSize(0);
        add(mainSplitPane, BorderLayout.CENTER);
    }

    private void initButtonPanel(){
        analyzeBtn = new JButton("Analizar texto");  
        exportBtn = new JButton("Exportar resultados");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(exportBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initListener(){
         analyzeBtn.addActionListener(e -> {
            try {
                analyzeText();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addButton.addActionListener(e -> {
            String word = wordField.getText().trim();
            if(!word.isEmpty()) {
                checker.ignoredWord(word);
                addWordSorted(word);
                wordField.setText("");
                try {
                    analyzeText();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        toggleTabsItem.addActionListener(e -> {
            boolean visible = !tabs.isVisible();
            tabs.setVisible(!tabs.isVisible());
            if(visible){
                splitPane.setDividerLocation(180);
            } else{
                splitPane.setDividerLocation(1.0);
            }
            splitPane.revalidate();
            splitPane.repaint();
        });

        removeButton.addActionListener(e ->{
            String selected = ignoredList.getSelectedValue();
            if(selected != null){
                checker.removeIgnoredWord(selected);
                ignoredListModel.removeElement(selected);
                try{
                    analyzeText();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        });

        menuItem.addActionListener(e -> {
            if(mainSplitPane.getRightComponent() == null) {
                mainSplitPane.setRightComponent(ignoredPanel);
                mainSplitPane.setDividerSize(5);
                mainSplitPane.setDividerLocation(650);
            } else {
                mainSplitPane.setRightComponent(null);
                mainSplitPane.setDividerSize(0);
            }
        });

        exportMenuItem.addActionListener(e ->{
            
            JOptionPane.showMessageDialog(this, "Funcion proximamente disponible", "Exportar", JOptionPane.INFORMATION_MESSAGE);
        });

        openMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Abrir archivo");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de texto y PDF (*.txt, *.pdf),", "txt", "pdf")
            );

            int result = fileChooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName().toLowerCase();
                //String line;

                try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                    String content = "";
                    if(fileName.endsWith(".txt")){
                        StringBuilder sb = new StringBuilder();
                        String line; 
                        while((line = reader.readLine()) != null){
                            sb.append(line).append("\n");
                        }
                        content = sb.toString();

                    }else if (fileName.endsWith(".pdf")){
                        PDDocument doc = Loader.loadPDF(file);
                        PDFTextStripper stripper = new PDFTextStripper();
                        content = stripper.getText(doc);
                        doc.close();
                    }
                    inputArea.setText(content);
                    analyzeText();

                }catch(IOException ex){
                    JOptionPane.showMessageDialog(this, "Algo salio mal " + ex.getMessage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Listener para detectar los cambios en el texto
        inputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
        });

        //Mouse Adapter para hacer clik derecho
        inputArea.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                if(javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    int pos = inputArea.viewToModel2D(e.getPoint());
                    String text = inputArea.getText();
                    
                    // buscar los límites de la palabra en esa posición
                    int start = pos;
                    int end = pos;
                    while(start > 0 && Character.isLetter(text.charAt(start - 1))) start--;
                    while(end < text.length() && Character.isLetter(text.charAt(end))) end++;
                    
                    String word = text.substring(start, end);
                    
                    if(!word.isEmpty()) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem ignoreItem = new JMenuItem("Agregar '" + word + "' al diccionario");
                        ignoreItem.addActionListener(ev -> {
                            checker.ignoredWord(word);
                            addWordSorted(word);
                            try {
                                analyzeText();
                            } catch(Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                        menu.add(ignoreItem);
                        menu.show(inputArea, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private int getAbsolutePosition(String text, int line, int column) {
        String[] lines = text.split("\n", -1); // el -1 es para conservar las lineas vacias al final
        int absolutePos = 0;
        for(int i = 0; i < line - 1; i++) {  // ciclo para iterar por todo el error, sumando las longitudes de las lineas previas
            absolutePos += lines[i].length() + 1; // +1 por el \n
        }
    return absolutePos + column; // se le suma la columna del token dentro de su linea para dar con la posicion absoluta
}

    private static class WavyUnderlinePainter extends javax.swing.text.LayeredHighlighter.LayerPainter {
        private final Color color;

    public WavyUnderlinePainter(Color color) {
        this.color = color;
    }

    // Requerido por la interfaz HighlightPainter, pero no se usa
    // porque JTextPane usa un LayeredHighlighter que llama a paintLayer().
    @Override
    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c){
        // sin implementacion ya que paintLayer() es el que pinta
    }
    public Shape paintLayer(Graphics g, int p0, int p1, Shape bounds, JTextComponent c, javax.swing.text.View view) {
        try {
            Rectangle2D r0 = view.modelToView(p0, javax.swing.text.Position.Bias.Forward, p1, javax.swing.text.Position.Bias.Backward, bounds).getBounds2D();
            g.setColor(color);
            int y = (int)(r0.getY() + r0.getHeight()) - 2;
            int x = (int) r0.getX();
            int endX = (int)(r0.getX() + r0.getWidth());
            int amplitude = 2;
            int wavelength = 4;
            while (x < endX) {
                g.drawLine(x, y, x + wavelength / 2, y - amplitude);
                g.drawLine(x + wavelength / 2, y - amplitude, x + wavelength, y);
                x += wavelength;
            }
            return r0.getBounds();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
