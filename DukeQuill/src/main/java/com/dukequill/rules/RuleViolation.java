package com.dukequill.rules;
import com.dukequill.lexer.Token;

public class RuleViolation {

    private final Token token;
    private final String message;
    private String ruleName;

    public RuleViolation (Token token, String message, String ruleName){
        this.token = token;
        this.message = message;
        this.ruleName = ruleName;

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

    public Token getRuleViolation(){
        return token.getRuleViolation();
    }

    public String getRuleName(){
        return ruleName;
    }

    public int getPosition(){
        return token.getPosition();
    }
    

    @Override
    public String toString() {
        return "[Línea " + token.getLine() + "] " + message + " → " + token.getLexeme();
    }
}
