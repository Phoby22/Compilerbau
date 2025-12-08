import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    Enviroment env;
    Scope scope;

    public Interpreter() {
        env = new Enviroment(null);
        scope = new Scope();
        this.env.define("print", new NativePrint(null, null));
        this.env.define("str", new NativeStr(null, null));
    }

    public void eval_init(Node t) {
        switch (t.type) {
            case "DEF" : def(t); break;
            case "DEFN" : defn(t); break;
            case "LET_DEF" : let_def(t); break;
        }
    }

    public Object eval(Node t) {
        switch (t.type) {
            case "IF_ELSE" : if_else(t); break;
            case "LET" : let(t); break;
            case "DEFN_CALL" : defn_call(t); break;
            case "MUL" : return mul(t);
            case "DIV" : return div(t);
            case "ADD" : return add(t);
            case "SUB" : return sub(t);
            case "EQUALS" : return equals(t);
            case "GREATER" : return greater(t);
            case "LESSER" : return lesser(t);
            case "ID" : return this.env.get(t.value);
            case "STRING" : case "BOOLEAN" : return t.value;
            case "NUMBER" : return Double.parseDouble(t.value);
        }
        return null;
    }

    public double mul(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double) lhs * (double) rhs;
    }

    public double div(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double) lhs / (double) rhs;
    }

    public double add(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double) lhs + (double) rhs;
    }

    public double sub(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double) lhs - (double) rhs;
    }

    public boolean equals(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return  (double)lhs == (double)rhs;
    }

    public boolean greater(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double)lhs > (double)rhs;
    }

    public boolean lesser(Node t) {
        Object lhs = eval(t.children.get(0));
        Object rhs = eval(t.children.get(1));
        return (double) lhs < (double) rhs;
    }

    public void if_else(Node t) {
        if ((boolean)eval(t.children.get(0))) {
            eval(t.children.get(1).children.get(0));
        } else {
            eval(t.children.get(1).children.get(1));
        }
    }

    public void def(Node t) {

        String name = t.children.get(0).value;

        Object value = eval(t.children.get(1));

        if (this.env.values.get(name) != null) {
            System.out.printf("%s wurde schon im Scope definiert!\n", name);
            return;
        }

        this.env.define(name, value);
    }

    public void let(Node t) {
        Enviroment prev = this.env;

        try {
            this.env = new Enviroment(this.env);
            for (int i = 0; i < t.children.size(); i++) {
                eval(t.children.get(i));
            }
            this.env = prev;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void let_def(Node t) {
        int count = 0;
        while (true) {
            String name = t.children.get(count).value;
            count++;
            Object value = eval(t.children.get(count));
            count++;

            this.env.define(name, value);
            if (count >= t.children.size()) {
                break;
            }
        }
    }

    public void defn(Node t) {
        Fun fn = new Fun(t, this.env);
        this.env.define(t.children.get(0).value, fn);
    }

    public void defn_call(Node t) {
        Fun fn = (Fun)eval(t.children.get(0));

        List<Object> args = new ArrayList<>();
        for (int i = 1; i < t.children.size(); i++) {
            args.add(eval(t.children.get(i)));
        }

        fn.call(this, args);
    }
}
