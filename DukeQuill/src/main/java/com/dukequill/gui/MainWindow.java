package com.dukequill.gui;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    private javax.swing.Timer delayTimer;
    private javax.swing.text.Highlighter.HighlightPainter errorPainter;
    private javax.swing.text.Highlighter.HighlightPainter rulePainter;

    private JTable ruleTable;
    private DefaultTableModel ruleModel;

    private Dictionary dictionary;
    private SpellChecker checker;
    private RuleEngine ruleEngine;

    private List<Token> tokens;
    private List<SpellErrors> errors;
    private Lexer lexer;
   

    public MainWindow() throws Exception{
        setTitle("DukeQuill");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tabs = new JTabbedPane();
        
        String[] errorCols = {"Linea", "Posicion", "Error", "Contexto"};
        errorModel = new DefaultTableModel(errorCols, 0);
        errorTable = new JTable(errorModel);
        tabs.addTab("Syntax Errors", new JScrollPane(errorTable));
        
        inputArea = new JTextPane();
        inputArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Escribe aquí tu texto"));
        
        errorPainter = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(
            new java.awt.Color(255, 100, 100, 100));
        rulePainter = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(
            new java.awt.Color(100, 100, 255, 100));

        delayTimer = new javax.swing.Timer(500, e -> {
            try {
                analyzeText();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        delayTimer.setRepeats(false);

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
        add(splitPane, BorderLayout.CENTER);

        JButton analyzeBtn = new JButton("Analizar texto");
        JButton exportBtn = new JButton("Exportar resultados");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(exportBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        lexer = new Lexer();
        dictionary = new Dictionary();
        dictionary.loadDictionary();
        checker = new SpellChecker(dictionary);
        ruleEngine = new RuleEngine();

     analyzeBtn.addActionListener(e -> {
        try {
            analyzeText();
        } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        if(!errors.isEmpty()){
            JOptionPane.showMessageDialog(this, errors.size() + "Error(es) ortograficos encontrados", "Error", JOptionPane.ERROR_MESSAGE);
            tabs.setSelectedIndex(0);
        }
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
    javax.swing.text.Highlighter highlighter = inputArea.getHighlighter();
    highlighter.removeAllHighlights();
    
    String text = inputArea.getText();
    
    for(SpellErrors e : errors) {
        String word = e.getLexeme();
        int index = text.indexOf(word);
        if(index >= 0) {
            try {
                highlighter.addHighlight(index, index + word.length(), errorPainter);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    for(RuleViolation v : violations) {
        int pos = v.getPosition();
        try {
            highlighter.addHighlight(pos, pos + v.getLexeme().length(), rulePainter);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
}
