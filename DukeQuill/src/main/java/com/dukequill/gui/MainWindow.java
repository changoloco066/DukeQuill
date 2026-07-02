package com.dukequill.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.JobKOctets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import com.dukequill.analyzer.MorphAnalyzer;
import com.dukequill.analyzer.SpellChecker;
import com.dukequill.analyzer.SpellErrors;
import com.dukequill.lexer.Token;
import com.dukequill.rules.RuleEngine;
import com.dukequill.rules.RuleViolation;

import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Lexer;

import java.awt.*;


public class MainWindow extends JFrame {
    private JTable errorTable;
    private JScrollPane scrollPane;
    private JTabbedPane tabs;
    private DefaultTableModel errorModel;
    private JTextPane inputArea;
    private JMenuBar menuBar;
    private JMenu herramientas;
    private JMenuItem menuItem;
    private JPanel ignoredPanel;
    private DefaultListModel<String> ignoredListModel;
    private JList<String> ignoredList;
    private JSplitPane mainSplitPane;

    private javax.swing.Timer delayTimer;
    private javax.swing.text.Highlighter.HighlightPainter errorPainter;
    private javax.swing.text.Highlighter.HighlightPainter rulePainter;

    private JTable ruleTable;
    private DefaultTableModel ruleModel;

    private Dictionary dictionary;
    private SpellChecker checker;
    private RuleEngine ruleEngine;
    private MorphAnalyzer morphAnalyzer;

    private List<Token> tokens;
    private List<SpellErrors> errors;
    private Lexer lexer;
    
    // Mapas para el uso de ToolTip
    private HashMap<Integer, List<String>> suggestionMap;
    private HashMap<Integer, Integer> wordLengthMap;
    private HashMap<Integer, String> ruleMessageMap;
    private HashMap<Integer, Integer> ruleLengthMap;

    public MainWindow() throws Exception{

        // Ventana principal 
        setTitle("DukeQuill");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Barra de Tareas
        menuBar = new JMenuBar();
        herramientas = new JMenu("Herramientas");
        menuItem = new JMenuItem("Diccionario personalizado");
        java.net.URL iconUrl = getClass().getResource("/icons/quill.png");
        ImageIcon icon = new ImageIcon(iconUrl);
        Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        menuItem.setIcon(new ImageIcon(scaled));

        herramientas.add(menuItem);
        menuBar.add(herramientas);
        setJMenuBar(menuBar);

        ignoredListModel = new DefaultListModel<>();
        ignoredList = new JList<>(ignoredListModel);

        JTextField wordField = new JTextField();
        JButton addButton = new JButton("Agregar");
        JButton removeButton = new JButton("Eliminar");

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsRow.add(addButton);
        buttonsRow.add(removeButton);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(wordField, BorderLayout.CENTER);
        inputPanel.add(buttonsRow, BorderLayout.SOUTH);

        JMenu verMenu = new JMenu("Ver tablas de errores");
        JMenuItem toggleTabsItem = new JMenuItem("Mostrar/ocultar tablas");
        verMenu.add(toggleTabsItem);
        menuBar.add(verMenu);
   


        ignoredPanel = new JPanel(new BorderLayout());
        ignoredPanel.setBorder(BorderFactory.createTitledBorder("Palabras ignoradas"));
        ignoredPanel.add(new JScrollPane(ignoredList), BorderLayout.CENTER);
        ignoredPanel.add(inputPanel, BorderLayout.SOUTH);

        // Pestañas y tablas de resultados (tablas posible eliminacion o desuso en version final)
        tabs = new JTabbedPane();
        tabs.setVisible(false);
        String[] errorCols = {"Linea", "Posicion", "Error", "Contexto"};
        errorModel = new DefaultTableModel(errorCols, 0);
        errorTable = new JTable(errorModel);
        tabs.addTab("Syntax Errors", new JScrollPane(errorTable));
        
        // Area del texto principal
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
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Escribe aquí tu texto"));
        
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


        // Listener para detectar los cambios en el texto
        inputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { delayTimer.restart(); }
        });

        String[] ruleCols = {"Línea", "Posición", "Regla", "Detalle"};
        ruleModel = new DefaultTableModel(ruleCols, 0);
        ruleTable = new JTable(ruleModel);
        tabs.addTab("Reglas de puntuación", new JScrollPane(ruleTable));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, tabs);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.3);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, null);
        mainSplitPane.setResizeWeight(1.0);
        mainSplitPane.setDividerSize(0);
        add(mainSplitPane, BorderLayout.CENTER);

        // Panel de botones 
        JButton analyzeBtn = new JButton("Analizar texto");  
        JButton exportBtn = new JButton("Exportar resultados");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(exportBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Inicializacion de la logica del corrector 
        morphAnalyzer = new MorphAnalyzer();
        lexer = new Lexer();
        dictionary = new Dictionary();
        dictionary.loadDictionary();
        checker = new SpellChecker(dictionary, morphAnalyzer);
        ruleEngine = new RuleEngine(morphAnalyzer);
        
    
        // Accion de los botones
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
            System.out.println("Seleccionado: " + selected);
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
    }

     private void analyzeText() throws Exception{
        String input = inputArea.getText();

        List<Token> tokens = lexer.analyze(input);
        List<SpellErrors> errors = checker.check(tokens);        
        List<RuleViolation> violations = ruleEngine.check(tokens);

        loadViolations(violations);
        loadErrors(errors);
        highlightErrors(errors, violations);

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

    private void highlightErrors(List<SpellErrors> errors, List<RuleViolation> violations) {

        suggestionMap = new HashMap<Integer, List<String>>();  
        wordLengthMap = new HashMap<Integer, Integer>();
        ruleMessageMap = new HashMap<>();
        ruleLengthMap = new HashMap<>();

        javax.swing.text.Highlighter highlighter = inputArea.getHighlighter();
        highlighter.removeAllHighlights();
        
        String text = inputArea.getText();
        
        for(SpellErrors e : errors) {
            String word = e.getLexeme();
            int index = text.indexOf(word);
            if(index >= 0) {
                try {
                    List<String> suggestions = checker.getSuggestions(word);
                    suggestionMap.put(index, suggestions);
                    wordLengthMap.put(index, word.length());
                    
                    highlighter.addHighlight(index, index + word.length(), errorPainter);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        for(RuleViolation v : violations) {
            int pos = v.getPosition();
            System.out.println("Violación: " + v.getMessage() + " en pos " + pos);
            try {
                ruleMessageMap.put(pos, v.getMessage());
                ruleLengthMap.put(pos, v.getLexeme().length());
                highlighter.addHighlight(pos, pos + v.getLexeme().length(), rulePainter);
            } catch(Exception ex) {
                    ex.printStackTrace();
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

    private static class WavyUnderlinePainter implements javax.swing.text.Highlighter.HighlightPainter {
        private final Color color;

        public WavyUnderlinePainter(Color color) {
            this.color = color;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try {
                Rectangle r0 = c.modelToView(p0);
                Rectangle r1 = c.modelToView(p1);
                g.setColor(color);
                int y = r0.y + r0.height - 2;
                int x = r0.x;
                int endX = r1.x;
                int amplitude = 2;
                int wavelength = 4;
                while (x < endX) {
                    g.drawLine(x, y, x + wavelength / 2, y - amplitude);
                    g.drawLine(x + wavelength / 2, y - amplitude, x + wavelength, y);
                    x += wavelength;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
  
}
