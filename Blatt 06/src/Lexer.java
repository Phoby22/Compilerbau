import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    String input;
    char peek;
    int pos = 0;

    public record Token(String name, String character) {}

    public Lexer(String input) {
        this.input = input;
        this.peek = input.charAt(pos);
    }

    public void consume() {
        pos++;
        if (pos >= input.length()) {
            peek = '\0';
        } else {
            peek = input.charAt(pos);
        }
    }

    public Token nextToken() {
        while(peek != '\0') {
            switch (peek) {
                case ' ' : case '\t' : case '\n' : WS(); continue;
                case '(' : consume(); return new Token("LBRACK", "(");
                case ')' : consume(); return new Token("RBRACK", ")");
                case '*' : consume(); return new Token("MUL", "*");
                case '/' : consume(); return new Token("DIV", "/");
                case '+' : consume(); return new Token("ADD", "+");
                case '-' : consume(); return new Token("SUB", "-");
                case '=' : consume(); return new Token("EQUALS", "=");
                case '<' : consume(); return new Token("LESSER", "<");
                case '>' : consume(); return new Token("GREATER", ">");
                default:
                    if (Character.isLetter(peek)) {
                        return checkWord();
                    }
                    if (Character.isDigit(peek)) {
                        return checkDigit();
                    }
                    if (peek == '"') {
                        return checkString();
                    }
                    System.out.println("Error! invalid character!");
                    return new Token("ERROR", "e");
            }
        }
        return new Token("EOF_Type", "EOF");
    }

    public Token checkWord() {
        StringBuilder sb = new StringBuilder();

        while (Character.isLetterOrDigit(peek) || peek == '_') {
            sb.append(peek);
            consume();
        }

        String word = sb.toString();

        switch (word) {
            //case "print" : return new Token("PRINT", "print");
            //case "str" : return new Token("STR", "str");
            case "def" : return new Token("DEF", "def");
            case "defn" : return new Token("DEFN", "defn");
            case "let" : return new Token("LET", "let");
            case "if" : return new Token("IF", "if");
            case "do" : return new Token("DO", "do");
            case "list" : return new Token("LIST", "list");
            case "nth" : return new Token("NTH", "nth");
            case "head" : return new Token("HEAD", "head");
            case "tail" : return new Token("TAIL", "tail");
            case "true" : case "false" : return new Token("BOOLEAN", word);
        }
        return new Token("ID", word);
    }

    public Token checkDigit(){
        StringBuilder sb = new StringBuilder();

        while (Character.isDigit(peek)) {
            sb.append(peek);
            consume();
        }

        return new Token("NUMBER", sb.toString());
    }

    public Token checkString() {
        consume(); // das " überspringen
        StringBuilder sb = new StringBuilder();
        while (peek != '"' && peek != '\0') {
            sb.append(peek);
            consume();
        }
        consume(); // abschließendes " überspringen
        return new Token("STRING", sb.toString());
    }

    public void WS() {
        while(peek == ' ' || peek == '\t' || peek == '\n'){
            consume();
        }
    }

    /*public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder input = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter?> ");

        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;   // leere Zeile beendet Eingabe
            input.append(line).append("\n");
        }

        Lexer lexer = new Lexer(input.toString());
        System.out.println("Ausgabe: " + input);

        Token current = lexer.nextToken();
        tokens.add(current);
        while (!current.name.equals("EOF_Type") && !current.character.equals("ERROR")) {
            current = lexer.nextToken();
            tokens.add(current);
        }

        System.out.println(tokens);
    }*/
}