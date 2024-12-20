package SalamiEvaluator;

import SalamiEvaluator.types.Type;

import javax.management.RuntimeErrorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

class LexerException extends Throwable{
    public LexerException(String s){
        super(s);
    }
}

public class Lexer {
    public static final String FILE = "assets\\source\\test.salami";
    public static final String[] OPERATORS = {"*", "-", "/", "+"};
    public Lexer(){}

    public TokenizedList lex() {
        try {
            return tokenizeFile();
        } catch (FileNotFoundException | LexerException e){
            System.out.println(e.getMessage());
            return new TokenizedList();
        }
    }

    private TokenizedList tokenizeFile()
    throws FileNotFoundException, LexerException {
        int lineindex = 1;
        TokenizedList tk = new TokenizedList();
        File f = new File(FILE);
        Scanner scan = new Scanner(f);

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (Objects.equals(line, "")) {
                continue;
            }
            int index = 0;

            // handle comments:
            if (line.startsWith("--")) {
                tk.addToken(new Token(Type.COMMENT, line));
                continue;
            }
            while (index < line.length()) {
                char currentChar = line.charAt(index);

                // Skip whitespace
                if (Character.isWhitespace(currentChar)) {
                    index++;
                    continue;
                }

                // Handle identifiers (letters)
                if (Character.isLetter(currentChar)) {
                    int start = index;  // Start of the identifier
                    while (index < line.length() && Character.isLetterOrDigit(line.charAt(index))) {
                        index++;
                    }
                    String subid = line.substring(start, index);  // Extract the identifier
                    tk.addToken(new Token(Type.ID, subid));
                    continue;
                }

                // Handle numbers (digits)
                if (Character.isDigit(currentChar)) {
                    int start = index;  // Start of the number
                    while (index < line.length() && Character.isDigit(line.charAt(index))) {
                        index++;
                    }
                    String num = line.substring(start, index);  // Extract the number
                    tk.addToken(new Token(Type.NUM, num));
                    continue;
                }

                // Handle unknown/invalid characters
                throw new LexerException("Undetermined Character at line [" + lineindex + ", " + (index+1) + "]: \"" +currentChar+"\"");
            }

            System.out.println(line);
            lineindex++;
        }
        tk.get();
        scan.close();
        return tk;
    }
    private boolean hasNextCharacter(String a, int i){
        if (i + 1 < a.length()){return true;}
        return false;
    }
    private boolean isOp(String a){
        return false;
    }
}

