package com.dukequill.rules;

import java.util.List;
import com.dukequill.*;
import com.dukequill.lexer.Token;
import com.dukequill.rules.RuleViolation;

public interface Rule {
    List<RuleViolation> check(List<Token> tokens);
    String getRuleName();
}
