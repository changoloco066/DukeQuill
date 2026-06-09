/*

words - variable asignada para la coleccion del diccionario completo
word - variable asignada para la palabra que recien se acaba de leer 

*/

package com.dukequill.dictionary;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Dictionary {

    private Set<String> words = new HashSet<>();

    public void loadDictionary() {
        boolean firstLine = true;
        try(BufferedReader br = new BufferedReader(new FileReader("DukeQuill/src/resources/dictionary/Spanish.dic"))){
        String line;

        while((line = br.readLine()) != null){
            if(firstLine){
                firstLine = false;
                continue;
            } 
            String word = line.split("/")[0];
            words.add(word);
        }

        }catch(FileNotFoundException e){
            System.out.println("No se encontro el archivo");
        }catch(IOException e){
            System.out.println("Algo salio mal :(");
        }
        
    }

    public boolean contains(String word) {
        return words.contains(word);
        
    }
}