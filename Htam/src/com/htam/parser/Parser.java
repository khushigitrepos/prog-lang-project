package com.htam.parser;

import com.htam.Htam;
import com.htam.lexicalAnalysis.*;

import static com.htam.lexicalAnalysis.TokenType.*;

import java.util.ArrayList;

public class Parser {

    private static final boolean printDebugMessages = false;

    // Instance Variables
    private final ArrayList<Lexeme> lexemes;
    private Lexeme currentLexeme;
    private int nextLexemeIndex;

    // Constructor
    public Parser(ArrayList<Lexeme> lexemes) {
        this.lexemes = lexemes;
        this.nextLexemeIndex = 0;
        advance();
    }

    // Support Functions
    private boolean check(TokenType type) {
        return currentLexeme.getType() == type;
    }

    public Lexeme consume(TokenType expected) {
        Lexeme currentReal = currentLexeme;
        if (check(expected)) advance();
        else error("Expected " + expected + " but found " + currentLexeme + ".");
        return currentReal;
    }

    private void advance() {
        currentLexeme = lexemes.get(nextLexemeIndex);
        nextLexemeIndex++;
    }

    private TokenType isNext() {
        if (nextLexemeIndex >= lexemes.size()) return null;
        return lexemes.get(nextLexemeIndex).getType();
    }

    private boolean checkNext(TokenType type) {
        if (nextLexemeIndex >= lexemes.size()) return false;
        return lexemes.get(nextLexemeIndex).getType() == type;
    }


    // Consumption Functions

    public Lexeme Program() {
        log("program");
        Lexeme program = new Lexeme(PROGRAM);
        Lexeme statementList = statementList();
        program.setLeft(statementList);
        return program;
    }

    private Lexeme statementList() {
        log("statement list");
        Lexeme statementList = new Lexeme(STATELIST);
        Lexeme statement = statement();
        statementList.setLeft(statement);
        if (statementListPending()) {
            statementList.setRight(statementList());
        }
        return statementList;
    }

    private Lexeme statement() {
        log("statement");
        Lexeme statement = new Lexeme(STATE);
        if (variableInitializationPending()) {
            Lexeme varinit = variableInitialization();
            statement.setLeft(varinit);
        } else if (arrayInitializationPending()) {
            Lexeme arrinit = arrayInitialization();
            statement.setLeft(arrinit);
        } else if (matrixInitializationPending()) {
            Lexeme matinit = matrixInitialization();
            statement.setLeft(matinit);
        } else if (operationsPending()) {
            Lexeme op = operations();
            statement.setLeft(op);
            if (logicalOperatorsPending()) {
                statement.setRight(logicalOperations());
            }
        } else if (setEqualsPending()) {
            System.out.print("HERE");
            Lexeme seteq = setEquals();
            statement.setLeft(seteq);
        } else if (primaryPending()) {
            Lexeme prim = primary();
            statement.setLeft(prim);
        } else if (conditionalsPending()) {
            Lexeme cond = conditionals();
            statement.setLeft(cond);
        } else if (functionDefinitionPending()) {
            Lexeme fxndef = functionDefinition();
            statement.setLeft(fxndef);
        } else if (functionTasksPending()) {
            Lexeme fxntasks = functionTasks();
            statement.setLeft(fxntasks);
        } else if (builtInFunctionsPending()) {
            Lexeme builtin = builtInFunctions();
            statement.setLeft(builtin);
        } else {
            error("Expected statement.");
        }

        return statement;
    }

    private Lexeme setEquals() {
        Lexeme seteq = new Lexeme(SETEQ);
        Lexeme id = consume(IDENTIFIER);
        seteq.setLeft(id);
        consume(EQUALS);
        Lexeme primary = primary();
        id.setRight(primary);
        return seteq;
    }

    private Lexeme variableInitialization() {
        log("var. init.");
        Lexeme varinit = new Lexeme(VARINIT);
        consume(NU_VAR_INITIAL);
        Lexeme glue = new Lexeme(GLUE);
        varinit.setLeft(glue);
        Lexeme typeinit = typeInitializer();
        glue.setLeft(typeinit);
        Lexeme id = consume(IDENTIFIER);
        glue.setRight(id);
        consume(EQUALS);

        if (primaryPending()) {
            Lexeme primary = primary();
            varinit.setRight(primary);
        } else if (operationsPending()) {
            Lexeme operations = operations();
            varinit.setRight(operations);
        } else error("Expected variable initialization.");

        return varinit;
    }

