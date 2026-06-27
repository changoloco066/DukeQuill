package com.dukequill.analyzer;

import java.util.*;
import java.util.stream.Collectors;

import com.dukequill.analyzer.algorithms.Levenshtein;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;
import com.dukequill.analyzer.algorithms.Levenshtein;;

public class SpellChecker {
    private final Dictionary dictionary;
    private final List<SpellErrors> errors;
    private final MorphAnalyzer morphAnalyzer;
    private final Levenshtein levenshtein;

    public SpellChecker(Dictionary dictionary) throws Exception{
        this.dictionary = dictionary;
        this.errors = new ArrayList<>();
        this.morphAnalyzer = new MorphAnalyzer();
        this.levenshtein = new Levenshtein();
    }

    public List<SpellErrors> check(List<Token> tokens) throws Exception{
        errors.clear();
        for(Token token : tokens){
            if(token.getType() == TokenType.WORD){
                if(!dictionary.contains(token.getLexeme()) && !dictionary.contains(token.getLexeme().toLowerCase())){
                    if(!morphAnalyzer.isValidWord(token.getLexeme())){
                        errors.add(new SpellErrors(token));
                    }
                }
            }
        }
        return errors;
    }

    public List<String> getSuggestions(String word){

        Map<String, Integer> candidates = new HashMap<>();

        for(String dictWord : dictionary.getWords()){
        int distance = levenshtein.calculate(word, dictWord);
            if(distance <= 2){
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
