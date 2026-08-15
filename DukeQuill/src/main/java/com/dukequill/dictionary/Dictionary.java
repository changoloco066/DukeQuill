/**
 * Clase de lógica — gestor del diccionario Hunspell del español.
 *
 * <p>Carga el archivo Spanish.dic en memoria usando un {@link java.util.HashSet}
 * para permitir búsquedas en tiempo O(1). Cada entrada del .dic se procesa
 * quitando los códigos morfológicos (e.g. "comer/ABC" → "comer").</p>
 *
 * <p>Expone las palabras cargadas via {@code getWords()} como un conjunto
 * no modificable para que {@link com.dukequill.analyzer.SpellChecker}
 * pueda iterar sobre ellas al generar sugerencias.</p>
 *
 * @see com.dukequill.analyzer.SpellChecker

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

    public Set<String> getWords(){
        return Collections.unmodifiableSet(words);
    }

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