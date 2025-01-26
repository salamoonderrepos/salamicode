package SalamiEvaluator;

import SalamiEvaluator.types.Type;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import Logger.Logger;
import java.util.Scanner;

/**
 * <p>Manages converting the raw text data into a list of tokens for the {@link Parser} to parse.</p>
 * <p>For example:<br>
 * <code>set x to 32+1</code> <br>
 * would turn into <br>
 * <code>[SET: "set"], [ID: "x"], [TO: "to"], [NUM: "32"], [OP: "+"], [NUM: "1]</code>
 * </p>
 */
public class Lexer {
    public static final Logger logger = new Logger("Lexer");
    public static final String[] OPERATORS = {"%","*", "-", "/", "+", "==", "!=", ">", "<", ">=", "<=", "++","-*", "&", "|", "!"};
    public static final String DELIMITERS = "{}[]()";
    public static final String[] KEYWORDS = {"set", "to"};
    public Lexer(){}

    public static TokenizedList lex(File FILE) throws LexerException, FileNotFoundException{
        return tokenizeFile(FILE);
    }
    public static TokenizedList lex(String source) throws LexerException{
        TokenizedList finaltk = tokenizeLine(source, 1);
        finaltk.addToken(new Token(Type.EOF, "EndOfLine"));
        return finaltk;
    }

    public static TokenizedList tokenizeFile(File f)
    throws FileNotFoundException, LexerException {
        int lineindex = 1;
        TokenizedList tk = new TokenizedList();
        Scanner scan = new Scanner(f);

        while (scan.hasNextLine()) {
            TokenizedList nextLine = tokenizeLine(scan.nextLine(), lineindex);
            if (scan.hasNextLine()) {
                nextLine.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex)));
            }
            tk.addTokens(nextLine);
            lineindex++;
        }
        // end of the file here
        tk.addToken(new Token(Type.EOF, ""));

        //tk.get();
        scan.close();
        return tk;
    }

    public static TokenizedList tokenizeLine(String line, int lineindex) throws LexerException{
        int index = 0; // imagine a pointer going from left to right for each line. index is the position of that pointer
        TokenizedList tk = new TokenizedList();
        if (line.isEmpty()) { //if the line is empty then add a newline token
            //tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex)));
            return tk;
        }


        // handle comments:
        if (line.startsWith("--")) { // if it starts with -- then add comment token
            tk.addToken(new Token(Type.COMMENT, line));
            //tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex))); // add newline token
            return tk;
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
                String subid = line.substring(start, index);// Extract the identifier
                if (subid.equals("void")) {tk.addToken(new Token(Type.VOID, "Void")); continue;}
                if (subid.equals("set")) {tk.addToken(new Token(Type.SET, "set")); continue;}
                if (subid.equals("to")) {tk.addToken(new Token(Type.TO, "to")); continue;}
                if (subid.equals("finally")) {tk.addToken(new Token(Type.FINALLY, "finally")); continue;}
                if (subid.equals("label")) {tk.addToken(new Token(Type.LABELSET, "label")); continue;}
                if (subid.equals("jump")) {tk.addToken(new Token(Type.JUMP, "jump")); continue;}
                if (subid.equals("comp")) {tk.addToken(new Token(Type.COMP, "comp")); continue;}
                if (subid.equals("print")) {tk.addToken(new Token(Type.PRINT, "print")); continue;}
                if (subid.equals("sub")) {tk.addToken(new Token(Type.SUB, "sub")); continue;}
                if (subid.equals("subend")) {tk.addToken(new Token(Type.SUBEND, "subend")); continue;}
                if (subid.equals("return")) {tk.addToken(new Token(Type.RETURN, "return")); continue;}
                tk.addToken(new Token(Type.ID, subid));
                continue;
            }

            // Handle numbers (digits)

            if (Character.isDigit(currentChar)) {
                int start = index;

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
                    if (twoCharOp.equals("++")){
                        tk.addToken(new Token(Type.INCREMENT, twoCharOp));
                        index+=2;
                        continue;
                    }
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
            if (currentChar == ';') {
                tk.addToken(new Token(Type.SEMICOLON, "END OF LINE"));
                index++;
                continue;
            }
            // Handle unknown/invalid characters
            throw new LexerException("Undetermined Character at line [" + lineindex + ", " + (index+1) + "]: \"" +currentChar+"\"");
        }

        logger.log(line);
        logger.log(tk);
        return tk;
        //tk.addToken(new Token(Type.NEWLINE, String.valueOf(lineindex)));
    }

    private static boolean hasNextCharacter(String a, int i){
        return i + 1 < a.length();
    }

    private static boolean isOp(char char1, char char2){
        String op = Character.toString(char1) + char2;
        for (String actop : OPERATORS){
            if (op.equals(actop)){
                return true;
            }
        }
        return false;
    }
    private static boolean isOp(char char1){
        for (String actop : OPERATORS){
            if (Character.toString(char1).equals(actop)){
                return true;
            }
        }
        return false;
    }
    private static boolean isOp(String foo){
        for (String actop : OPERATORS){
            if (foo.equals(actop)){
                return true;
            }
        }
        return false;
    }
    private static boolean isDelimiter(char foo){
        return DELIMITERS.indexOf(foo) != -1;
    }
}

