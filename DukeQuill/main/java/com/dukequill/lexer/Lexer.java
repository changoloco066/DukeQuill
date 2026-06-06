package com.dukequill.lexer;

import javax.swing.JFrame;
import javax.swing.JTable;
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


}

private void analyzeLine(){

}

private void addToken(){
    
}

}
