package com.dukequill.rules;

import java.util.List;
import com.dukequill.lexer.Token;

public interface Rule {
    List<RuleViolation> check(List<Token> tokens);
    String getRuleName();
}
