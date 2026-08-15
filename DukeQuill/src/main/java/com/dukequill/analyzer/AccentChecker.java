package com.dukequill.analyzer;

/**
 * Clase de lógica — detector de errores de acentuación contextual.
 *
 * <p>Usa LanguageTool ({@link org.languagetool.JLanguageTool}) para analizar
 * el texto completo y detectar palabras con acento incorrecto según su contexto
 * gramatical. A diferencia del SpellChecker que trabaja palabra por palabra,
 * esta clase necesita el texto completo para determinar si el acento es correcto.</p>
 *
 * <p>Filtra solo los errores de tipo MORFOLOGIK_RULE_ES donde la sugerencia
 * tiene la misma longitud que la palabra original, indicando un cambio de acento.</p>
 *
 * <p>Produce: {@link AccentViolations}</p>
 *
 * @see AccentViolations
 */

import java.util.ArrayList;
import java.util.List;

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
