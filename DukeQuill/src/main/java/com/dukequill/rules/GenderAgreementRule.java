package com.dukequill.rules;
/**
 * Clase de regla — detecta errores de concordancia de género.
 *
 * <p>Verifica que los artículos concuerden en género con el sustantivo
 * que les sigue. Por ejemplo detecta "el casa" (masculino + femenino)
 * o "la perro" (femenino + masculino) como errores.</p>
 *
 * <p>Usa {@link com.dukequill.analyzer.MorphAnalyzer} para consultar
 * el género gramatical de cada sustantivo vía LanguageTool.</p>
 *
 * <p>Implementa: {@link Rule}</p>
 *
 * @see Rule
 * @see com.dukequill.analyzer.MorphAnalyzer
 */

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.dukequill.analyzer.MorphAnalyzer;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;


public class GenderAgreementRule implements Rule {
    private final MorphAnalyzer morphAnalyzer;

    // Conjuntos de artículos masculinos y femeninos para detectar concordancia
    private static final Set<String> MASCULINOS = Set.of("el", "un", "los", "unos");
    private static final Set<String> FEMENINOS = Set.of("la", "una", "las", "unas");

    public GenderAgreementRule(MorphAnalyzer morphAnalyzer) {
        this.morphAnalyzer = morphAnalyzer;
    }

    @Override
    public String getRuleName() {
        return "Concordancia de género";
    }

    @Override
    public List<RuleViolation> check(List<Token> tokens){
        List<RuleViolation> violations = new ArrayList<>();
        
        // Recorre los tokens buscando artículos
        for(int i = 0; i < tokens.size() - 1; i++){
        Token actual = tokens.get(i);
        Token siguiente = tokens.get(i + 1);

            if(actual.getType() == TokenType.WORD && MASCULINOS.contains(actual.getLexeme() .toLowerCase())){
                // Busca la siguiente palabra saltando espacios
                for(int j = i + 1; j < tokens.size(); j ++){
                    siguiente = tokens.get(j);
                    if(siguiente.getType() == TokenType.SPACES) continue; // ignora espacios
                    if(siguiente.getType() == TokenType.WORD){
                        try {
                            // Consulta el género de la palabra siguiente con LanguageTool
                            String genero = morphAnalyzer.getGender(siguiente.getLexeme());
                             // Si el género es femenino, hay error de concordancia
                            if(genero.equals("F")){       
                                violations.add(new RuleViolation(actual, "Concordancia de género: '" + actual.getLexeme() + "' no concuerda con '" 
                                + siguiente.getLexeme() + "'",  "Concordancia de género"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;  // encontró la siguiente palabra, sale del for interno
                    }
                    break;     // encontró algo que no es palabra ni espacio, sale
               }
            }

                // mismo proceso para artículos femeninos, verifica que la siguiente palabra sea masculina

            if(actual.getType() == TokenType.WORD && FEMENINOS.contains(actual.getLexeme().toLowerCase())){
                for(int j = i + 1; j < tokens.size(); j ++){
                    siguiente = tokens.get(j);
                    if(siguiente.getType() == TokenType.SPACES) continue;
                    if(siguiente.getType() == TokenType.WORD){
                        try {
                            String genero = morphAnalyzer.getGender(siguiente.getLexeme());
                            if(genero.equals("M")){
                                violations.add(new RuleViolation(actual, "Concordancia de género: '" + actual.getLexeme() + "' no concuerda con '" 
                                + siguiente.getLexeme() + "'", "Concordancia de género"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    break;
               }
            }
        }
        return violations;
    }
}