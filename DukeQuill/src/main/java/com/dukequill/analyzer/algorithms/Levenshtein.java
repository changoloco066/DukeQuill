package com.dukequill.analyzer.algorithms;

import java.util.*;
import com.dukequill.dictionary.Dictionary;
import com.dukequill.lexer.Token;
import com.dukequill.lexer.TokenType;

public class Levenshtein {
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
            }
        }

    return dp[a.length()][b.length()];
    }
}
    





