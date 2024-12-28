package SalamiEvaluator;

import SalamiEvaluator.types.Type;
import SalamiEvaluator.types.ast.*;

import java.io.FileNotFoundException;


public class Parser {
    static int current = 0;
    static TokenizedList tokens = new TokenizedList();
    public Parser(){}

    // create a list of variables which can easily be created and getted
    // create a list of functions which can be made with a certain id which refernce an actual function call
    // kind of like:
    // new KeyFunction(functionthathandlesjumpning, "jump");
    // create an AST tree and classes for the such
    private static void resetParser(){
        current = 0;
        tokens.clear();
    }
    public static ProgramNode parseFile(String source) throws ParserException, LexerException, FileNotFoundException {
        resetParser();
        tokens = Lexer.tokenizeFile(source);
        return parse();
    }
    public static ProgramNode parseLine(String source) throws ParserException, LexerException, FileNotFoundException{
        resetParser();
        tokens = Lexer.tokenizeLine(source,0);
        tokens.addToken(new Token(Type.EOF, "EndOfLine")); // bandaid solution
        return parse();
    }
    public static ProgramNode parse() throws ParserException, LexerException, FileNotFoundException{

        ProgramNode p = new ProgramNode();
        while (!isEnd()) {
            // do the parsing till it ends :(
            p.addStatement(parseStatement());
        }
        return p;
    }
    // Order of Presidence :)


    public static ExpressionNode parseAdditiveExpression() throws ParserException{
        ExpressionNode left = parseMultiplicativeExpression();
        while (grabCurrentToken().getValue().equals("+") | grabCurrentToken().getValue().equals("-")){
            String op = advance().getValue();
            ExpressionNode right = parseMultiplicativeExpression();
            left = new BinaryExpressionNode(left, op, right);
        }
        return left;
    }
    public static ExpressionNode parseMultiplicativeExpression() throws ParserException{
        ExpressionNode left = parseExpression();
        while (grabCurrentToken().getValue().equals("/") | grabCurrentToken().getValue().equals("*") | grabCurrentToken().getValue().equals("%")){
            String op = advance().getValue();
            ExpressionNode right = parseExpression();
            left = new BinaryExpressionNode(left, op, right);
        }
        return left;
    }


    //finish parse statements for stuff
    public static StatementNode parseStatement() throws ParserException{
        switch (grabCurrentToken().getType()) {
            case COMMENT: return new CommentNode(advance().getValue());
            default: return parseGeneralExpression();
        }
        //throw new ParserException("Unexpected Token at "+grabCurrentToken());
    }

    public static ExpressionNode parseGeneralExpression()  throws ParserException {
        return parseAdditiveExpression();
    }

    public static ExpressionNode parseExpression() throws ParserException{
        // id just like to break down whats going on here for future me

        // we create a switch statement which has cases depending on the type of the current token.
        // also its gonna yell at you to use an "enhanced switch" but don't because its more readable without it :)
        switch (grabCurrentToken().getType()) {
            case ID: return new IdentifierNode(advance().getValue());

            case VOID: advance(); return new VoidLiteralNode();

            // if the current token is an identifier, then return an identifier node with the value of the token.
            // this value is obtained by calling "advance" which is just a fancy way of saying:
            // "give me the value, but also increment current by one in the process"
            case NUM: return new NumericalLiteralNode(Integer.parseInt(advance().getValue())); // all token values are strings. dont forget that.

            case LGROUPING: advance(); ExpressionNode value = parseGeneralExpression(); eat(Type.RGROUPING, ")"); return value;
            default: throw new ParserException("Unexpected ExpressionNode at "+grabCurrentToken());
        }
    }

    public static Token advance(){
        Token prev = grabCurrentToken();
        current++;
        return prev;
    }

    public static boolean eat(Type t, String v) throws ParserException{
        if (grabCurrentToken().getValue().equals(v) && grabCurrentToken().getType() == t){
            advance();
            return true;
        }
        throw new ParserException("Expected \'"+v+"\' of type "+t+" but got "+grabCurrentToken()+" instead.");
    }

    public static boolean eat(Type t) throws ParserException{
        if (grabCurrentToken().getType() == t){
            advance();
            return true;
        }
        throw new ParserException("Expected type "+t+" but got "+grabCurrentToken().getType()+" instead.");
    }

    public static Token grabCurrentToken(){
        return tokens.grab(current);
    }
    public static Token peekAhead(){
        if (!isEnd()) return tokens.grab(current+1);
        return tokens.grab(current);
    }
    public static boolean isEnd(){
        return tokens.grab(current).type == Type.EOF;
    }


    // rethink the entire parser because i forgot
    // we know the TokenizedList can be indexed using grab(index)
    // we want to go through each token.
    // need a function to check if the token equals a type and lexeme, before current++;
}
