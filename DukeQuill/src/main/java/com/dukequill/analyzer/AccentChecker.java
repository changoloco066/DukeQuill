package com.dukequill.analyzer;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Spanish;
import org.languagetool.rules.RuleMatch;

public class AccentChecker {
    private final JLanguageTool langTool;

    public AccentChecker() throws Exception {
        langTool = new JLanguageTool(new Spanish());
    }

    public List<AccentViolations> check(String text) throws Exception{
        List<AccentViolations> violations = new ArrayList<>();
        List<RuleMatch> matches = langTool.check(text);

        for(RuleMatch match : matches){
            String ruleId = match.getRule().getId();
            if(ruleId.equals("MORFOLOGIK_RULE_ES")){
                String original = text.substring(match.getFromPos(), match.getToPos());
                String suggestion = match.getSuggestedReplacements().isEmpty() ? "" : match.getSuggestedReplacements().get(0);
                if(original.length() == suggestion.length()) {
                     violations.add (new AccentViolations(
                    match.getFromPos(), match.getMessage(), suggestion, text.substring(match.getFromPos(), match.getToPos()) 
                 ));
                }
            }
        }
        return violations;
    }
}
