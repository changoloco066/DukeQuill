package com.dukequill.analyzer;

import com.dukequill.*;
import com.dukequill.analyzer.SpellChecker;
import com.dukequill.analyzer.SpellErrors;
import com.dukequill.lexer.Token;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Lexer;

import org.languagetool.language.Spanish;
import org.languagetool.tagging.Tagger;
import java.util.List;

public class MorphAnalyzer {

    private final Tagger tagger;

    public MorphAnalyzer() throws Exception{
        tagger = new Spanish().createDefaultTagger();
    }

    public boolean isValidWord(String word) throws Exception{
        List<?> tags = tagger.tag(List.of(word));
        return !tags.isEmpty();
    }
}

