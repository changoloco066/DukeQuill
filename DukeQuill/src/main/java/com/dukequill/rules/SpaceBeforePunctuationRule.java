package com.dukequill.rules;

import java.util.List;
import java.util.ArrayList;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class SpaceBeforePunctuationRule implements Rule{
    @Override
    public String getRuleName() {
        return "Espacio antes de puntuación";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens) {
        List<RuleViolation> violations = new ArrayList<>();

        for(int i = 1; i < tokens.size(); i ++){
                Token antes = tokens.get(i - 1);
                Token actual = tokens.get(i);

                if(antes.getType() == TokenType.SPACES && actual.getType() == TokenType.SINGLE_PUNCTUATION_SIGN ){
                    violations.add(new RuleViolation(actual, "Hay un espacio antes del signo ' " + actual.getLexeme() + " '", "Espacio antes de puntuación"));
                }
            }
        return violations;
    }
}
