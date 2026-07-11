package com.dukequill.lexer;

/**
 * Clase de objeto — representa una unidad léxica del texto analizado.
 *
 * <p>Cada token es el resultado de tokenizar una palabra, signo o espacio
 * del texto de entrada. Contiene el texto original (lexema), su clasificación
 * ({@link TokenType}), y su posición dentro del texto para localizar errores.</p>
 *
 * <p>Producida por: {@link Lexer}</p>
 * <p>Consumida por: {@link com.dukequill.analyzer.SpellChecker},
 * {@link com.dukequill.rules.RuleEngine}</p>
 *
 * @see TokenType
 * @see Lexer
 */

public class Token {
      private String lexeme;
    private TokenType type;
    private int position;
    private int line;

    public Token(String lexeme, TokenType type, int position, int line) {
        this.lexeme = lexeme;
        this.type = type;
        this.position = position;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return lexeme + " | " + type + " | " + position + " | " + line;
    }
}
