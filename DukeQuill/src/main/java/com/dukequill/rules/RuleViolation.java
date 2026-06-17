package com.dukequill.rules;
import com.dukequill.lexer.Token;

public class RuleViolation {

    private final Token token;
    private final String message;

    public RuleViolation (Token token, String message){
        this.token = token;
        this.message = message;
    }

    public Token getToken(){
        return token;
    }

    public String getMessage(){
       return message;
    }

    public int getLine(){
        return token.getLine();
    }

    public String getLexeme(){
        return token.getLexeme();
    }
    

    @Override
    public String toString() {
        return "[Línea " + token.getLine() + "] " + message + " → " + token.getLexeme();
    }
}
