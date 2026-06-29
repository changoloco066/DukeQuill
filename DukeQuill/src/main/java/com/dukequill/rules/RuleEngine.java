package com.dukequill.rules;

import java.util.ArrayList;
import java.util.List;

import com.dukequill.analyzer.MorphAnalyzer;
import com.dukequill.lexer.Token;
import com.dukequill.rules.*;

public class RuleEngine {
    private List<Rule> rules;

    public RuleEngine(MorphAnalyzer morphAnalyzer){
        rules = new ArrayList<>();
        rules.add(new InterrogationRule());
        rules.add(new ExclamationRule());
        rules.add(new SpacingRule());
        rules.add(new UpperCaseRule());
        rules.add(new SpaceBeforePunctuationRule());
        rules.add(new PeriodRule());
        rules.add(new GenderAgreementRule(morphAnalyzer));
    }

    public List<RuleViolation> check(List<Token> tokens) throws Exception{
        List<RuleViolation> allViolations = new ArrayList<>();
        for (Rule rule : rules){
            allViolations.addAll(rule.check(tokens));
        }
        return allViolations;
    } 
}
