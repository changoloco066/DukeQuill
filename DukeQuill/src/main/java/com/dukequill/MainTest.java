package com.dukequill;

import com.dukequill.analyzer.SpellChecker;
import com.dukequill.analyzer.SpellErrors;
import com.dukequill.dictionary.*;
import com.dukequill.lexer.Lexer;
import com.dukequill.lexer.Token;
import com.dukequill.*;

import java.util.List;

public class MainTest {
public static void main(String[] args) throws Exception {
    System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));

        // 1. Cargar el diccionario
        Dictionary dict = new Dictionary();
        dict.loadDictionary();

        // 2. Tokenizar un texto con errores
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyze("Hola, esot es una prueba con erorres ortograficos");

        // 3. Revisar ortografía
        SpellChecker checker = new SpellChecker(dict);
        List<SpellErrors> errors = checker.check(tokens);

        // 4. Imprimir errores
        for(SpellErrors e : errors){
            System.out.println(e);
        }
    }
}