    private Lexeme arrayInitialization() {
        log("arr. init.");
        Lexeme arrinit = new Lexeme(ARRINIT);
        consume(UPSILON_ARR_INITIAL);
        Lexeme id = consume(IDENTIFIER);
        arrinit.setLeft(id);
        consume(EQUALS);
        consume(OPEN_CURL);
        arrinit.setRight(primaryList());
        consume(CLOSE_CURL);

        return arrinit;
    }

    private Lexeme primaryList() {
        Lexeme primaryList = new Lexeme(PRIMLIST);
        Lexeme prim = primary();
        primaryList.setLeft(prim);
        if (primaryPending()) primaryList.setRight(primaryList());

        return primaryList;
    }

    private Lexeme matrixInitialization() {
        log("mat. init.");
        Lexeme matinit = new Lexeme(MATLIST);
        assert matinit != null;
        consume(UPSILON_SQUARED_MATRIX_INITIAL);
        Lexeme id = consume(IDENTIFIER);
        matinit.setLeft(id);
        consume(EQUALS);
        consume(OPEN_CURL);
        Lexeme glue = new Lexeme(GLUE);
        matinit.setRight(glue);
        glue.setLeft(primaryList());
        consume(CLOSE_CURL);
        consume(OPEN_CURL);
        glue.setRight(primaryList());
        consume(CLOSE_CURL);

        return matinit;
    }

    private Lexeme typeInitializer() {
        log("type init.");
        Lexeme typeinit = new Lexeme(TYPEINIT);
        if (numInitializerPending()) typeinit = (consume(POUND_NUM));
        else if (stringInitializerPending()) typeinit = (consume(EUROPEAN_S_STRING));
        else if (booleanInitializerPending()) typeinit = (consume(GERMAN_B_BOOLEAN));
        else {
            error("Expected type initializer.");
        }

        return typeinit;
    }

    private Lexeme type() {
        log("type");
        Lexeme type = new Lexeme(TYPE);
        if (numberPending()) type = (consume(NUMBER));
        else if (stringPending()) type = (consume(STRING));
        else if (boolPending()) {
            if (check(TAU_TRUE)) type = (consume(TAU_TRUE));
            else if (check(CAPITAL_PI_FALSE)) type = (consume(CAPITAL_PI_FALSE));
        } else if (parentheticalOperationsPending()) {
            Lexeme parenops = parentheticalOperations();
            return parenops;
        } else {
            error("Expected type value.");
        }

        return type;
    }

    private Lexeme primary() {
        log("primary");
        Lexeme prim = new Lexeme(PRIM);

        if (check(IDENTIFIER)) return (consume(IDENTIFIER));
        else if (check(NUMBER)) return consume(NUMBER);
        else if (check(STRING)) return consume(STRING);
        else if (typePending()) return (type());
        else if (mathOperationsPending()) {
            Lexeme mathops = mathOperations();
            return (mathops);
        } else if (comparisonOperationsPending()) {
            Lexeme logicops = logicalOperations();
            return (logicops);
        } else if (parentheticalOperationsPending()) {
            Lexeme parenops = parentheticalOperations();
            return (parenops);
        } else if (primaryCommaPending()) {
            primary();
            consume(COMMA);
        } else error("Expected primary.");

        return prim;
    }

    private Lexeme operations() {
        log("operations");
        Lexeme ops = new Lexeme(OPS);

        if (mathOperationsPending()) {
            Lexeme mathop = mathOperations();
            return (mathop);
        } else if (comparisonOperationsPending()) {
            Lexeme compop = comparisonOperations();
            return (compop);
        } else if (logicalOperationsPending()) {
            Lexeme logop = logicalOperations();
            return (logop);
        } else error("Expected an operation.");

        return ops;
    }

    private Lexeme parentheticalOperations() {
        log("paren. operations");
        consume(OPEN_PAREN);
        Lexeme parenop = new Lexeme(PARENOP);
        parenop.setLeft(operations());
        consume(CLOSE_PAREN);

        return parenop;
    }

    private Lexeme mathOperations() {
        log("math operations");

        Lexeme mathop = mathOperators();
        Lexeme prim1 = primary();
        mathop.setLeft(prim1);
        Lexeme glue = new Lexeme(GLUE);
        mathop.setRight(glue);
        Lexeme and = consume(AND_SYMBOL);
        glue.setLeft(and);
        Lexeme prim2 = primary();
        glue.setRight(prim2);

        return mathop;
    }

