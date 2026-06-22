package com.dukequill.analyzer;

import java.util.*;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class SpellChecker {
    private final Dictionary dictionary;
    private final List<SpellErrors> errors;
    private final MorphAnalyzer morphAnalyzer;

    public SpellChecker(Dictionary dictionary) throws Exception{
        this.dictionary = dictionary;
        this.errors = new ArrayList<>();
        this.morphAnalyzer = new MorphAnalyzer();

    }

    public List<SpellErrors> check(List<Token> tokens) throws Exception{
        errors.clear();
        for(Token token : tokens){
            if(token.getType() == TokenType.WORD){
                if(!dictionary.contains(token.getLexeme()) && !dictionary.contains(token.getLexeme().toLowerCase())){
                    if(!morphAnalyzer.isValidWord(token.getLexeme())){
                        errors.add(new SpellErrors(token));
                    }
                }
            }
        }
        return errors;
    }
}
