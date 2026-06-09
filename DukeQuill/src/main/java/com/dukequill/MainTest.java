package com.dukequill;

import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Lexer;
import com.dukequill.lexer.Token;
import java.util.List;

public class MainTest {
public static void main(String[] args) throws Exception {
    System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
   
    Dictionary dict = new Dictionary();
    dict.loadDictionary();

    System.out.println(dict.contains("hola"));      // debe imprimir true
    System.out.println(dict.contains("casa"));      // debe imprimir true
    System.out.println(dict.contains("asdfgh"));    // debe imprimir false
    System.out.println(dict.contains("comer"));     // debe imprimir true
    System.out.println(dict.contains("camión"));   // true
    System.out.println(dict.contains("camion"));    // ¿true o false?
        
    }
}