package com.dukequill.lexer;
/*

*/

public enum TokenType {
    WORD, 
    NUMBER_INT,
    NUMBER_DEC,

    //DIACRITIC_SIGN,  la tilde y diéresis nunca aparecen solas en el texto, siempre van dentro de una letra

    SINGLE_PUNCTUATION_SIGN,
    AUX_SIGN,

    OPEN_PUNCTUATION_SIGN,
    OPEN_AUX_SIGN,

    CLOSE_PUNCTUATION_SIGN,
    CLOSE_AUX_SIGN,

    SPACES,
    LINE_BREAK,

    UNKNOWN
}
