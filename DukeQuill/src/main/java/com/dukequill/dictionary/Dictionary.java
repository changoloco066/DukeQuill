package com.dukequill.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Dictionary {

    private Set<String> words = new HashSet<>();

    public void loadDictionary() {
       BufferedReader br = new BufferedReader(new FileReader(DukeQuill/src/resources/dictionary/Spanish.dic));
       String line;

       while((line = br.readLine()) != null){

       }
       br.close();
    }

    public boolean contains(String word) {
        return words.contains(word);
        
    }
}