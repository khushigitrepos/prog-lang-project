package com.htam.lexicalAnalysis;

import com.htam.environment.Environment;

public class Lexeme {

    private Environment definingEnvironment;
    Environment parent;

    TokenType type;
    int lineNumber;

    String str;
    Double num;
    Boolean bool;

    Lexeme left;
    Lexeme right;

    // Constructors

    public Lexeme(TokenType type) {
        this.type = type;
    }

    public Lexeme(TokenType type, int lnNum) {
        this.type = type;
        this.lineNumber = lnNum;
    }

    public Lexeme(TokenType type, Double noVal) {
        this.type = type;
        this.num = noVal;
    }

    public Lexeme(TokenType type, int lineNumber, Double noVal) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.num = noVal;
    }

    public Lexeme(TokenType type, String strVal) {
        this.type = type;
        this.str = strVal;
    }

    public Lexeme(TokenType type, int lineNumber, String strVal) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.str = strVal;
    }

    public Lexeme(TokenType type, Boolean boolVal) {
        this.type = type;
        this.bool = boolVal;
    }

    public Lexeme(TokenType type, int lineNumber, Boolean boolVal) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.bool = boolVal;
    }


    // Getters and Setters
    public double getNumVal() {
        return this.num;
    }

    public void setNumVal(Double numVal) {
        this.num = numVal;
    }

    public String getStrVal() {
        return this.str;
    }

    public void setStrVal(String strVal) {
        this.str = strVal;
    }

    public boolean getBooleanVal() {
        return this.bool;
    }

    public void setBooleanVal(boolean booleanVal) {
        this.bool = booleanVal;
    }

    public Lexeme getLeft() {
        return this.left;
    }

    public void setLeft(Lexeme lex) {
        this.left = lex;
    }

    public Lexeme getRight() {
        return this.right;
    }

    public void setRight(Lexeme lex) {
        this.right = lex;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public TokenType getType() {
        return type;
    }

    // ToString

    @Override
    public String toString() {
        String str = "Type: " + this.type + " | Line Num: " + this.lineNumber + " | Value: ";
        if (this.num != null) {
            str += this.num;
        } else if (this.str != null) {
            str += this.str;
        } else if (this.bool != null) {
            str += this.bool;
        } else {
            str += "N/A";
        }
        return str;
    }


    public void printAsTree(int level) {
        System.out.print(this.type);
        if (this.left != null) {
            System.out.println();
            String string = "\t";
            System.out.print(string.repeat(level + 1) + "Left branch: ");
            this.left.printAsTree(level + 1);
        } else {
            System.out.println();
            String string = "\t";
            System.out.print(string.repeat(level + 1) + " HAS NO LEFT CHILD.");
        }
        if (this.right != null) {
            System.out.println();
            String string = "\t";
            System.out.print(string.repeat(level + 1) + "and right branch: ");
            this.right.printAsTree(level + 1);
        } else {
            System.out.println();
            String string = "\t";
            System.out.print(string.repeat(level + 1) + " HAS NO RIGHT CHILD.");
        }
    }

    public boolean eqs(Lexeme a) {
        if (this.num != null && this.num == a.getNumVal()) {
            return true;
        } else if (this.str != null && this.str.equals(a.getStrVal())) {
            return true;
        } else if (this.bool != null && this.bool == a.getBooleanVal()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean eqs(TokenType b) {
        if (this.type == b) {
            return true;
        } else {
            return false;
        }
    }

    public void setParent(Environment parent) {
        this.parent = parent;
    }

    public Environment getParent() {
        return parent;
    }
}