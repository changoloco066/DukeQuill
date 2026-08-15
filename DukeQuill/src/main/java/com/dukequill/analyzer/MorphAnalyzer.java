package com.dukequill.analyzer;
/**
 * Clase de lógica — analizador morfológico del español usando LanguageTool.
 *
 * <p>Usa el componente Tagger de LanguageTool para analizar la morfología
 * de palabras individuales. Permite determinar si una palabra es una forma
 * conjugada válida del español (e.g. "comieron" → verbo de "comer") y
 * obtener su género gramatical para detección de concordancia.</p>
 *
 *  <p>Etiquetas morfológicas usadas:
 * <ul>
 *   <li>NCM — Nombre Común Masculino | NCF — Nombre Común Femenino</li>
 *   <li>AOM — Adjetivo Ordinal Masculino | AOF — Adjetivo Ordinal Femenino</li>
 *   <li>DEM — Determinante Masculino | DEF — Determinante Femenino</li>
 *   <li>/null — palabra no reconocida por LanguageTool</li>
 * </ul>
 * </p>
 * <p>{@code tagger.tag(List.of(word))} — solicita al Tagger el análisis 
 * morfológico de una palabra y devuelve sus etiquetas gramaticales.</p>
 *   
 *
 * <p>Consumido por: {@link com.dukequill.analyzer.SpellChecker},
 * {@link com.dukequill.rules.GenderAgreementRule}</p>
 *
 * @see com.dukequill.analyzer.SpellChecker
 * @see com.dukequill.rules.GenderAgreementRule
 */

import org.languagetool.language.Spanish;
import org.languagetool.tagging.Tagger;
import java.util.List;

public class MorphAnalyzer {

    private final Tagger tagger;
    /*
    Tagger: componente de LanguageTool que analiza morfológicamente cada palabra
    y devuelve su etiqueta gramatical (POS tag) con información de género, número y tiempo verbal
     */

    public MorphAnalyzer() throws Exception{
        tagger = new Spanish().createDefaultTagger();
    }
    public boolean isValidWord(String word) throws Exception {
        List<?> tags = tagger.tag(List.of(word));
        if(tags.isEmpty()) return false;
        String tagStr = tags.get(0).toString();
        return !tagStr.contains("/null");
    }

    public String getGender(String word) throws Exception {
        List<?> tags = tagger.tag(List.of(word));
        if(tags.isEmpty()) return "unknown";
        String tagStr = tags.get(0).toString();
        if(tagStr.contains("/NCM") || tagStr.contains("/AOM") || tagStr.contains("/DEM")) return "M";
        if(tagStr.contains("/NCF") || tagStr.contains("/AOF") || tagStr.contains("/DEF")) return "F";
        return "unknown";

        /*
            " tagger.tag(List.of(word))"  le pide al Tagger que analice la palabra y devuelve una lista de etiquetas morfológicas
            NCM = Nombre Común Masculino        NCF = Nombre común Femenino
            AOM = Adjetivo Ordinal Masculino    AOF = Adjetivo Ordinal Femenino
            DEM = Determinante Masculino        DEF = Determinante Femenino     
        */
    }
}

