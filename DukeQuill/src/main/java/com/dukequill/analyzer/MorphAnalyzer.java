package com.dukequill.analyzer;

import org.languagetool.language.Spanish;
import org.languagetool.tagging.Tagger;
import java.util.List;

public class MorphAnalyzer {

    private final Tagger tagger;

    public MorphAnalyzer() throws Exception{
        tagger = new Spanish().createDefaultTagger();
    }
    public boolean isValidWord(String word) throws Exception {
        List<?> tags = tagger.tag(List.of(word));
        if(tags.isEmpty()) return false;
        String tagStr = tags.get(0).toString();
        return !tagStr.contains("/null");
    }
}

