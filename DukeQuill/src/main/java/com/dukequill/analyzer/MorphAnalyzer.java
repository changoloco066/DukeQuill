package com.dukequill.analyzer;

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

