package com.dukequill.rules;

import java.util.List;
import java.util.ArrayList;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;


public class PeriodRule implements Rule{
    @Override
    public String getRuleName() {
        return "Punto Final";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens) {
        List<RuleViolation> violations = new ArrayList<>();
        
        //Mensaje de depuracion 
            for(Token token : tokens){
                    System.out.printf("%-30s [%s]%n",token.getType(),token.getLexeme().replace("\n", "\\n"));
         }
        ///
        for(int i = 1; i < tokens.size() - 1; i ++){
                //Token antes = tokens.get(i - 1);
                Token actual = tokens.get(i);
                Token despues = tokens.get(i + 1);

                if(actual.getType() == TokenType.WORD && despues.getType() == TokenType.LINE_BREAK){
                    violations.add(new RuleViolation(actual, "Falta el punto final ' " + actual.getLexeme() + " '", "Punto Final"));
                }
            }

            Token ultimo = tokens.get(tokens.size() - 1);
            if(ultimo.getType() == TokenType.WORD){
                violations.add(new RuleViolation(ultimo, "Falta el punto final '" + ultimo.getLexeme() + "'", "Punto Final"));
        }
        return violations;
    }
}
