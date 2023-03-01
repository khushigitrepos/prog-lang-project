package com.htam.environment;

import com.htam.lexicalAnalysis.*;

import java.util.ArrayList;
import java.util.HashMap;

import static com.htam.lexicalAnalysis.TokenType.*;

public class EnvironmentTest {

    public static void main(String[] args) {

        Environment global = new Environment(null);
        Environment local1 = new Environment(global);
        Environment local2 = new Environment(global);


        // TODO: So here's my conundrum. I have a variable, and I can add it to an environment and
        // TODO: "update" its value. But if I'm not giving it a value, what am I updating???

        Lexeme x = new Lexeme(IDENTIFIER, "x");
        Lexeme numthree = new Lexeme(NUMBER, 3.0);
        Lexeme alsox = new Lexeme(IDENTIFIER, "x");
        Lexeme numfive = new Lexeme(NUMBER, 5.0);

        local1.add(x, numthree);
        local2.add(alsox, numfive);

        local1.lookup(alsox);
        global.lookup(x);

        local1.update(x, numfive);

        if (x.eqs(alsox) == true) {
            System.out.println("Equal fxn works!");
        }
    }

}
