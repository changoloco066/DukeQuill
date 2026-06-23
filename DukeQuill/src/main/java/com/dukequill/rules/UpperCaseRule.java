package com.dukequill.rules;

import java.util.List;
import java.util.ArrayList;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class UpperCaseRule implements Rule {
    @Override
    public String getRuleName() {
        return "Mayúscula";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens) {
        List<RuleViolation> violations = new ArrayList<>();
        
        for(int i = 0; i < tokens.size() - 2; i ++){
            Token actual = tokens.get(i);
            Token siguiente = tokens.get(i + 1);
            Token despues = tokens.get(i + 2);

            if(actual.getType() == TokenType.SINGLE_PUNCTUATION_SIGN && actual.getLexeme().equals(".") && siguiente.getType() == TokenType.SPACES && despues.getType() == TokenType.WORD && !Character.isUpperCase(despues.getLexeme().charAt(0))){
                violations.add(new RuleViolation(actual, "Falta una mayúscula después del punto ' " + actual.getLexeme() + " '", "Mayúscula"));
            }
        }
       return violations;
   }
}
