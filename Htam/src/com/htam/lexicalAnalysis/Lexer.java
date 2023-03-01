package com.htam.lexicalAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

import static com.htam.lexicalAnalysis.TokenType.*;

public class Lexer {

    // Instance Variables
    private final String source;
    private final ArrayList<Lexeme> lexemes;
    private final HashMap<String, TokenType> keywords;

    private int currentPosition;
    private int startOfCurrentLexeme;
    private int lineNumber;

    // Constructor
    public Lexer(String source) {
        this.source = source;
        this.lexemes = new ArrayList<>();
        this.keywords = getKeywords();

        this.currentPosition = 0;
        this.startOfCurrentLexeme = 0;
        this.lineNumber = 1;
    }

    // Populating Keywords
    private HashMap<String, TokenType> getKeywords() {
        HashMap<String, TokenType> keywords = new HashMap<>();
        return keywords;
    }

    // Core Helper Methods

    private Lexeme getNextLexeme() {

        char c = advance();

        switch (c) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                return null;

            // Single-Character Tokens

            case 'ν':
                return new Lexeme(NU_VAR_INITIAL);
            case '#':
                return new Lexeme(POUND_NUM, lineNumber);
            case '¨':
                return new Lexeme(UMLAUT_DOUBLE, lineNumber);
            case '§':
                return new Lexeme(EUROPEAN_S_STRING, lineNumber);
            case 'ß':
                return new Lexeme(GERMAN_B_BOOLEAN, lineNumber);
            case ',':
                return new Lexeme(COMMA, lineNumber);
            case '.':
                return new Lexeme(PERIOD, lineNumber);

            case 'τ':
                return new Lexeme(TAU_TRUE, lineNumber);
            case 'Π':
                return new Lexeme(CAPITAL_PI_FALSE, lineNumber);
            case 'θ':
                return new Lexeme(THETA, lineNumber);
            case 'σ':
                return new Lexeme(LOWERCASE_SIGMA_FXN, lineNumber);
            case 'π':
                return new Lexeme(LOWERCASE_PI_PRINT, lineNumber);
            case '&':
                return new Lexeme(AND_SYMBOL, lineNumber);

            case '(':
                return new Lexeme(OPEN_PAREN, lineNumber);
            case ')':
                return new Lexeme(CLOSE_PAREN, lineNumber);
            case '{':
                return new Lexeme(OPEN_CURL, lineNumber);
            case '}':
                return new Lexeme(CLOSE_CURL, lineNumber);
            case '[':
                return new Lexeme(OPEN_SQUARE, lineNumber);
            case ']':
                return new Lexeme(CLOSE_SQUARE, lineNumber);
            case ':':
                return new Lexeme(COLON, lineNumber);

            case 'μ':
                return new Lexeme(MU_MULTI, lineNumber);
            case 'δ':
                return new Lexeme(DELTA_DIV, lineNumber);
            case '%':
                return new Lexeme(MOD, lineNumber);
            case 'α':
                return new Lexeme(ALPHA_AND, lineNumber);
            case 'Ω':
                return new Lexeme(OMEGA_OR, lineNumber);
            case 'β':
                return new Lexeme(BETA_NOT, lineNumber);
            case 'ψ':
                return new Lexeme(PSI_CLASS, lineNumber);
            case 'χ':
                return new Lexeme(CHI_FXN, lineNumber);
            case 'ρ':
                return new Lexeme(RHO_RETURN, lineNumber);
            case 'ς':
                return new Lexeme(LOWERCASE_SIGMA_WHILE, lineNumber);
            case 'φ':
                return new Lexeme(PHI_FOR, lineNumber);
            case '…':
                return new Lexeme(ELIPSIS_NOTEQUAL, lineNumber);
            case 'κ':
                return new Lexeme(KAPPA_BREAK, lineNumber);
            case '«':
                return new Lexeme(DOUBLE_ARROW_OPEN, lineNumber);
            case '»':
                return new Lexeme(DOUBLE_ARROW_CLOSE, lineNumber);
            case 'ε':
                return new Lexeme(EPSILON_ELSE, lineNumber);
            case 'ℓ':
                return new Lexeme(CURLY_LENGTH, lineNumber);

            // Double-Character Tokens

            case 'υ':
                return new Lexeme(match('²') ? UPSILON_SQUARED_MATRIX_INITIAL : UPSILON_ARR_INITIAL, lineNumber);
            case '•':
                return new Lexeme(match('•') ? EQUALS_EQUALS : EQUALS, lineNumber);
            case 'ο':
                return new Lexeme(match('•') ? GREATER_EQUAL : GREATER_THAN, lineNumber);
            case '˙':
                return new Lexeme(match('•') ? LESS_EQUAL : LESS_THAN, lineNumber);
            case 'ι':
                return new Lexeme(match('ε') ? IOTA_EPSILON : IOTA_IF, lineNumber);

            case 'Σ':
                if (match('+')) {
                    if (match('Σ')) {
                        return new Lexeme(SIGMA_INCREMENT_ADD, lineNumber);
                    }
                    return new Lexeme(SIGMA_ADD, lineNumber);
                } else if (match('-')) {
                    if (match('Σ')) {
                        return new Lexeme(SIGMA_INCREMENT_MINUS, lineNumber);
                    }
                    return new Lexeme(SIGMA_MINUS, lineNumber);
                } else {
                    return new Lexeme(SIGMA, lineNumber);
                }

            case '|':
                if (match('|')) {
                    if (match('|')) {
                        return new Lexeme(MULTI_BAR, lineNumber);
                    }
                } else {
                    return new Lexeme(VERTICAL_BAR, lineNumber);
                }

            case '"':
                return lexString();

            default:
                if (isDigit(c)) return lexNumber();
                else if (isAlpha(c)) return lexIdentifierOrKeyword();
                else error(lineNumber, "Unexpected character: " + c);
        }

