package com.dukequill.gui;

import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.dukequill.*;
import com.dukequill.analyzer.SpellChecker;
import com.dukequill.analyzer.SpellErrors;
import com.dukequill.lexer.Token;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Lexer;

import java.awt.*;


public class MainWindow extends JFrame {
    private JTextArea inputArea;
    private JTable errorTable;
    private JScrollPane scrollPane;
    private JTabbedPane tabs;
    private DefaultTableModel errorModel;

    private Dictionary dictionary;
    private SpellChecker checker;

    private List<Token> tokens;
    private Set<String> words;
    private List<SpellErrors> errors;
    private Lexer lexer;
   

    public MainWindow(){
        setTitle("DukeQuill");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputArea = new JTextArea();
        //inputArea.setText(buildSampleCode());
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Escribe aqui tu texto"));

        tabs = new JTabbedPane();
        
        String[] errorCols = {"Line", "Position", "Error", "Context"};
        errorModel = new DefaultTableModel(errorCols, 0);
        errorTable = new JTable(errorModel);
        tabs.addTab("Syntax Errors", new JScrollPane(errorTable));

        String[] symbolCols = {"Name", "Type", "Value", "Line"};

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, tabs);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.3);
        add(splitPane, BorderLayout.CENTER);

        JButton analyzeBtn = new JButton("Analyze");
        JButton exportBtn = new JButton("Export Results");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(exportBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        lexer = new Lexer();
        dictionary = new Dictionary();
        dictionary.loadDictionary();
        checker = new SpellChecker(dictionary);

        analyzeBtn.addActionListener(e -> analyzeText());
    }

     private void analyzeText(){
        String input = inputArea.getText();
        
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyze(input);

        SpellChecker checker = new checker(token);
        SpellChecker.checker();
        List<SpellErrors> errors = checker.getErrors();

        tokens = tokens;
        errors = errors;

        loadTokens(tokens);
        loadErrors(errors);

        if(!errors.isEmpty()){
            JOptionPane.showMessageDialog(this, errors.size() + "Error(es) ortograficos encontrados", "Error", JOptionPane.ERROR_MESSAGE);
            tabs.setSelectedIndex(1);
        }
    }

    private void LoadErrors(List<SpellErrors> errors){
        errorModel.setRowCount(0);
        for(SpellErrors e : errors){
            errorModel.addRow(new Objet[]{ e.getLine(), e.getPosition(), e.getMessage()
                
            });
        }
    }
}
