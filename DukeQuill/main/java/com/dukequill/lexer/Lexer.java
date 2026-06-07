package com.dukequill.lexer;

import java.util.*;

public class Lexer {

    private List<Tokens> tokens;

    private static final Set<Character> SINGLE_PUNCTUATION_SIGN = Set.of('.', ',', ';', ':', '…');
    private static final Set<Character> AUX_SIGN = Set.of('/', '-');
    private static final Set<Character> OPEN_PUNCTUATION_SIGN = Set.of('¿', '¡');
    private static final Set<Character> OPEN_AUX_SIGN = Set.of('(', '[', '{', '«', '“', '‘', '"', '\'');
    private static final Set<Character> CLOSE_PUNCTUATION_SIGN = Set.of('?', '!');
    private static final Set<Character> CLOSE_AUX_SIGN = Set.of(')', ']', '}', '»', '”', '’', '"', '\'');


    public Lexer(){
        tokens = new ArrayList<>();
    }

    public List<Tokens> analyze(String input) {
        tokens.clear();
        String[] lines = input.split("\n");
       
        for (int lineNum = 0; lineNum < lines.length; lineNum ++){
            analyzeLine(lines[lineNum], lineNum + 1);
           
            if(lineNum != lines.length - 1){
                addToken("\n", TokenType.LINE_BREAK, 0, lineNum + 1);            
            }
        }
        return tokens;
    }

    private void analyzeLine(String line, int lineNumber){
        int i = 0;

        while(i < line.length()){
            char c = line.charAt(i);

            //if para espacios
            if(Character.isWhitespace(c)){
                addToken(String.valueOf(c), TokenType.SPACES, i, lineNumber);
                i++; 
                continue;

            }

             //if para los 3 puntos suspensivos
             if(c == '.' && i + 2 < line.length() && line.charAt(i + 1) == '.' && line.charAt(i + 2) == '.'){
                addToken("...", TokenType.SINGLE_PUNCTUATION_SIGN, i , lineNumber);
                i +=3;
            }           
            //validar con if cada caso especial 

            //if para saltos de linea 
            
            //if para numeros decimales y enteros 

            //un if por cada token

        }

    }

    private void addToken(String lexeme, TokenType type, int pos, int line ){
        tokens.add(new Tokens(lexeme, type, pos, line));
    }

}
