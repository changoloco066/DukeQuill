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

        for(int i = 0; i < tokens.size() - 2; i++){
            Token actual    = tokens.get(i);
            Token siguiente = tokens.get(i + 1);
            Token despues   = tokens.get(i + 2);

            // WORD seguido de LINE_BREAK
            if(actual.getType() == TokenType.WORD && siguiente.getType() == TokenType.LINE_BREAK){
                violations.add(new RuleViolation(actual, "Falta el punto final '" + actual.getLexeme() + "'", "Punto Final"));
            }

            // WORD seguido de SPACES seguido de LINE_BREAK
            if(actual.getType() == TokenType.WORD && siguiente.getType() == TokenType.SPACES && despues.getType() == TokenType.LINE_BREAK){
                violations.add(new RuleViolation(actual, "Falta el punto final '" + actual.getLexeme() + "'", "Punto Final"));
            }

            // WORD seguido de LINE_BREAK seguido de LINE_BREAK (párrafo con línea vacía)
            if(actual.getType() == TokenType.WORD && siguiente.getType() == TokenType.LINE_BREAK && despues.getType() == TokenType.LINE_BREAK){
                violations.add(new RuleViolation(actual,"Falta el punto final '" + actual.getLexeme() + "'", "Punto Final"));
            }
        }

        // Penúltimo token: WORD seguido de LINE_BREAK
        if(tokens.size() >= 2){
            Token penultimo = tokens.get(tokens.size() - 2);
            Token ultimo    = tokens.get(tokens.size() - 1);

            if(penultimo.getType() == TokenType.WORD && ultimo.getType() == TokenType.LINE_BREAK){
                violations.add(new RuleViolation(penultimo, "Falta el punto final '" + penultimo.getLexeme() + "'", "Punto Final"));
            }
        }

        // Último token: WORD sin nada después
        Token ultimo = tokens.get(tokens.size() - 1);
        if(ultimo.getType() == TokenType.WORD){
            violations.add(new RuleViolation(ultimo, "Falta el punto final '" + ultimo.getLexeme() + "'", "Punto Final"));
        }

        return violations;
    }
}
