package com.dukequill.analyzer.algorithms;
/**
 * Clase de algoritmo — calcula la distancia de edición entre dos palabras.
 *
 * <p>Implementación del algoritmo Damerau-Levenshtein, una extensión del
 * Levenshtein clásico que agrega la operación de transposición (intercambio
 * de dos letras adyacentes). Esto mejora la detección de errores tipográficos
 * comunes como "priemro" → "primero".</p>
 *
 * <p>Operaciones soportadas:
 * <ul>
 *   <li>Inserción — "cas" → "casa"</li>
 *   <li>Eliminación — "casaa" → "casa"</li>
 *   <li>Sustitución — "cesa" → "casa"</li>
 *   <li>Transposición — "csaa" → "casa"</li>
 * </ul>
 * </p>
 *
 * <p>Consumido por: {@link com.dukequill.analyzer.SpellChecker}</p>
 *
 * @see com.dukequill.analyzer.SpellChecker
 */

public class DamerauLevenshtein {
     public int calculate(String a, String b ){
        int [][] dp = new int[a.length() + 1][b.length() + 1];

        // inicializar primera fila
        for(int i = 0; i <= a.length(); i++) dp[i][0] = i;
        
        // inicializar primera columna
        for(int j = 0; j <= b.length(); j++) dp[0][j] = j;

        // llenar la matriz
        for(int i = 1; i <= a.length(); i++){
            for(int j = 1; j <= b.length(); j++){
                
                if(a.charAt(i-1) == b.charAt(j-1)){
                    dp[i][j] = dp[i-1][j-1]; // son iguales, sin costo
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],   // reemplazo
                                Math.min(dp[i-1][j],        // eliminación
                                            dp[i][j-1]));       // inserción
                }

                if(i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)){
                    dp [i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }

    return dp[a.length()][b.length()];
    }
}
