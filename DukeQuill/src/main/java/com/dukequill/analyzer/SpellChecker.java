package com.dukequill.analyzer;

import java.util.*;
import java.util.stream.Collectors;

import com.dukequill.analyzer.algorithms.DamerauLevenshtein;
import com.dukequill.analyzer.algorithms.Levenshtein;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;
import com.dukequill.analyzer.algorithms.Levenshtein;;

public class SpellChecker {
    private final Dictionary dictionary;
    private final List<SpellErrors> errors;
    private final MorphAnalyzer morphAnalyzer;
    private final DamerauLevenshtein damerauLevenshtein;
    private Set<String> ignoredWords;

    public SpellChecker(Dictionary dictionary, MorphAnalyzer morphAnalyzer) throws Exception{
        this.dictionary = dictionary;
        this.errors = new ArrayList<>();
        this.morphAnalyzer =  morphAnalyzer;
        this.damerauLevenshtein = new DamerauLevenshtein();
        this.ignoredWords = new HashSet<>();
    }

    public List<SpellErrors> check(List<Token> tokens) throws Exception{
        errors.clear();
        for(Token token : tokens){
            if(token.getType() == TokenType.WORD){
                if(!dictionary.contains(token.getLexeme()) && !dictionary.contains(token.getLexeme().toLowerCase())){
                    if(!morphAnalyzer.isValidWord(token.getLexeme()) && !ignoredWords.contains(token.getLexeme() .toLowerCase())){
                        errors.add(new SpellErrors(token));
                    }
                }
            }
        }
        return errors;
    }

    public void ignoredWord(String word){
        ignoredWords.add(word.toLowerCase());
    }

    public void removeIgnoredWord(String word){
        ignoredWords.remove(word.toLowerCase());
    }

    public List<String> getSuggestions(String word){

        Map<String, Integer> candidates = new HashMap<>();
        int maxDistance = word.length() <= 5 ? 1 : word.length() <= 8 ? 2 : 3;

        for(String dictWord : dictionary.getWords()){
        int distance = damerauLevenshtein.calculate(word, dictWord);
            if(distance <= maxDistance){
                candidates.put(dictWord, distance);        
             }
        }
        return candidates.entrySet()              // convierte el Map en una lista de pares (palabra, distancia)
        .stream()                                // crea un "flujo" de datos para procesarlos
        .sorted(Map.Entry.comparingByValue())   // ordena por el valor (la distancia)
        .limit(5)                     // toma solo los primeros 5
        .map(Map.Entry::getKey)              // de cada par, toma solo la palabra (la clave)
        .collect(Collectors.toList());      // junta todo en una List<String>
    }
}


/*
    // esto:
    int maxDistance = word.length() <= 5 ? 1 : word.length() <= 8 ? 2 : 3;

    // es equivalente a: 
    int maxDistance;
    if(word.length() <= 5) {
        maxDistance = 1;
    } else if(word.length() <= 8) {
        maxDistance = 2;
    } else {
        maxDistance = 3;
    }

*/