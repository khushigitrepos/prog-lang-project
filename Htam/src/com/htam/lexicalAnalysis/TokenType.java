package com.htam.lexicalAnalysis;

public enum TokenType {

    // Single-Character Tokens

    NU_VAR_INITIAL,
    POUND_NUM, UMLAUT_DOUBLE, EUROPEAN_S_STRING, THETA, LOWERCASE_SIGMA_FXN,
    GERMAN_B_BOOLEAN, TAU_TRUE, CAPITAL_PI_FALSE, LOWERCASE_PI_PRINT,
    AND_SYMBOL, OPEN_PAREN, CLOSE_PAREN, OPEN_SQUARE, CLOSE_SQUARE, COLON,
    OPEN_CURL, CLOSE_CURL, MU_MULTI, DELTA_DIV, MOD, ALPHA_AND, OMEGA_OR, BETA_NOT,
    PSI_CLASS, CHI_FXN, RHO_RETURN, LOWERCASE_SIGMA_WHILE, PHI_FOR, KAPPA_BREAK,
    DOUBLE_ARROW_OPEN, DOUBLE_ARROW_CLOSE, ELIPSIS_NOTEQUAL, EOF, COMMA, PERIOD,
    GLUE, VARINIT, ARRINIT, PRIMLIST, MATLIST, PARENOP, IFEL, WHI, FORE, FXNDEF,
    FXNTASKS, CALL, RETUR, ARRLEN, PRINT, PROGRAM, STATELIST, STATE, TYPEINIT, TYPE, PRIM,
    OPS, MATHOPS, COMPOPS, LOGOPS, BIF, GLOBAL, ENVIRONMENT, CONVI, IFFY, ELSEY, BLOCK, PPLIST, COND, SETEQ, TLIST,

    // Single or Multi-Character Tokens

    UPSILON_ARR_INITIAL, UPSILON_SQUARED_MATRIX_INITIAL, SIGMA,
    SIGMA_ADD, SIGMA_INCREMENT_ADD,
    SIGMA_MINUS, SIGMA_INCREMENT_MINUS,
    EQUALS, EQUALS_EQUALS, GREATER_THAN,
    GREATER_EQUAL, LESS_THAN, LESS_EQUAL,
    IOTA_IF, EPSILON_ELSE, IOTA_EPSILON,
    VERTICAL_BAR, MULTI_BAR, CURLY_LENGTH,

    // Literals

    IDENTIFIER, NUMBER, STRING

}