    private Lexeme mathOperators() {
        log("math operators");
        Lexeme matops = new Lexeme(MATHOPS);
        if (additionPending()) {
            matops = (consume(SIGMA_ADD));
        } else if (subtractionPending()) matops = (consume(SIGMA_MINUS));
        else if (multiplicationPending()) matops = (consume(MU_MULTI));
        else if (divisionPending()) matops = (consume(DELTA_DIV));
        else if (modulusPending()) matops = (consume(MOD));
        else if (incrementAdditionPending()) matops = (consume(SIGMA_INCREMENT_ADD));
        else if (incrementSubtractionPending()) matops = (consume(SIGMA_INCREMENT_MINUS));
        else error("Expected math operator.");
        return matops;
    }

    private Lexeme comparisonOperations() {
        log("comp. operations");
        Lexeme prim1 = primary();
        Lexeme compop = comparisonOperators();
        Lexeme prim2 = primary();
        compop.setLeft(prim1);
        compop.setRight(prim2);

        return compop;
    }

    private Lexeme comparisonOperators() {
        log("comp. operators");
        Lexeme compops = new Lexeme(COMPOPS);
        if (eqPending()) compops = consume(EQUALS);
        else if (equalsQuestionedPending()) compops = (consume(EQUALS_EQUALS));
        else if (notEqualPending()) compops = (consume(ELIPSIS_NOTEQUAL));
        else if (greaterThanPending()) compops = (consume(GREATER_THAN));
        else if (lessThanPending()) compops = (consume(LESS_THAN));
        else if (greaterThanEqualToPending()) compops = (consume(GREATER_EQUAL));
        else if (lessThanEqualToPending()) compops = (consume(LESS_EQUAL));
        else error("Expected comparison operator.");
        return compops;
    }

    private Lexeme logicalOperations() {
        log("log. operations");
        Lexeme logop = logicalOperators();
        Lexeme comp2 = comparisonOperations();
        logop.setLeft(comp2);

        return logop;
    }

    private Lexeme logicalOperators() {
        log("log. operators");
        Lexeme logops = new Lexeme(LOGOPS);
        if (andPending()) logops = consume(ALPHA_AND);
        else if (notPending()) logops = consume(BETA_NOT);
        else if (orPending()) logops = consume(OMEGA_OR);
        else error("Expected logical operator");
        return logops;
    }

    private Lexeme conditionals() {
        log("cond.");

        Lexeme cond = new Lexeme(COND);
        if (ifPending()) {
            Lexeme iffy = iffy();
            return (iffy);
        } else if (whilePending()) {
            Lexeme whilee = whilee();
            return (whilee);
        } else if (forPending()) {
            Lexeme fore = fore();
            return (fore);
        } else {
            error("Conditional expected. " + currentLexeme);
        }
        return cond;
    }

    private Lexeme convenienceBranch() {
        consume(OPEN_PAREN);
        Lexeme convi = new Lexeme(CONVI);
        Lexeme compop = comparisonOperations();
        convi.setLeft(compop);
        if (logicalOperationsPending()) {
            Lexeme glue2 = new Lexeme(GLUE);
            convi.setRight(glue2);
            Lexeme logop = logicalOperations();
            glue2.setLeft(logop);
        }
        if (primaryPending()) {
            Lexeme glue2 = new Lexeme(GLUE);
            convi.setRight(glue2);
            Lexeme primlist = primaryList();
            glue2.setRight(primlist);
        }
        consume(CLOSE_PAREN);

        return convi;
    }

    private Lexeme block() {
        log("block");
        consume(DOUBLE_ARROW_OPEN);
        Lexeme blo = new Lexeme(BLOCK);
        blo.setLeft(statementList());
        consume(DOUBLE_ARROW_CLOSE);
        return blo;
    }

    private Lexeme iffy() {
        log("if");
        Lexeme iffy = new Lexeme(IFFY);
        consume(IOTA_IF);
        Lexeme convi1 = convenienceBranch();
        iffy.setLeft(convi1);
        Lexeme glue1 = new Lexeme(GLUE);
        iffy.setRight(glue1);
        Lexeme blo = block();
        glue1.setLeft(blo);

        if (ifelsePending()) {
            glue1.setRight(ifelse());
        }
        if (elsePending()) {
            glue1.setRight(elsee());
        }

        return iffy;
    }

