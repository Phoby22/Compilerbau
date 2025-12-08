import java.util.ArrayList;
import java.util.List;

public class Parser {
    Lexer lexer;
    Lexer.Token lookahead;
    List<Node> ast = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        lookahead = lexer.nextToken();
    }

    public void match(String x) {
        if (lookahead.name().equals(x) || lookahead.character().equals(x)) {
            consume();
        } else {
            System.out.println("Error! missing or false character!");
        }
    }

    public void consume() {
        lookahead = lexer.nextToken();
    }

    public boolean predict_s_expr(String l) {
        if (!l.equals("LBRACK") && !l.equals("STRING") && !l.equals("NUMBER") && !l.equals("ID") && !l.equals("BOOLEAN")) {
            return true;
        }
        return false;
    }

    public void program() {
        while (true) {
            ast.add(s_expr());
            String l = lookahead.name();
            if (predict_s_expr(l)) {
                break;
            }
        }
    }

    public Node s_expr() {
        switch (lookahead.name()) {
            case "LBRACK" : return list_expr();
            case "STRING" : case "NUMBER" : case "ID" : case "BOOLEAN" : return atom();
            default :
                System.out.println("Error! s_expr could not be matched!");
        }
        return null;
    }

    public Node list_expr() {
        match("(");

        String type = lookahead.name();
        String value = lookahead.character();

        Node node;
        switch (lookahead.name()) {
            case "MUL" : case "DIV" : case "ADD" : case "SUB" :
            case "EQUALS" : case "LESSER" : case "GREATER" :
                node = new Node(lookahead.name(), lookahead.character());
                operator();
                int count = 0;
                while(!predict_s_expr(lookahead.name())) {
                    if (count == 2) {
                        Node node2 = new Node(type, value);
                        node2.children.add(node);
                        node = node2;
                        count--;
                    }
                    node.children.add(s_expr());
                    count++;
                }
                match(")");
                return node;
            //case "PRINT" : case "STR" :
            case "LIST" : case "NTH" : case "HEAD" : case "TAIL" :
                node = new Node(lookahead.name(), lookahead.character());
                operator();
                while(!predict_s_expr(lookahead.name())){
                    node.children.add(s_expr());
                }
                match(")");
                return node;
            case "IF" : node = new Node("IF_ELSE"); match(lookahead.name());
                node.children.add(s_expr());
                match(lookahead.character());
                node.children.add(if_expr());
                match(")");
                return node;
            case "DEF" : return def_expr();
            case "DEFN" : return defn_expr();
            case "LET" : return let_expr();
            case "ID" : return defn_call_expr();
            default:
                System.out.println("Error! list_expr could not be matched!");
        }

        match(")");

        return null;
    }

    public Node atom() {
        Node node;
        String value = lookahead.character();
        switch (lookahead.name()) {
            case "STRING" : node = new Node("STRING", value); match(value); return node;
            case "ID" : node = new Node("ID", value); match(value); return node;
            case "NUMBER": node = new Node("NUMBER", value); match(value); return node;
            case "BOOLEAN": return booLean();
            default:
                System.out.println("Error! atom could not be matched!");
        }
        return null;
    }

    public Node booLean() {
        Node node;
        switch (lookahead.character()) {
            case "true" : node = new Node("BOOLEAN", "true"); match("true"); return node;
            case "false" : node = new Node("BOOLEAN", "false"); match("false"); return node;
            default:
                System.out.println("Error! boolean could not be matched!");
        }
        return null;
    }

    public void operator() {
        switch (lookahead.character()) {
            case "*" : case "/" : case "+" : case "-" : aop(); break;
            case "=" : case "<" : case ">" : vop(); break;
            case "print" : match("print"); break;
            case "str" : match("str"); break;
            case "list" : match("list"); break;
            case "nth" : match("nth"); break;
            case "head" : match("head"); break;
            case "tail" : match("tail"); break;
            default:
                System.out.println("Error! operator could not be matched!");
        }
    }

    public Node aop() {
        String character = lookahead.character();
        Node node;
        switch(character) {
            case "*" : node = new Node("MUL", "*"); match(lookahead.character()); return node;
            case "/" : node =new Node("DIV", "/"); match(lookahead.character()); return node;
            case "+" : node = new Node("ADD", "+"); match(lookahead.character()); return node;
            case "-" : node = new Node("SUB", "-"); match(lookahead.character()); return node;
            default:
                System.out.println("Error! aop could not be matched!");
        }
        return null;
    }

    public Node vop() {
        String character = lookahead.character();
        Node node;
        switch(character) {
            case "=" : node = new Node("ASSIGN", "="); match(character); return node;
            case "<" : node = new Node("LESSER", "<"); match(character); return node;
            case ">" : node = new Node("GREATER", ">"); match(character); return node;
            default:
                System.out.println("Error! vop could not be matched!");
        }
        return null;
    }

    public Node if_expr() {
        Node node = null;
        switch (lookahead.name()) {
            case "LBRACK" : case "STRING" : case "NUMBER" : case "ID" : case "BOOLEAN" :
                node = new Node("IF");
                node.children.add(s_expr());
                //match(")");
                if (!predict_s_expr(lookahead.name())) {
                    node.children.add(s_expr());
                }
                break;
            case "do" :
                node = new Node("DO");
                match("do");
                while(true) {
                    node.children.add(s_expr());
                    if (predict_s_expr(lookahead.name())) {
                        break;
                    }
                }
                break;
        }
        return node;
    }

    public Node def_expr() {
        Node node = new Node("DEF");

        match("def");

        node.children.add(new Node("ID", lookahead.character()));
        match("ID");

        node.children.add(s_expr());

        match(")");

        return node;
    }

    public Node defn_expr() {
        Node node = new Node("DEFN");
        match("defn");

        node.children.add(new Node("ID", lookahead.character()));
        match("ID");

        match("(");

        while(lookahead.name().equals("ID")) {
            node.children.add(new Node("ID", lookahead.character()));
            match("ID");
        }

        match(")");

        node.children.add(s_expr());

        match(")");

        return node;
    }

    public Node defn_call_expr() {
        Node node = new Node("DEFN_CALL");

        node.children.add(new Node("ID", lookahead.character()));
        match("ID");

        while(true) {
            node.children.add(s_expr());
            if (predict_s_expr(lookahead.name())) {
                break;
            }
        }

        match(")");

        return node;
    }

    public Node let_expr() {
        Node node = new Node("LET");
        match("let");

        node.children.add(let_def_expr());

        while(true) {
            node.children.add(s_expr());
            if (predict_s_expr(lookahead.name())) {
                break;
            }
        }

        match(")");
        return node;
    }

    public Node let_def_expr() {
        Node node = new Node("LET_DEF");

        while(true) {
            match("(");
            node.children.add(new Node("ID", lookahead.character()));
            match("ID");
            node.children.add(s_expr());
            match(")");
            if (!lookahead.name().equals("ID")) {
                break;
            }
        }
        match(")");
        return node;
    }
}
