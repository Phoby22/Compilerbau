import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;
import my.pkg.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Main_Language {
    public static class MyLanguageListener extends LanguageBaseListener {
        boolean newLine = true;
        boolean skip_newline = false;
        int tab_count = 0;
        public void visitTerminal(TerminalNode node){
            String text = node.getText();
            int type = node.getSymbol().getType();

            if (type == LanguageLexer.EOF) {
                return;
            }

            if (text.equals("end")) {
                System.out.print("\b");
                tab_count = tab_count - 1;
            } else if (text.equals("else do")) {
                System.out.print("\b");
            }

            if (type == LanguageLexer.NEWLINE) {
                if (!skip_newline) {
                    System.out.print(text);
                    for (int i = 0; i < tab_count; i++) {
                        System.out.print("\t");
                    }
                } else {
                    System.out.print(" ");
                    skip_newline = false;
                }
                newLine = true;
                return;
            }

            if (!newLine) {
                System.out.print(" "); // Leerzeichen nur zwischen Tokens in derselben Zeile
            }
            System.out.print(text);
            newLine = false;

            if (text.equals("do")) {
                tab_count++;
            }
        }

        public void enterIf(LanguageParser.IfContext ctx) {
            if (ctx.nw != null) {
                skip_newline = true;
            }
        }

        public void enterWhile(LanguageParser.WhileContext ctx) {
            if (ctx.nw != null) {
                skip_newline = true;
            }
        }
    }

    sealed interface Stmt permits Stmt.ExprStmt, Stmt.ContStmt {
        record ExprStmt(Expr expr) implements Stmt {}
        record ContStmt(Cont cont) implements Stmt {}
    }

    sealed interface Expr permits Expr.MUL, Expr.DIV, Expr.ADD, Expr.SUB, Expr.VAR, Expr.STRING, Expr.ZAHL, Expr.ASSI {
        record MUL(Expr e1, Expr e2) implements Expr {}
        record DIV(Expr e1, Expr e2) implements Expr {}
        record ADD(Expr e1, Expr e2) implements Expr {}
        record SUB(Expr e1, Expr e2) implements Expr {}
        record VAR(String bezeichner) implements Expr {}
        record STRING(String string) implements Expr {}
        record ZAHL(int zahl) implements Expr {}
        record ASSI(String bz, Expr e) implements Expr {}
    }

    sealed interface Condition permits Condition.Comparison {
        record Comparison(Expr e1, String op, Expr e2) implements Condition {}
    }

    sealed interface Cont permits Cont.IfCont, Cont.WhileCont {
        record IfCont(Condition con, List<Stmt> doBlock, List<Stmt> elseBlock) implements Cont {}
        record WhileCont(Condition con, List<Stmt> doBlock) implements Cont {}
    }

    static Stmt toAst(LanguageParser.StmtContext s) {
        return switch(s) {
            case LanguageParser.ExprStmtContext e -> new Stmt.ExprStmt(toAst(e.expr()));
            case LanguageParser.ContStmtContext c -> new Stmt.ContStmt(toAst(c.cont()));
            default -> throw new IllegalStateException();
        };
    }

    static Expr toAst(LanguageParser.ExprContext e) {
        return switch(e) {
            case LanguageParser.MULContext m -> new Expr.MUL(toAst(m.e1), toAst(m.e2));
            case LanguageParser.DIVContext d -> new Expr.DIV(toAst(d.e1), toAst(d.e2));
            case LanguageParser.ADDContext a -> new Expr.ADD(toAst(a.e1), toAst(a.e2));
            case LanguageParser.SUBContext s -> new Expr.SUB(toAst(s.e1), toAst(s.e2));
            case LanguageParser.VARContext v -> new Expr.VAR(v.BEZEICHNER().getText());
            case LanguageParser.ZAHLContext z -> new Expr.ZAHL(Integer.parseInt(z.NUM().getText()));
            case LanguageParser.STRINGContext st -> new Expr.STRING(st.STRING().getText());
            case LanguageParser.ASSIContext as -> new Expr.ASSI(as.assign().bz.getText(), toAst(as.assign().e));
            default -> throw new IllegalStateException();
        };
    }

    static Cont toAst(LanguageParser.ContContext c) {
        return switch(c) {
            case LanguageParser.IfStmtContext i -> new Cont.IfCont(
                toAst(i.if_().c),                          // c = con
                i.if_().doBlock.stream().map(Main_Language::toAst).toList(),
                i.if_().elseBlock != null
                    ? i.if_().elseBlock.stream().map(Main_Language::toAst).toList()
                    : List.of()
            );
            case LanguageParser.WhileStmtContext w -> new Cont.WhileCont(
                toAst(w.while_().c),                          // w.c = con
                w.while_().doBlock.stream().map(Main_Language::toAst).toList()
            );
            default -> throw new IllegalStateException();
        };
    }

    static Condition toAst(LanguageParser.ConContext c) {
        return new Condition.Comparison(
            toAst(c.e1),          // Expr
            c.vp().getText(),     // Operator: "==", "!=", "<", ">"
            toAst(c.e2)           // Expr
        );
    }

    static List<Stmt> toAst(LanguageParser.ProgContext ctx) {
        return ctx.stmt().stream()
            .map(Main_Language::toAst)
            .toList();
    }

    static void main (String... args) throws IOException, URISyntaxException {
        StringBuilder input = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter?> ");

        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;   // leere Zeile beendet Eingabe
            input.append(line).append("\n");
        }

        System.out.println("Ausgabe:");

        LanguageLexer lexer = new LanguageLexer(CharStreams.fromString(input.toString()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LanguageParser parser = new LanguageParser(tokens);

        LanguageParser.ProgContext progCtx = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();
        MyLanguageListener listener = new MyLanguageListener();
        walker.walk(listener, progCtx);

        List<Stmt> ast = toAst(progCtx);

        System.out.println("AST:");
        System.out.println(ast);
    }
}
