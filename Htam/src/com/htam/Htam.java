package com.htam;


import com.htam.environment.Environment;
import com.htam.evaluator.Evaluator;

import com.htam.lexicalAnalysis.Lexeme;
import com.htam.lexicalAnalysis.Lexer;
import com.htam.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Htam {

    private static final ArrayList<String> syntaxErrorMessages = new ArrayList<>();
    private static final ArrayList<String> runtimeErrorMessages = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            if (args.length == 1) runFile(args[0]);
            else {
                System.out.println("Usage: htam Htam/testInput/variabledeclarations.htam");
                System.exit(64);
            }
        } catch (IOException exception) {
            throw new IOException(exception.toString());
        }
    }

    public static void runFile(String path) throws IOException {
        if (!path.endsWith(".htam")) path += ".htam";
        System.out.println();
        System.out.println("Running " + path + "...");
        System.out.println();
        String sourceCode = getSourceCodeFromFile(path);
        run(sourceCode);
    }

    private static String getSourceCodeFromFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes, Charset.defaultCharset());
    }

    public static void run(String sourceCode) {
        Lexer lexer = new Lexer(sourceCode);
        ArrayList<Lexeme> lexemes = lexer.lex();
        for (int i = 0; i < lexemes.size(); i++) {
            System.out.println(lexemes.get(i));
            if (i + 1 < lexemes.size() && lexemes.get(i).getLineNumber() < lexemes.get(i + 1).getLineNumber()) {
                System.out.println();
            }
        }

        Parser r = new Parser(lexemes);
        Lexeme root = r.Program();

        System.out.println();
        root.printAsTree(0);

        System.out.println();
        System.out.println();

        Environment globalEnvironment = new Environment(null);

        Evaluator evaluator = new Evaluator();

        Lexeme programResult = evaluator.eval(root, globalEnvironment);

        System.out.println();

        for (int i = 0; i < syntaxErrorMessages.size(); i++) {
            System.out.println(syntaxErrorMessages.get(i));
        }

        for (int i = 0; i < runtimeErrorMessages.size(); i++) {
            System.out.println(runtimeErrorMessages.get(i));
        }

    }


    public static void syntaxError(String message, int lineNumber) {
        syntaxErrorMessages.add("Syntax error (line" + lineNumber + "): " + message);
    }

    public static void syntaxError(String message, Lexeme lexeme) {
        syntaxErrorMessages.add("Syntax error at <<< " + lexeme + " >>> : " + message);
    }

    public static void runtimeError(String message, int lineNumber) {
        runtimeErrorMessages.add("Runtime error (line" + lineNumber + "): " + message);
        printErrors();
        System.exit(65);
    }

    public static void runtimeError(String message, Lexeme lexeme) {
        runtimeErrorMessages.add("Runtime error at <<< " + lexeme + " >>> : " + message);
        printErrors();
        System.exit(65);
    }

    private static void printErrors() {
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_RED_BACKGROUND = "\u001B[41m";
        final String ANSI_RESET = "\u001B[0m";

        for (String syntaxErrorMessage : syntaxErrorMessages) {
            System.out.println(ANSI_YELLOW + syntaxErrorMessage + ANSI_RESET);
        }

        for (String runtimeErrorMessage : runtimeErrorMessages) {
            System.out.println(ANSI_YELLOW + runtimeErrorMessage + ANSI_RESET);
        }
    }


}
