package com.htam.environment;

import com.htam.Htam;
import com.htam.lexicalAnalysis.*;

import java.util.ArrayList;

import static com.htam.lexicalAnalysis.TokenType.*;

public class Environment {

    Environment parent;
    ArrayList<storedValues> storedValues = new ArrayList();

    public Environment(Environment pare) {
        this.parent = pare;
    }

    public void setParent(Environment parent) {
        this.parent = parent;
    }

    private class storedValues {

        private Lexeme ide;
        private Lexeme value;

        private storedValues(Lexeme ide) {
            ide = this.ide;
        }

        private storedValues(Lexeme lex, Lexeme value) {
            lex = this.ide;
            value = this.value;
        }

        public Lexeme getIde() {
            return ide;
        }

        public void setIde(Lexeme ide) {
            this.ide = ide;
        }

        public Lexeme getValue() {
            return value;
        }

        public void setValue(Lexeme value) {
            this.value = value;
        }
    }

    public void add(Lexeme lex) {
        storedValues val = new storedValues(lex);
        storedValues.add(val);
        val.setIde(lex);
        System.out.println("Added " + lex.getStrVal() + " to environment " + this);
    }

    public void add(Lexeme lex, Lexeme valu) {
        storedValues val = new storedValues(lex, valu);
        storedValues.add(val);
        val.setIde(lex);
        val.setValue(valu);
        System.out.println("Added " + lex.getStrVal() + " to environment " + this);
    }

    public void update(Lexeme idee, Lexeme newval) {
        int count = 0;
        System.out.println("Now updating variable " + idee.getStrVal() + " in environment " + this);
        for (int i = 0; i < storedValues.size(); i++) {
            storedValues sv = storedValues.get(i);
            System.out.println("Currently on " + sv);
            if (sv.getIde().getStrVal() == idee.getStrVal()) {
                if (sv.getValue().getType() == TAU_TRUE || sv.getIde().getType() == CAPITAL_PI_FALSE) {
                    boolean newVal = newval.getBooleanVal();
                    System.out.println("Updated value of " + sv.getIde().getStrVal() + " to be " + newVal);
                    sv.getValue().setBooleanVal(newVal);
                } else if (sv.getValue().getType() != newval.getType()) {
                    count = 1;
                    Htam.syntaxError("Cannot assign lexeme " + newval + " to variable " + sv.getIde(), 0);
                } else if (sv.getValue().getType() == NUMBER) {
                    double newVal = newval.getNumVal();
                    System.out.println("Updated value of " + sv.getIde().getStrVal() + " to be " + newVal);
                    sv.getValue().setNumVal(newVal);
                } else if (sv.getValue().getType() == STRING) {
                    String newVal = newval.getStrVal();
                    System.out.println("Updated value of " + sv.getIde().getStrVal() + " to be " + newVal);
                    sv.getValue().setStrVal(newVal);
                }
            }
        }
        if (this.parent != null && count == 1) {
            this.parent.update(idee, newval);
        } else if (count == 1) {
            System.out.println("Var not able to be updated in accessible environments.");
        }
    }

    public Lexeme lookup(Lexeme ide) {
        int found = softLookup(ide);
        if (found != -1) {
            return storedValues.get(found).getValue();
        } else {
            Htam.syntaxError("Error: " + ide + " not found in accessible environments.", 0);
            return null;
        }
    }

    private int softLookup(Lexeme ide) {
        for (int i = 0; i < storedValues.size(); i++) {
            Lexeme current = storedValues.get(i).getIde();
            if (current.equals(ide)) {
                return i;
            }
        }
        if (parent != null) {
            return parent.softLookup(ide);
        }
        return -1;
    }

    public void extend(Lexeme paramList, Lexeme argList) {
        if (paramList != null && argList != null) {
            Lexeme param = paramList.getLeft().getRight();
            Lexeme arg = argList.getLeft();
            if (param != null && arg != null) {
                add(param, arg);
                if (paramList.getRight() != null && argList.getRight() != null) {
                    extend(paramList.getRight(), argList.getRight());
                }
            }
        }
    }

    public void print() {
        for (int i = 0; i < storedValues.size(); i++) {
            System.out.println(i);
        }
    }

}
