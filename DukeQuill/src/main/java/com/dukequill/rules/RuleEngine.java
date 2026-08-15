package com.dukequill.rules;
/**
 * Clase de lógica — orquestador de reglas gramaticales y de puntuación.
 *
 * <p>Gestiona una colección de reglas que implementan la interfaz {@link Rule}
 * y las ejecuta sobre la lista de tokens del texto analizado. Cada regla es
 * responsable de detectar un tipo específico de error.</p>
 *
 * <p>Reglas activas:
 * <ul>
 *   <li>{@link InterrogationRule} — valida el uso de ¿ ... ?</li>
 *   <li>{@link ExclamationRule} — valida el uso de ¡ ... !</li>
 *   <li>{@link SpacingRule} — detecta falta de espacio después de puntuación</li>
 *   <li>{@link SpaceBeforePunctuationRule} — detecta espacio antes de puntuación</li>
 *   <li>{@link UpperCaseRule} — detecta falta de mayúscula después de punto</li>
 *   <li>{@link PeriodRule} — detecta falta de punto al final de oración</li>
 *   <li>{@link GenderAgreementRule} — detecta errores de concordancia de género</li>
 * </ul>
 * </p>
 *
 * <p>Usa el patrón Strategy — agregar una nueva regla solo requiere crear una
 * clase que implemente {@link Rule} y registrarla en el constructor.</p>
 *
 * @see Rule
 * @see RuleViolation
 */

import java.util.ArrayList;
import java.util.List;

import com.dukequill.analyzer.MorphAnalyzer;
import com.dukequill.lexer.Token;


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