    private Lexeme ifelse() {
        log("ifelse");
        Lexeme ifelse = new Lexeme(IFEL);
        Lexeme ioep = consume(IOTA_EPSILON);
        ifelse.setLeft(ioep);
        Lexeme convi = convenienceBranch();
        ioep.setLeft(convi);
        Lexeme glue2 = new Lexeme(GLUE);
        ioep.setRight(glue2);
        Lexeme blo = block();
        glue2.setLeft(blo);
        if (ifelsePending()) {
            glue2.setRight(elsee());
        } else if (elsePending()) {
            glue2.setRight(elsee());
        }
        return ifelse;
    }

    private Lexeme elsee() {
        log("else");
        Lexeme elsee = new Lexeme(ELSEY);
        Lexeme el = consume(EPSILON_ELSE);
        elsee.setLeft(el);
        Lexeme blo = block();
        elsee.setRight(blo);
        return elsee;
    }

    private Lexeme whilee() {
        log("while");

        consume(LOWERCASE_SIGMA_WHILE);
        Lexeme whi = new Lexeme(WHI);
        Lexeme convi = convenienceBranch();
        whi.setLeft(convi);
        Lexeme blo = block();
        whi.setRight(blo);

        return whi;
    }

    private Lexeme fore() {
        log("for");

        Lexeme f = new Lexeme(FORE);
        Lexeme glue1 = new Lexeme(GLUE);
        f.setLeft(glue1);
        Lexeme glue2 = new Lexeme(GLUE);
        f.setRight(glue2);
        consume(PHI_FOR);
        consume(OPEN_PAREN);
        Lexeme varinit = variableInitialization();
        glue1.setLeft(varinit);
        consume(COMMA);
        Lexeme mathop = mathOperations();
        glue1.setRight(mathop);
        consume(COMMA);
        Lexeme compop = comparisonOperations();
        glue2.setLeft(compop);
        consume(CLOSE_PAREN);
        consume(DOUBLE_ARROW_OPEN);
        while (statementPending()) glue2.setRight(statementList());
        consume(DOUBLE_ARROW_CLOSE);

        return f;
    }

    private Lexeme typeList() {
        Lexeme typelist = new Lexeme(TLIST);
        Lexeme glue = new Lexeme(GLUE);
        typelist.setLeft(glue);
        Lexeme typein = typeInitializer();
        glue.setLeft(typein);
        Lexeme id = consume(IDENTIFIER);
        glue.setRight(id);
        if (typeInitializerPending()) typelist.setRight(typeList());
        return typelist;
    }

    private Lexeme functionDefinition() {
        log("func. def.");

        consume(NU_VAR_INITIAL);
        consume(CHI_FXN);
        Lexeme fxndef = new Lexeme(FXNDEF);
        Lexeme glue = new Lexeme(GLUE);
        fxndef.setLeft(glue);
        Lexeme id = consume(IDENTIFIER);
        glue.setLeft(id);
        consume(OPEN_PAREN);
        if (typeInitializerPending()) {
            Lexeme typelist = typeList();
            glue.setRight(typelist);
        }
        consume(CLOSE_PAREN);
        consume(DOUBLE_ARROW_OPEN);
        while (statementPending()) {
            Lexeme statelist = statementList();
            fxndef.setRight(statelist);
        }
        consume(DOUBLE_ARROW_CLOSE);

        return fxndef;
    }

    private Lexeme functionTasks() {
        log("func. tasks");
        Lexeme fxntasks = new Lexeme(FXNTASKS);
        if (callPending()) {
            Lexeme call = call();
            fxntasks.setLeft(call);
        } else if (returnPending()) {
            Lexeme returne = returne();
            fxntasks.setRight(returne);
        } else error("Expected function task.");

        return fxntasks;
    }

    private Lexeme call() {
        log("call");

        consume(CHI_FXN);
        Lexeme call = new Lexeme(CALL);
        Lexeme id = consume(IDENTIFIER);
        call.setLeft(id);
        consume(OPEN_PAREN);
        if (primaryPending()) {
            Lexeme primlist = primaryList();
            call.setRight(primlist);
        }
        consume(CLOSE_PAREN);

        return call;
    }

    private Lexeme returne() {
        log("return");

        consume(RHO_RETURN);
        consume(OPEN_PAREN);
        Lexeme returne = new Lexeme(RETUR);
        if (statementPending()) {
            returne.setRight(statementList());
        }
        consume(CLOSE_PAREN);

        return returne;
    }


