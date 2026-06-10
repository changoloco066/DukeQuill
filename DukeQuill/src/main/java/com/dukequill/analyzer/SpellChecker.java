package com.dukequill.analyzer;

import java.util.*;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;
import com.dukequill.lexer.*;

public class SpellChecker {
    private final Dictionary dictionary;
    private final List<SpellErrors> errors;

    public SpellChecker(Dictionary dictionary){
        this.dictionary = dictionary;
        this.errors = new ArrayList<>();

    }

    public List<SpellErrors> check(List<Token> tokens){

        for(Token token : tokens){
            if(token.getType() == TokenType.WORD){
                if(!dictionary.contains(token.getLexeme())){
                    errors.add(new SpellErrors(token));
                }
            }
        }
        return errors;
    }


}
