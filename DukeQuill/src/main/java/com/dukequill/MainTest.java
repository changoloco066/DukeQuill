package com.dukequill;

import com.dukequill.lexer.Lexer;
import com.dukequill.lexer.Token;
import java.util.List;

public class MainTest {
public static void main(String[] args) throws Exception {
    System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
    
    Lexer lexer = new Lexer();
    String texto = "1.- \"Yo me levanto para cumplir con los oficios propios de un hombre.\"\n" +
    "¿Has nacido tú para deleitarte y no ocuparte de trabajar nada ?, ¿No ves cómo esos árboles, los pájaros, las hormigas, las arañas, las abejas, cada cual por su parte, se esmeran en perfeccionar su labor ? ¿Y tú no querrás hacer los oficios propios de un hombre ni te darás prisa en poner por obra lo que es conforme a tu naturaleza ?\n" +
    "Pero también es necesario descansar, y la naturaleza prescribió en esto su regla, en esto último pasas más allá de lo que es regular y suficiente; y en lo que te toca a tu deber no lo haces así, si no te quedas mucho más atrás de lo que pueden tus fuerzas.\n" +
    "De veras no te amas a ti mismo, que si en realidad te amases, amarías también tu naturaleza y abrazarías sus dictámenes.";

    List<Token> tokens = lexer.analyze(texto);
    for(Token t : tokens){
        System.out.println(t);
    }
}
}