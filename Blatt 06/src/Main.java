import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        //REPL
        while (true) {
            StringBuilder input = new StringBuilder();
            System.out.println("Lisp?> ");

            while (true) {
                String line = scanner.nextLine();
                if (line.isEmpty()) break;   // leere Zeile beendet Eingabe
                input.append(line).append("\n");
            }

            if (input.toString().equals("exit\n")) {
                break;
            }

            Lexer lexer = new Lexer(input.toString());

            Parser parser = new Parser(lexer);

            parser.program();
            List<Node> ast = parser.ast;
            Interpreter ip = new Interpreter();

            for (Node n : ast) {
                ip.eval_init(n);
            }

            for (Node n : ast) {
                ip.eval(n);
            }
        }
    }
}
