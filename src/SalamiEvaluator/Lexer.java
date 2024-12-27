package SalamiEvaluator;

import SalamiEvaluator.types.Type;

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
    public static String FILE = "assets\\source\\test.salami";
    public static final String[] OPERATORS = {"*", "-", "/", "+", "=", "==", "!=", ">", "<", ">=", "<=", "++"};
    public static final String DELIMITERS = "{}[]()";
    public Lexer(String fileName){FILE=fileName;}
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
            int index = 0; // imagine a pointer going from left to right for each line. index is the position of that pointer

            String line = scan.nextLine(); // get next line
            if (Objects.equals(line, "")) { //if the line is empty then add a newline token
                lineindex++;
                tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex)));
                continue; // continue, basically just skip the rest of the code and go to the next line
            }


            // handle comments:
            if (line.startsWith("--")) { // if it starts with -- then add comment token
                tk.addToken(new Token(Type.COMMENT, line));
                lineindex++;
                tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex))); // add newline token
                continue;
            }

            // do all this code until the pointer reaches the end of the line. then go to the next line, and reset the index to 0.
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
                    Type dtype = Type.NUM; // Default to integer type

                    // Consume digits before the decimal point
                    while (index < line.length() && Character.isDigit(line.charAt(index))) {
                        index++;
                    }

                    // Check for a decimal point
                    if (index < line.length() && line.charAt(index) == '.') {
                        index++; // Consume the decimal point

                        // Check for digits after the decimal point
                        if (index < line.length() && Character.isDigit(line.charAt(index))) {
                            dtype = Type.FLOAT; // Update type to FLOAT
                            while (index < line.length() && Character.isDigit(line.charAt(index))) {
                                index++;
                            }
                        } else {
                            throw new LexerException("Invalid floating-point number at index " + index);
                        }
                    }

                    // Extract the number and add the token
                    String num = line.substring(start, index); // Extract the entire number
                    tk.addToken(new Token(dtype, num));
                    continue;
                }

                // Handle Two-Character Operators
                if (index + 1 < line.length()){
                    String twoCharOp = line.substring(index, index + 2);
                    boolean isanoperator = isOp(twoCharOp);
                    if (isanoperator){
                        tk.addToken(new Token(Type.OP, twoCharOp));
                        index+=2;
                        continue;
                    }
                }

                // Handle Operators
                if (isOp(currentChar)){
                    tk.addToken(new Token(Type.OP, String.valueOf(currentChar)));
                    index++;
                    continue;
                }

                // Handle Delimiters
                if (isDelimiter(currentChar)){
                    Type type = switch (currentChar) {
                        case '(' -> Type.LGROUPING;
                        case ')' -> Type.RGROUPING;
                        case '{' -> Type.LARRAY;
                        case '}' -> Type.RARRAY;
                        case '[' -> Type.LCOMPARE;
                        case ']' -> Type.RCOMPARE;
                        default -> throw new LexerException("Unknown delimiter at index " + index);
                    };
                    tk.addToken(new Token(type, String.valueOf(currentChar)));
                    index++; // Move to the next character
                    continue;
                }

                // HANDLE STRINGS why are we caps locked??
                if (currentChar == '\'') {
                    index++; // Skip the opening quote
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean strended = false;
                    while (index<line.length()){
                        char curcar = line.charAt(index);
                        switch (curcar){
                            case '\'': strended = true; break;
                            default: stringBuilder.append(curcar);
                        }
                        if (strended) break;
                        index++;
                    }
                    if (!strended){
                        throw new LexerException("Unclosed String Literal at line [" + lineindex + ", " + (index+1)+"]");
                    }
                    tk.addToken(new Token(Type.STRING, stringBuilder.toString()));
                    index++;
                    continue;
                }

                // Handle unknown/invalid characters
                throw new LexerException("Undetermined Character at line [" + lineindex + ", " + (index+1) + "]: \"" +currentChar+"\"");
            }

            System.out.println(line);
            lineindex++;
            tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex)));
        }
        // end of the file here
        tk.addToken(new Token(Type.EOF, ""));

        tk.get();
        scan.close();
        return tk;
    }
    private boolean hasNextCharacter(String a, int i){
        return i + 1 < a.length();
    }
    private boolean isOp(char char1, char char2){
        String op = Character.toString(char1) + char2;
        for (String actop : OPERATORS){
            if (op.equals(actop)){
                return true;
            }
        }
        return false;
    }
    private boolean isOp(char char1){
        for (String actop : OPERATORS){
            if (Character.toString(char1).equals(actop)){
                return true;
            }
        }
        return false;
    }
    private boolean isOp(String foo){
        for (String actop : OPERATORS){
            if (foo.equals(actop)){
                return true;
            }
        }
        return false;
    }
    private boolean isDelimiter(char foo){
        return DELIMITERS.indexOf(foo) != -1;
    }
}

