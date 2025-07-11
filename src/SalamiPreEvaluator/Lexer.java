package SalamiPreEvaluator;

import SalamiPreEvaluator.types.TokenType;
import java.io.File;
import java.io.FileNotFoundException;

import Helper.Logger.Logger;
import Helper.Logger.Timer;

import java.io.InputStream;
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
        finaltk.addToken(new Token(TokenType.EOF, "EndOfLine",finaltk.getEndLocation()));
        return finaltk;
    }
    public static TokenizedList lex(InputStream stream) throws LexerException, FileNotFoundException {
        return tokenizeStream(stream);
    }

    public static TokenizedList tokenizeStream(InputStream f)
            throws FileNotFoundException, LexerException {
        Timer lextimer = new Timer("LexerTime");
        int lineindex = 1;
        TokenizedList tk = new TokenizedList();
        Scanner scan = new Scanner(f);

        while (scan.hasNextLine()) {
            TokenizedList nextLine = tokenizeLine(scan.nextLine(), lineindex);
            if (scan.hasNextLine()) {
                nextLine.addToken(new Token(TokenType.NEWLINE, String.valueOf(lineindex), new int[]{lineindex, 0}));
            }
            tk.addTokens(nextLine);
            lineindex++;
        }
        // end of the file here
        tk.addToken(new Token(TokenType.EOF, "",tk.getEndLocation()));

        //tk.get();
        scan.close();
        logger.whisper("Took "+lextimer.time()+" milliseconds (LexerStream)");
        return tk;
    }

    public static TokenizedList tokenizeFile(File f)
    throws FileNotFoundException, LexerException {
        Timer lextimer = new Timer("LexerTime");
        int lineindex = 1;
        TokenizedList tk = new TokenizedList();
        Scanner scan = new Scanner(f);

        while (scan.hasNextLine()) {
            TokenizedList nextLine = tokenizeLine(scan.nextLine(), lineindex);
            if (scan.hasNextLine()) {
                nextLine.addToken(new Token(TokenType.NEWLINE, String.valueOf(lineindex), new int[]{lineindex, 0}));
            }
            tk.addTokens(nextLine);
            lineindex++;
        }
        // end of the file here
        tk.addToken(new Token(TokenType.EOF, "",tk.getEndLocation()));

        //tk.get();
        scan.close();
        logger.whisper("Took "+lextimer.time()+" milliseconds (LexerFile)");
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
            tk.addToken(new Token(TokenType.COMMENT, line, new int[]{lineindex, index+1}));
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
                // unicode now so better identifiers
                while (index < line.length() && Character.isUnicodeIdentifierPart(line.charAt(index))) {
                    index++;
                }
                String subid = line.substring(start, index);// Extract the identifier
                switch (subid) { // swapped if statements with switch statement
                    case "void" -> tk.addToken(new Token(TokenType.VOID, "Void", new int[]{lineindex, start + 1}));
                    case "set" -> tk.addToken(new Token(TokenType.SET, "set", new int[]{lineindex, start + 1}));
                    case "to" -> tk.addToken(new Token(TokenType.TO, "to", new int[]{lineindex, start + 1}));
                    case "finally" -> tk.addToken(new Token(TokenType.FINALLY, "finally", new int[]{lineindex, start + 1}));
                    case "label" -> tk.addToken(new Token(TokenType.LABELSET, "label", new int[]{lineindex, start + 1}));
                    case "jump" -> tk.addToken(new Token(TokenType.JUMP, "jump", new int[]{lineindex, start + 1}));
                    case "comp" -> tk.addToken(new Token(TokenType.COMP, "comp", new int[]{lineindex, start + 1}));
                    case "print" -> tk.addToken(new Token(TokenType.PRINT, "print", new int[]{lineindex, start + 1}));
                    case "sub" -> tk.addToken(new Token(TokenType.SUB, "sub", new int[]{lineindex, start + 1}));
                    case "subend" -> tk.addToken(new Token(TokenType.SUBEND, "subend", new int[]{lineindex, start + 1}));
                    case "return" -> tk.addToken(new Token(TokenType.RETURN, "return", new int[]{lineindex, start + 1}));
                    case "of" -> tk.addToken(new Token(TokenType.OF, "of", new int[]{lineindex, start + 1}));
                    case "throw" -> tk.addToken(new Token(TokenType.THROW, "throw", new int[]{lineindex, start + 1}));
                    case "from" -> tk.addToken(new Token(TokenType.FROM, "from", new int[]{lineindex, start + 1}));
                    case "port" -> tk.addToken(new Token(TokenType.PORT, "port", new int[]{lineindex, start + 1}));
                    case "the" -> {} // skip silently (filler word)
                    default -> tk.addToken(new Token(TokenType.ID, subid, new int[]{lineindex, start + 1}));
                }
                continue; //whoops almost forgot this
            }

            // Handle numbers (digits)

            if (Character.isDigit(currentChar)) {
                int start = index;

                TokenType dtype = TokenType.NUM; // Default to integer type

                // Consume digits before the decimal point
                while (index < line.length() && Character.isDigit(line.charAt(index))) {
                    index++;
                }

                // Check for a decimal point
                if (index < line.length() && line.charAt(index) == '.') {
                    index++; // Consume the decimal point

                    // Check for digits after the decimal point
                    if (index < line.length() && Character.isDigit(line.charAt(index))) {
                        dtype = TokenType.FLOAT; // Update type to FLOAT
                        while (index < line.length() && Character.isDigit(line.charAt(index))) {
                            index++;
                        }
                    } else {
                        throw new LexerException("Invalid floating-point number at index " + index,new int[]{lineindex, index+1});
                    }
                }

                // Extract the number and add the token
                String num = line.substring(start, index); // Extract the entire number
                tk.addToken(new Token(dtype, num, new int[]{lineindex, index+1}));
                continue;
            }

            // Handle Two-Character Operators
            if (index + 1 < line.length()){
                String twoCharOp = line.substring(index, index + 2);
                boolean isanoperator = isOp(twoCharOp);
                if (isanoperator){
                    if (twoCharOp.equals("++")){
                        tk.addToken(new Token(TokenType.INCREMENT, twoCharOp, new int[]{lineindex, index+1}));
                        index+=2;
                        continue;
                    }
                    tk.addToken(new Token(TokenType.OP, twoCharOp, new int[]{lineindex, index+1}));
                    index+=2;
                    continue;
                }
            }

            // Handle Operators
            if (isOp(currentChar)){
                tk.addToken(new Token(TokenType.OP, String.valueOf(currentChar), new int[]{lineindex, index+1}));
                index++;
                continue;
            }

            // Handle Delimiters
            if (isDelimiter(currentChar)){
                TokenType type = switch (currentChar) {
                    case '(' -> TokenType.LGROUPING;
                    case ')' -> TokenType.RGROUPING;
                    case '{' -> TokenType.LARRAY;
                    case '}' -> TokenType.RARRAY;
                    case '[' -> TokenType.LCOMPARE;
                    case ']' -> TokenType.RCOMPARE;
                    default -> throw new LexerException("Unknown delimiter at index " + index,new int[]{lineindex, index+1});
                };
                tk.addToken(new Token(type, String.valueOf(currentChar), new int[]{lineindex, index+1}));
                index++; // Move to the next character
                continue;
            }

            // HANDLE STRINGS why are we caps locked??
            if (currentChar == '\'' || currentChar == '"') {
                char stringQuotationType = '"';
                if (currentChar == '\''){
                    stringQuotationType = '\'';
                }
                //index++; // Skip the opening quote
                StringBuilder stringBuilder = new StringBuilder();
                boolean stringEnded = false;
                boolean escaped = false;
                while (!stringEnded){
                    index++;
                    if (index>=line.length()){break;}

                    char curcar = line.charAt(index);
                    if (escaped){
                        escaped = false;
                        switch (curcar){
                            case '\'', '"', '\\': stringBuilder.append(curcar); break;
                            case 'n': stringBuilder.append("\n"); break;
                            default: stringBuilder.append('\\'+curcar);
                        }
                    } else {
                        if (curcar==stringQuotationType){
                            stringEnded = true;
                            continue;
                        }
                        switch (curcar) {
                            case '\\':
                                escaped = true;
                                continue;
                            default:
                                stringBuilder.append(curcar);
                        }
                    }
                    if (stringEnded) break;
                }
                if (!stringEnded){
                    throw new LexerException("Unclosed String Literal at line [" + lineindex + ", " + (index+1)+"]",new int[]{lineindex, index+1});
                }
                tk.addToken(new Token(TokenType.STRING, stringBuilder.toString(), new int[]{lineindex, index+1}));
                index++;
                continue;
            }
            if (currentChar == ';') {
                tk.addToken(new Token(TokenType.SEMICOLON, "SEMICOLON", new int[]{lineindex, index+1}));
                index++;
                continue;
            }
            // Handle unknown/invalid characters
            throw new LexerException("Undetermined Character at line [" + lineindex + ", " + (index+1) + "]: \"" +currentChar+"\"",new int[]{lineindex, index+1});
        }

        //logger.log(line);
        //logger.log(tk);
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

