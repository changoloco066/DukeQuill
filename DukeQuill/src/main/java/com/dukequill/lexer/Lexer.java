package com.dukequill.lexer;

import java.lang.foreign.AddressLayout;
import java.util.*;

public class Lexer {

    private List<Token> tokens;

    private static final Set<Character> SINGLE_PUNCTUATION_SIGN = Set.of('.', ',', ';', ':', '…');
    private static final Set<Character> AUX_SIGN = Set.of('/', '-');
    private static final Set<Character> OPEN_PUNCTUATION_SIGN = Set.of('¿', '¡');
    private static final Set<Character> OPEN_AUX_SIGN = Set.of('(', '[', '{', '«', '“', '‘', '"', '\'');
    private static final Set<Character> CLOSE_PUNCTUATION_SIGN = Set.of('?', '!');
    private static final Set<Character> CLOSE_AUX_SIGN = Set.of(')', ']', '}', '»', '”', '’', '"', '\'');


    public Lexer(){
        tokens = new ArrayList<>();
    }

    public List<Token> analyze(String input) {
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
            int start = i;

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
                continue;
            }           

            //if para numeros decimales y enteros 
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                boolean hasDot = false;
                while (i < line.length()) {
                    char cur = line.charAt(i);
                    if (Character.isDigit(cur)) {
                        sb.append(cur);
                    } else if (cur == '.' && !hasDot) {
                        sb.append(cur);
                        hasDot = true;
                    } else {
                        break;
                    }
                    i++;
                }
                addToken(sb.toString(), TokenType.NUMBER, start, lineNumber);
                continue;
            }

            //un if por cada token

            if(SINGLE_PUNCTUATION_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.SINGLE_PUNCTUATION_SIGN, start, lineNumber);
                i ++;
                continue;
            }

            if(AUX_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.AUX_SIGN, start, lineNumber);
                i ++;
                continue;
            }

            if(OPEN_PUNCTUATION_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.OPEN_PUNCTUATION_SIGN, start, lineNumber);
                i ++; 
                continue;
            }

            if(OPEN_AUX_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.OPEN_AUX_SIGN, start, lineNumber);
                i ++;
                continue;
            }

            if(CLOSE_PUNCTUATION_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.CLOSE_PUNCTUATION_SIGN, start, lineNumber);
                i ++;
                continue;
            }

            if(CLOSE_AUX_SIGN.contains(c)){
                addToken(String.valueOf(c), TokenType.CLOSE_AUX_SIGN, start, lineNumber);
                i ++;
                continue;
            }

            if(Character.isLetter(c)){
                StringBuilder sb = new StringBuilder();
                while(i < line.length()){
                    char cur = line.charAt(i);
                    if(Character.isLetter(cur)){
                        sb.append(cur);
                    }else {
                        break;
                    }
                    i ++;
                }
                addToken(sb.toString(), TokenType.WORD, start, lineNumber);
                continue;
            }

            // desconocido
                addToken(String.valueOf(c), TokenType.UNKNOWN, start, lineNumber);
                i ++;
                continue;
        
        }

    }

    private void addToken(String lexeme, TokenType type, int pos, int line ){
        tokens.add(new Token(lexeme, type, pos, line));
    }

}
