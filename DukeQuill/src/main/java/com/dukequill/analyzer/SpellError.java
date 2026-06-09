package com.dukequill.analyzer;

import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class SpellError {
    private final Token token;
 
    public SpellError(Token token) {
       this.token = token;
    }
 
  
    public String getLexeme() {
        return token.getLexeme();
    }

    public TokenType getType() {
        return token.getType();
    }

    public int getPosition() {
        return token.getPosition();
    }

    public int getLine() {
        return token.getLine();
    }

    @Override
    public String toString() {
        return token.getLexeme() + " | " + token.getType() + " | " + token.getPosition() + " | " + token.getLine();
    }
}
