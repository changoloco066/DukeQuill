package com.dukequill.rules;

import java.util.List;
import java.util.ArrayList;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class InterrogationRule implements Rule {

    @Override
    public String getRuleName() {
        return "Signos de interrogación";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens) {
        List<RuleViolation> violations = new ArrayList<>();
        // lógica aquí

        boolean esperandoCierre = false;
        Token tokenApertura = null;

        for(Token token : tokens){
            if(token.getType() == TokenType.OPEN_PUNCTUATION_SIGN && token.getLexeme().equals("¿")){
                if(esperandoCierre){
                    violations.add(new RuleViolation(tokenApertura, "Falta cerrar el signo '?'", "Signos de interrogación "));
                }
                esperandoCierre = true;
                tokenApertura = token;
            }
            if(token.getType() == TokenType.CLOSE_PUNCTUATION_SIGN && token.getLexeme().equals("?")){
                if(!esperandoCierre){
                    violations.add(new RuleViolation(token, "Falta abrir con '¿'", "Signos de interrogación"));
                
                }
                esperandoCierre = false;
                
            }
        }

        if(esperandoCierre){
           violations.add(new RuleViolation(tokenApertura, "Falta cerrar el signo '?'", "Signos de interrogación "));
            }
        return violations;
    }
}