    private Lexeme builtInFunctions() {
        log("built-in func.");
        Lexeme bif = new Lexeme(BIF);
        if (arrayLengthPending()) bif.setLeft(arrayLength());
        else if (printPending()) bif.setLeft(print());
        else if (commentPending()) bif.setLeft(comment());
        else if (multiCommentPending()) bif.setLeft(multiComment());
        else if (breakPending()) bif.setLeft(breake());
        else error("Expected built-in function call.");
        return bif;
    }

    private Lexeme arrayLength() {
        log("arr.len");
        Lexeme arrlen = new Lexeme(ARRLEN);
        Lexeme id = consume(IDENTIFIER);
        arrlen.setLeft(id);
        Lexeme curlyl = consume(CURLY_LENGTH);
        arrlen.setRight(curlyl);

        return arrlen;
    }

    private Lexeme print() {
        log("print");

        Lexeme print = new Lexeme(PRINT);
        Lexeme lowerpisymbol = consume(LOWERCASE_PI_PRINT);
        print.setLeft(lowerpisymbol);
        consume(OPEN_PAREN);
        while (statementPending()) print.setRight(statementList());
        consume(CLOSE_PAREN);

        return print;
    }

    private Lexeme comment() {
        log("comment");
        consume(VERTICAL_BAR);

        consume(VERTICAL_BAR);

        return null;
    }

    private Lexeme multiComment() {
        log("multi comment");
        consume(VERTICAL_BAR);
        consume(VERTICAL_BAR);
        consume(VERTICAL_BAR);

        consume(VERTICAL_BAR);
        consume(VERTICAL_BAR);
        consume(VERTICAL_BAR);

        return null;
    }

    private Lexeme breake() {
        log("break");
        return consume(KAPPA_BREAK);
    }

    // Pending Functions

    private boolean programPending() {
        return statementListPending();
    }

    private boolean statementListPending() {
        return statementPending();
    }

    private boolean statementPending() {
        return variableInitializationPending() ||
                arrayInitializationPending() ||
                matrixInitializationPending() ||
                operationsPending() ||
                conditionalsPending() ||
                primaryPending() ||
                functionDefinitionPending() ||
                functionTasksPending() ||
                builtInFunctionsPending();
    }

    public boolean blockPending() {
        return check(DOUBLE_ARROW_OPEN);
    }

    private boolean variableInitializationPending() {
        return check(NU_VAR_INITIAL) && !(checkNext(UPSILON_ARR_INITIAL) || checkNext(UPSILON_SQUARED_MATRIX_INITIAL) || checkNext(CHI_FXN));
    }

    private boolean arrayInitializationPending() {
        return check(NU_VAR_INITIAL) && checkNext(UPSILON_ARR_INITIAL);
    }

    private boolean matrixInitializationPending() {
        return check(NU_VAR_INITIAL) && checkNext(UPSILON_SQUARED_MATRIX_INITIAL);
    }

    private boolean setEqualsPending() {
        return (check(IDENTIFIER) && checkNext(EQUALS));
    }

    private boolean typeInitializerPending() {
        return numInitializerPending() ||
                stringInitializerPending() ||
                booleanInitializerPending();
    }

    private boolean numInitializerPending() {
        return check(POUND_NUM);
    }

    private boolean stringInitializerPending() {
        return check(EUROPEAN_S_STRING);
    }

    private boolean booleanInitializerPending() {
        return check(GERMAN_B_BOOLEAN);
    }

    private boolean typePending() {
        return numberPending() ||
                stringPending() ||
                boolPending();
    }

    private boolean numberPending() {
        return check(NUMBER);
    }

    private boolean stringPending() {
        return check(STRING);
    }

    private boolean boolPending() {
        return check(TAU_TRUE) || check(CAPITAL_PI_FALSE);
    }

    private boolean primaryPending() {
        return check(IDENTIFIER) ||
                typePending() ||
                parentheticalOperationsPending();
    }

    private boolean primaryCommaPending() {
        return primaryPending() && checkNext(COMMA);
    }

    private boolean operationsPending() {
        return mathOperationsPending() ||
                comparisonOperationsPending() ||
                logicalOperationsPending();
    }

    private boolean parentheticalOperationsPending() {
        return check(OPEN_PAREN);
    }

