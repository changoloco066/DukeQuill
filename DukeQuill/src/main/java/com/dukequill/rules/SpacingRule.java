package com.dukequill.rules;

import java.util.List;
import java.util.ArrayList;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;


public class SpacingRule implements Rule{

    @Override
    public String getRuleName() {
        return "Espaciado";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens) {
        List<RuleViolation> violations = new ArrayList<>();
        
        for(int i = 0; i < tokens.size() - 1; i ++){
            Token actual = tokens.get(i);
            Token siguiente = tokens.get(i + 1);

            if(actual.getType() == TokenType.SINGLE_PUNCTUATION_SIGN && siguiente.getType() != TokenType.SPACES && siguiente.getType() != TokenType.LINE_BREAK){
                violations.add(new RuleViolation(actual, "Falta un espacio después del signo ' " + actual.getLexeme() + " '", "Espaciado"));
            }
        }
       return violations;
   }
}
