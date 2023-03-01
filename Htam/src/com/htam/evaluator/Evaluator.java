package com.htam.evaluator;

import com.htam.Htam;
import com.htam.environment.Environment;
import com.htam.lexicalAnalysis.Lexeme;
import com.htam.lexicalAnalysis.TokenType;

import static com.htam.lexicalAnalysis.TokenType.*;

import java.util.ArrayList;

public class Evaluator {

    public Evaluator() {
        Environment global = new Environment(null);
    }

    public Lexeme eval(Lexeme tree, Environment env) {

        if (tree.getType() == PROGRAM) tree = tree.getLeft();

        return switch (tree.getType()) {

            case STATELIST -> evalStatementList(tree, env);
            case STATE -> evalStatement(tree, env);

            case SIGMA_ADD -> evalAdd(tree, env);
            case SIGMA_MINUS -> evalSubtract(tree, env);
            case MU_MULTI -> evalMultiply(tree, env);
            case DELTA_DIV -> evalDivide(tree, env);
            case MOD -> evalMod(tree, env);

            case GREATER_THAN -> evalGreaterThan(tree, env);
            case GREATER_EQUAL -> evalGreaterEqual(tree, env);
            case LESS_THAN -> evalLessThan(tree, env);
            case LESS_EQUAL -> evalLessEqual(tree, env);
            case EQUALS_EQUALS -> evalEqualsEquals(tree, env);
            case ELIPSIS_NOTEQUAL -> evalNotEqual(tree, env);
            case SETEQ -> evalSetEq(tree, env);

            case VARINIT -> evalVarInit(tree, env);
            case FXNTASKS -> eval(tree.getLeft(), env);


            case BLOCK -> evalBlock(tree, env);
            case IFFY -> evalIfStatement(tree, env);
            case IFEL -> evalIfElseStatement(tree, env);
            case ELSEY -> evalElseStatement(tree, env);
            case WHI -> evalWhileLoop(tree, env);
            case FORE -> evalForLoop(tree, env);

            case FXNDEF -> evalFunctionDefinition(tree, env);
            case CALL -> evalFunctionCall(tree, env);


            default -> throw new IllegalStateException("Unexpected value: " + tree.getType());
        };

    }

    private Lexeme evalStatementList(Lexeme tree, Environment environment) {
        // how would you get the tree to remain constant? how to access the statelist branch again

        eval(tree.getLeft(), environment);
        if (tree.getRight() != null) {
            eval(tree.getRight(), environment);
        }
        return new Lexeme(STATELIST);

    }

    private Lexeme evalStatement(Lexeme tree, Environment environment) {

        if (tree.getRight() != null) {
            Lexeme result = eval(tree.getLeft(), environment);
            return switch (tree.getRight().getType()) {
                case ALPHA_AND -> evalAnd(tree.getRight(), environment, result);
                case BETA_NOT -> evalNot(tree.getRight(), environment, result);
                case OMEGA_OR -> evalOr(tree.getRight(), environment, result);
                default -> throw new IllegalStateException("Unexpected value: " + tree.getType());
            };
        }

        eval(tree.getLeft(), environment);

        return new Lexeme(STATE);

    }