        return null;
    }

    private Lexeme lexNumber() {
        boolean isInteger = true;
        while (isDigit(peek())) advance();

        if (peek() == '.') {
            if (!isDigit(peekNext())) error(lineNumber, "Malformed real number (ends in decimal point).");
            advance();
            while (isDigit(peek())) advance();
        }

        String numberString = source.substring(startOfCurrentLexeme, currentPosition);
        double num = Double.parseDouble(numberString);
        return new Lexeme(NUMBER, lineNumber, num);

    }

    private Lexeme lexString() {
        while (peek() != '"') advance();

        if (isAtEnd()) error(lineNumber, "Improper string ending.");

        String str = source.substring(startOfCurrentLexeme, currentPosition);
        currentPosition++;
        return new Lexeme(STRING, lineNumber, str + '"');
    }

    private Lexeme lexIdentifierOrKeyword() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(startOfCurrentLexeme, currentPosition);

        return new Lexeme(IDENTIFIER, lineNumber, text);
    }

    // Character Classification Methods:

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Main lex() Function:

    public ArrayList<Lexeme> lex() {
        while (!isAtEnd()) {
            startOfCurrentLexeme = currentPosition;
            Lexeme nextLexeme = getNextLexeme();
            if (nextLexeme != null) lexemes.add(nextLexeme);
        }
        lexemes.add(new Lexeme(EOF, lineNumber));
        return this.lexemes;
    }

    // Smaller lexing functions called by lex():

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(currentPosition);
    }

    private char peekNext() {
        if (currentPosition + 1 >= source.length()) return '\0';
        return source.charAt(currentPosition + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(currentPosition) != expected) return false;
        currentPosition++;
        return true;
    }

    private char advance() {
        char currentChar = source.charAt(currentPosition);
        if (currentChar == '\n' || currentChar == '\r') lineNumber++;
        currentPosition++;
        return currentChar;
    }

    private boolean isAtEnd() {
        return currentPosition >= source.length();
    }

    // Printing:

    public void printLexemes() {
        System.out.println("Lexemes found: ");
        for (Lexeme i : this.lexemes) {
            System.out.println(i);
        }
    }

    // Error Reporting:

    private void error(int lineNumber, String message) {
        System.err.println("Syntax error at line " + lineNumber + ": " + message);
    }

    private void error(int lineNumber, Lexeme lexeme, String message) {
        System.err.println("Syntax error at " + lexeme + ": " + message);
    }


}