    private boolean mathOperationsPending() {
        return mathOperatorsPending();
    }

    private boolean mathOperatorsPending() {
        return additionPending() ||
                subtractionPending() ||
                multiplicationPending() ||
                divisionPending() ||
                modulusPending() ||
                incrementAdditionPending() ||
                incrementSubtractionPending();
    }

    private boolean additionPending() {
        return check(SIGMA_ADD);
    }

    private boolean subtractionPending() {
        return check(SIGMA_MINUS);
    }

    private boolean multiplicationPending() {
        return check(MU_MULTI);
    }

    private boolean divisionPending() {
        return check(DELTA_DIV);
    }

    private boolean modulusPending() {
        return check(MOD);
    }

    private boolean incrementAdditionPending() {
        return check(SIGMA_INCREMENT_ADD);
    }

    private boolean incrementSubtractionPending() {
        return check(SIGMA_INCREMENT_MINUS);
    }

    private boolean comparisonOperationsPending() {
        return typePending() && (checkNext(EQUALS) ||
                checkNext(EQUALS_EQUALS) ||
                checkNext(ELIPSIS_NOTEQUAL) ||
                checkNext(GREATER_THAN) ||
                checkNext(LESS_THAN) ||
                checkNext(GREATER_EQUAL) ||
                checkNext(LESS_EQUAL));
    }

    private boolean comparisonOperatorsPending() {
        return eqPending() ||
                equalsQuestionedPending() ||
                notEqualPending() ||
                greaterThanPending() ||
                lessThanPending() ||
                greaterThanEqualToPending() ||
                lessThanEqualToPending();
    }

    private boolean eqPending() {
        return check(EQUALS);
    }

    private boolean equalsQuestionedPending() {
        return check(EQUALS_EQUALS);
    }

    private boolean notEqualPending() {
        return check(ELIPSIS_NOTEQUAL);
    }

    private boolean greaterThanPending() {
        return check(GREATER_THAN);
    }

    private boolean lessThanPending() {
        return check(LESS_THAN);
    }

    private boolean greaterThanEqualToPending() {
        return check(GREATER_EQUAL);
    }

    private boolean lessThanEqualToPending() {
        return check(LESS_EQUAL);
    }

    private boolean logicalOperationsPending() {
        return typePending() && (checkNext(ALPHA_AND) || checkNext(BETA_NOT) || checkNext(OMEGA_OR));
    }

    private boolean logicalOperatorsPending() {
        return andPending() || notPending() || orPending();
    }

    private boolean andPending() {
        return check(ALPHA_AND);
    }

    private boolean notPending() {
        return check(BETA_NOT);
    }

    private boolean orPending() {
        return check(OMEGA_OR);
    }

    private boolean conditionalsPending() {
        return ifPending() ||
                whilePending() ||
                forPending();
    }

    private boolean ifPending() {
        return check(IOTA_IF);
    }

    private boolean ifelsePending() {
        return check(IOTA_EPSILON);
    }

    private boolean elsePending() {
        return check(EPSILON_ELSE);
    }

    private boolean whilePending() {
        return check(LOWERCASE_SIGMA_WHILE);
    }

    private boolean forPending() {
        return check(PHI_FOR);
    }

    private boolean functionDefinitionPending() {
        return check(NU_VAR_INITIAL) && checkNext(CHI_FXN);
    }

    private boolean functionTasksPending() {
        return callPending() || returnPending();
    }

    private boolean callPending() {
        return check(CHI_FXN);
    }

    private boolean returnPending() {
        return check(RHO_RETURN);
    }

    private boolean builtInFunctionsPending() {
        return arrayLengthPending() || printPending() || commentPending() || multiCommentPending() || breakPending();
    }

    private boolean arrayLengthPending() {
        return check(IDENTIFIER) && checkNext(CURLY_LENGTH);
    }

    private boolean printPending() {
        return check(LOWERCASE_PI_PRINT);
    }

    private boolean commentPending() {
        return check(VERTICAL_BAR);
    }

    private boolean multiCommentPending() {
        return check(MULTI_BAR);
    }

    private boolean breakPending() {
        return check(KAPPA_BREAK);
    }

    private boolean openCurlyPending() {
        return check(OPEN_CURL);
    }

    // Debugging
    private static void log(String message) {
        if (printDebugMessages) System.out.println(message);
    }

    // Error Reporting
    private void error(String message) {
        Htam.syntaxError(message, currentLexeme);

    }
}