    private Lexeme evalAdd(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == NUMBER) {
            if (rightType == NUMBER) {
                double value = (left.getNumVal() + right.getNumVal());
                System.out.println(value);
                return new Lexeme(NUMBER, lineNumber, value);
            } else if (rightType == STRING) {
                String value = left.getNumVal() + right.getStrVal();
                System.out.println(value);
                return new Lexeme(STRING, lineNumber, value);
            }
        } else if (leftType == STRING) {
            if (rightType == NUMBER) {
                String value = (left.getStrVal() + right.getNumVal());
                System.out.println(value);
                return new Lexeme(STRING, lineNumber, value);
            } else if (rightType == STRING) {
                String value = left.getStrVal() + right.getStrVal();
                System.out.println(value);
                return new Lexeme(STRING, lineNumber, value);
            } else if (rightType == TAU_TRUE || rightType == CAPITAL_PI_FALSE) {
                String value = left.getStrVal() + right.getBooleanVal();
                System.out.println(value);
                return new Lexeme(STRING, lineNumber, value);
            }
        } else if (leftType == TokenType.TAU_TRUE) {
            if (rightType == TokenType.STRING) {
                String value = left.getBooleanVal() + right.getStrVal();
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            } else if (rightType == TokenType.TAU_TRUE || rightType == TokenType.CAPITAL_PI_FALSE) {
                System.out.println("τ");
                return new Lexeme(TokenType.STRING, lineNumber, true);
            }
        } else if (leftType == TokenType.CAPITAL_PI_FALSE) {
            if (rightType == TokenType.STRING) {
                String value = left.getBooleanVal() + right.getStrVal();
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            } else if (rightType == TokenType.TAU_TRUE) {
                System.out.println("τ");
                return new Lexeme(TokenType.STRING, lineNumber, true);
            } else if (rightType == TokenType.CAPITAL_PI_FALSE) {
                System.out.println("Π");
                return new Lexeme(TokenType.STRING, lineNumber, false);
            }
        } else {
            Htam.syntaxError("Cannot add " + leftType + " and " + rightType, lineNumber);
        }
        return null;
    }

    private Lexeme evalSubtract(Lexeme tree, Environment environment) {

        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                double value = (left.getNumVal() - right.getNumVal());
                System.out.println(value);
                return new Lexeme(TokenType.NUMBER, lineNumber, value);
            }
        } else if (leftType == TokenType.STRING) {
            if (rightType == TokenType.NUMBER) {
                String str = left.getStrVal();
                int num = (int) right.getNumVal();
                String value = null;
                ArrayList vals = new ArrayList<Character>();
                for (int i = 0; i < str.length(); i++) {
                    vals.set(i, str.charAt(i));
                }
                for (int i = 0; i < num; i++) {
                    value += vals.get(i);
                }
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            } else if (rightType == TokenType.STRING) {
                String str = left.getStrVal();
                String rem = right.getStrVal();
                str.replace(rem, "");
                String value = str;
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            }
        } else if (leftType == null) {
            if (rightType == TokenType.NUMBER) {
                double value = -right.getNumVal();
                return new Lexeme(TokenType.NUMBER, lineNumber, value);
            } else if (rightType == TokenType.STRING) {
                String str = left.getStrVal();
                String value = null;
                ArrayList vals = new ArrayList<Character>();
                for (int i = str.length(); i > 0; i--) {
                    vals.set(i, str.charAt(i));
                }
                for (int i = 0; i < str.length(); i++) {
                    value += vals.get(i);
                }
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            } else if (rightType == TokenType.TAU_TRUE) {
                System.out.println("Π");
                return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
            } else if (rightType == TokenType.CAPITAL_PI_FALSE) {
                System.out.println("τ");
                return new Lexeme(TokenType.TAU_TRUE, lineNumber);
            } else {
                Htam.syntaxError("Cannot negate " + rightType, lineNumber);
            }
        } else {
            Htam.syntaxError("Cannot subtract " + leftType + " and " + rightType, lineNumber);
        }
        return null;
    }

    private Lexeme evalMultiply(Lexeme tree, Environment environment) {

        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                double value = (left.getNumVal() * right.getNumVal());
                System.out.println(value);
                return new Lexeme(TokenType.NUMBER, lineNumber, value);
            }
        } else if (leftType == TokenType.STRING) {
            if (rightType == TokenType.NUMBER) {
                String str = left.getStrVal();
                int num = (int) right.getNumVal();
                String value = str.repeat(num);
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            }
        }
        return null;
    }

    private Lexeme evalDivide(Lexeme tree, Environment environment) {

        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                double value = (left.getNumVal() / right.getNumVal());
                System.out.println(value);
                return new Lexeme(TokenType.NUMBER, lineNumber, value);
            }
        }
        return null;
    }

    private Lexeme evalMod(Lexeme tree, Environment environment) {

        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                double value = (left.getNumVal() % right.getNumVal());
                System.out.println(value);
                return new Lexeme(TokenType.NUMBER, lineNumber, value);
            }
        } else if (leftType == TokenType.STRING) {
            if (rightType == TokenType.STRING) {
                String str = left.getStrVal();
                String rem = right.getStrVal();
                str.replace(rem, "");
                String value = str;
                System.out.println(value);
                return new Lexeme(TokenType.STRING, lineNumber, value);
            }
        }
        return null;
    }

    private Lexeme evalGreaterThan(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                if (left.getNumVal() > right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() <= right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);
                }
            }
        }
        if (leftType == TokenType.STRING) {
            if (rightType == TokenType.STRING) {
                if (left.getStrVal().length() > right.getStrVal().length()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal().length() <= right.getStrVal().length()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare " + leftType + " and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalGreaterEqual(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                if (left.getNumVal() >= right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() < right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);

                }
            }
        }
        if (leftType == TokenType.STRING) {
            if (rightType == TokenType.STRING) {
                if (left.getStrVal().length() >= right.getStrVal().length()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal().length() < right.getStrVal().length()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare " + leftType + " and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalLessThan(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                if (left.getNumVal() < right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() >= right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);
                }
            }
        }
        if (leftType == TokenType.STRING) {
            if (rightType == TokenType.STRING) {
                if (left.getStrVal().length() < right.getStrVal().length()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal().length() >= right.getStrVal().length()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare " + leftType + " and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalLessEqual(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                if (left.getNumVal() <= right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() > right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);
                }
            }
        }
        if (leftType == TokenType.STRING) {
            if (rightType == TokenType.STRING) {
                if (left.getStrVal().length() <= right.getStrVal().length()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal().length() > right.getStrVal().length()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare " + leftType + " and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalEqualsEquals(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == TokenType.NUMBER) {
            if (rightType == TokenType.NUMBER) {
                if (left.getNumVal() == right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() != right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);
                }
            } else if (rightType == TokenType.STRING) {
                if (left.getStrVal().length() == right.getStrVal().length()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal().length() != right.getStrVal().length()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else if (rightType == TokenType.TAU_TRUE || rightType == TokenType.CAPITAL_PI_FALSE) {
                if (left.getBooleanVal() == right.getBooleanVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getBooleanVal() != right.getBooleanVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                } else {
                    Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare " + leftType + " and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalNotEqual(Lexeme tree, Environment environment) {
        Lexeme left = tree.getLeft();
        Lexeme right = tree.getRight();
        int lineNumber = tree.getLineNumber();


        if (left.getType() == IDENTIFIER) {
            left = environment.lookup(left);
        }
        if (right.getType() == IDENTIFIER) {
            right = environment.lookup(right);
        }

        TokenType leftType = left.getType();
        TokenType rightType = right.getType();

        if (leftType == NUMBER) {
            if (rightType == NUMBER) {
                if (left.getNumVal() != right.getNumVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getNumVal() == right.getNumVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare NUMBER and " + rightType, lineNumber);
            }
        } else if (leftType == STRING) {
            if (rightType == STRING) {
                if (left.getStrVal() != right.getStrVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getStrVal() == right.getStrVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare STRING and " + rightType, lineNumber);
            }
        } else if (leftType == TAU_TRUE || leftType == CAPITAL_PI_FALSE) {
            if (leftType == TAU_TRUE || leftType == CAPITAL_PI_FALSE) {
                if (left.getBooleanVal() != right.getBooleanVal()) {
                    System.out.println("τ");
                    return new Lexeme(TokenType.TAU_TRUE, lineNumber);
                } else if (left.getBooleanVal() == right.getBooleanVal()) {
                    System.out.println("Π");
                    return new Lexeme(TokenType.CAPITAL_PI_FALSE, lineNumber);
                }
            } else {
                Htam.syntaxError("Cannot compare BOOLEAN and " + rightType, lineNumber);
            }
        }
        return null;
    }

    private Lexeme evalAnd(Lexeme tree, Environment environment, Lexeme result) {
        Lexeme result2 = eval(tree.getLeft(), environment);
        System.out.println("THIS IS RESULT 1: " + result.getType() + " AND THIS IS RESULT 2: " + result2.getType() + ", MEANING THAT THIS IS THE OVERALL RESULT: ");
        int lineNumber = tree.getLineNumber();

        if (result.getType() == TAU_TRUE && result2.getType() == TAU_TRUE) {
            System.out.println("τ");
            return new Lexeme(TokenType.TAU_TRUE, lineNumber);
        } else {
            System.out.println("Π");
            return new Lexeme(CAPITAL_PI_FALSE, lineNumber);
        }
    }

    private Lexeme evalNot(Lexeme tree, Environment environment, Lexeme result) {
        Lexeme result2 = eval(tree.getLeft(), environment);
        System.out.println("THIS IS RESULT 1: " + result.getType() + " AND THIS IS RESULT 2: " + result2.getType() + ", MEANING THAT THIS IS THE OVERALL RESULT: ");
        int lineNumber = tree.getLineNumber();

        if ((result.getType() == TAU_TRUE && result2.getType() == CAPITAL_PI_FALSE) || result.getType() == CAPITAL_PI_FALSE && result2.getType() == TAU_TRUE) {
            System.out.println("τ");
            return new Lexeme(TokenType.TAU_TRUE, lineNumber);
        } else {
            System.out.println("Π");
            return new Lexeme(CAPITAL_PI_FALSE, lineNumber);
        }
    }

    private Lexeme evalOr(Lexeme tree, Environment environment, Lexeme result) {
        Lexeme result2 = eval(tree.getLeft(), environment);
        System.out.println("THIS IS RESULT 1: " + result.getType() + " AND THIS IS RESULT 2: " + result2.getType() + ", MEANING THAT THIS IS THE OVERALL RESULT: ");
        int lineNumber = tree.getLineNumber();

        if (result.getType() == TAU_TRUE || result2.getType() == TAU_TRUE) {
            System.out.println("τ");
            return new Lexeme(TokenType.TAU_TRUE, lineNumber);
        } else {
            System.out.println("Π");
            return new Lexeme(CAPITAL_PI_FALSE, lineNumber);
        }
    }

    private Lexeme evalVarInit(Lexeme tree, Environment environment) {
        environment.add(tree.getLeft().getRight(), tree.getRight());
        System.out.println(tree.getLeft().getRight() + " " + tree.getRight());
        return tree.getLeft().getRight();
    }

    private Lexeme evalSetEq(Lexeme tree, Environment environment) {
        environment.update(tree.getLeft(), tree.getLeft().getRight());
        System.out.println(tree.getLeft() + " is now equal to " + tree.getLeft().getRight());
        return tree.getLeft().getRight();
    }

    private Lexeme evalBlock(Lexeme tree, Environment environment) {
        Environment block = new Environment(environment);
        eval(tree.getLeft(), block);
        return null;
    }

    private Lexeme evalIfStatement(Lexeme tree, Environment environment) {
        if (eval(tree.getLeft().getLeft(), environment).getType() == TAU_TRUE) {
            eval(tree.getRight().getLeft(), environment);
        } else if (eval(tree.getLeft().getLeft(), environment).getType() == CAPITAL_PI_FALSE) {
            eval(tree.getRight().getRight(), environment);
        }
        return null;
    }

    private Lexeme evalIfElseStatement(Lexeme tree, Environment environment) {
        if (eval(tree.getLeft().getLeft().getLeft(), environment).getType() == TAU_TRUE) {
            eval(tree.getLeft().getRight().getLeft(), environment);
        } else if (eval(tree.getLeft().getLeft().getLeft(), environment).getType() == CAPITAL_PI_FALSE) {
            eval(tree.getLeft().getRight().getRight(), environment);
        }
        return null;
    }

    private Lexeme evalElseStatement(Lexeme tree, Environment environment) {
        eval(tree.getRight(), environment);
        return null;
    }

    private Lexeme evalWhileLoop(Lexeme tree, Environment environment) {
        Lexeme thistree = tree;
        Lexeme curtree = tree.getRight().getLeft();
        TokenType condition = eval(tree.getLeft().getLeft(), environment).getType();
        while (condition == TAU_TRUE) {
            eval(curtree, environment);
        }
        return null;
    }

    private Lexeme evalForLoop(Lexeme tree, Environment environment) {
        Environment fore = new Environment(environment);
        Double num = eval(tree.getLeft().getLeft(), fore).getNumVal();
        while (eval(tree.getRight().getLeft(), environment).getType() != TAU_TRUE) {
            eval(tree.getRight().getRight(), environment);
            eval(tree.getLeft().getRight(), fore);
        }

        return null;
    }

    private Lexeme evalFunctionDefinition(Lexeme tree, Environment environment) {
        Lexeme fxnname = tree.getLeft().getLeft();
        tree.setParent(environment);
        environment.add(new Lexeme(FXNDEF));
        environment.add(fxnname);
        environment.add(tree);
        return tree;
    }

    private Lexeme evalFunctionCall(Lexeme tree, Environment environment) {

        Lexeme fxnname = tree.getLeft();
        Lexeme closure = environment.lookup(fxnname);

        System.out.println(fxnname);

        if (closure.getType() != IDENTIFIER) {
            Htam.syntaxError("Attempted to call " + closure.getType() + " as a function.", fxnname);
        }

        Environment defEnvironment = closure.getParent();
        Environment callEnvironment = new Environment(defEnvironment);

        Lexeme parameterList = closure.getRight();
        Lexeme argumentList = tree.getRight();
        Lexeme evaluatedArgumentList = eval(argumentList, environment);

        callEnvironment.extend(parameterList, evaluatedArgumentList);

        // TODO: callEnvironment.extend(parameterList, evaluatedArgumentList); -> extend fxn repeatedly adds
        // TODO: a list of IDS and values to an env -> (parameterList = LIST of IDS, evaluatedArgumentList = List of Values);

        Lexeme fxnBody = closure.getLeft().getRight();
        return eval(fxnBody, callEnvironment);

    }

}
