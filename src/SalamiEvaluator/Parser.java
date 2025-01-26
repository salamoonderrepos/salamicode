package SalamiEvaluator;

import Logger.Logger;
import SalamiEvaluator.types.Type;
import SalamiEvaluator.types.ast.*;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * <p>Takes a list of tokens and parses them into an AST node tree.</p>
 * @see SalamiEvaluator.types.ast
 */
public class Parser {
    static int current = 0;
    static TokenizedList tokens = new TokenizedList();
    public static final Logger logger = new Logger("Parser");
    public Parser(){}
    // create an AST tree and classes for the such
    private static void resetParser(){
        current = 0;
        tokens.clear();
    }
    public static ProgramNode parseFile(File source) throws ParserException, LexerException, FileNotFoundException {
        resetParser();
        tokens = Lexer.lex(source);
        return parse();
    }
    public static ProgramNode parseLine(String source) throws ParserException, LexerException{
        resetParser();
        tokens = Lexer.lex(source);
        return parse();
    }
    public static ProgramNode parse() throws ParserException, LexerException{

        ProgramNode p = new ProgramNode();
        while (!isEnd()) {
            // do the parsing till it ends :(
            StatementNode nextStatement = parseStatement();
            p.addStatement(nextStatement);
            logger.log(nextStatement);
        }
        return p;
    }
    // Order of Presidence :)

    // Statements
    // Groupings
    // Unary
    // Multiplicative
    // Addititive
    // Literals
    // Logical Relations
    // Logical Equitors
    // Logical Comparaters


    // PLEASE DO THESE THINGS:
    // - ~~Add a general parser/evaluator to get the labels out of the way from the beginning~~ DONE!
    // - make the port function to access other environments
    // - add the "of" keyword
    //finish parse statements for stuff



    //            STATEMENTS               //

    public static StatementNode parseStatement() throws ParserException{
        if (peekAhead().type==Type.INCREMENT){ // check if an increment statement is present before evaluating expressions.
            return parseIncrementStatement();
        }
        if (grabCurrentToken().getType()==Type.NEWLINE){
            advance();
            return parseStatement();
        }
        switch (grabCurrentToken().getType()) {
            case COMMENT: return new CommentNode(advance().getValue());
            case SET: return parseSetStatement();
            case LABELSET: return parseLabelStatement();
            case JUMP: return parseJumpStatement();
            case COMP: return parseCompareStatement();
            case PRINT: return parsePrintStatement();
            case SUB: return parseSubroutineDeclarationStatement();
            case SUBEND: advance(); return new SubroutineEndIndicator();
            case RETURN: advance(); return new ReturnStatement(parseStatement());

            // move on to the next order
            default: return parseGeneralExpression();
        }
        //throw new ParserException("Unexpected Token at "+grabCurrentToken());
    }
    // SET x TO 40
    // SET b TO 'cunkonearth'-'onearth'
    // SET foo TO 32.2 FINALLY
    public static StatementNode parsePrintStatement() throws ParserException{
        advance();
        ExpressionNode exp = parseGeneralExpression();
        eatEndOfStatement();
        return new PrintStatement(exp);
    }
    public static StatementNode parseSetStatement() throws ParserException{
        boolean isfinal=false;
        advance();
        final Token identifier = eat(Type.ID);
        eat(Type.TO);
        final ExpressionNode value = parseGeneralExpression();
        if (grabCurrentToken().type == Type.FINALLY){
            eat(Type.FINALLY);
            isfinal=true;
        }
        eatEndOfStatement();
        return new VariableDeclarationStatement(identifier.getValue(), value, isfinal);

    }
    public static StatementNode parseCompareStatement() throws ParserException{
        advance();
        eat(Type.LCOMPARE);
        final ExpressionNode compareexp = parseLogicalExpression();
        eat(Type.RCOMPARE);
        final StatementNode exec = parseStatement();
        //eatEndOfStatement();  // we dont need to parse end of statement because the parseStatement(); already does that
        return new CompareStatement(compareexp,exec);
    }
    public static StatementNode parseLabelStatement() throws ParserException{
        advance();
        final Token identifier = eat(Type.ID);
        eatEndOfStatement();
        return new LabelDeclarationStatement(identifier.getValue());

    }
    public static StatementNode parseJumpStatement() throws ParserException{
        advance();
        final Token identifier = eat(Type.ID);
        eatEndOfStatement();
        return new JumpStatement(identifier.getValue());

    }
    public static StatementNode parseIncrementStatement() throws ParserException{
        final Token identifier = eat(Type.ID);
        eat(Type.INCREMENT);
        eatEndOfStatement();
        return new ExpressionIncrementStatement(identifier.getValue());
        //return new ExpressionIncrementStatement("x");

    }

    public static StatementNode parseSubroutineDeclarationStatement() throws ParserException{
        advance();
        final Token identifier = eat(Type.ID);
        final ArrayList<String> params = parseParameterList();
        eatEndOfStatement();
        final ProgramNode code = parseCodeBlock(Type.SUBEND);
        eatEndOfStatement();
        return new SubroutineDeclarationStatement(identifier.getValue(), params, code);

    }

    public static ProgramNode parseCodeBlock(Type endingindicator) throws ParserException{
        ProgramNode programNode = new ProgramNode();
        while (current<tokens.tokens.size()){
            if (isStatementEnder()){
                advance();
            }
            if (grabCurrentToken().getType()==endingindicator){
                advance();
                return programNode;
            }
            programNode.addStatement(parseStatement());

        }
        throw new ParserException("EOF reached without ending indicator: "+endingindicator);
    }

    public static ExpressionNode parseSubroutineCallStatement() throws ParserException{
        final Token identifier = eat(Type.ID);
        final ArrayList<ExpressionNode> params = parseParameterExpressionList();
        return new SubroutineCallStatement(identifier.getValue(), params);
    }

    public static ArrayList<String> parseParameterList() throws ParserException{
        ArrayList<String> results = new ArrayList<>();
        eat(Type.LGROUPING);
        while (!(grabCurrentToken().getType()==Type.RGROUPING)){
            results.add(eat(Type.ID).getValue());
        }
        eat(Type.RGROUPING);
        return results;
    }
    public static ArrayList<ExpressionNode> parseParameterExpressionList() throws ParserException{
        ArrayList<ExpressionNode> results = new ArrayList<>();
        eat(Type.LGROUPING);
        while (!(grabCurrentToken().getType()==Type.RGROUPING)){
            results.add(parseGeneralExpression());
        }
        eat(Type.RGROUPING);
        return results;
    }

    // ------------
    // EXPRESSIONS
    // ------------

    // -------------- Groupings ---------------//
    public static ExpressionNode parseGeneralExpression()  throws ParserException {
        return parseAdditiveExpression();
    }

    // -------------- Unary ---------------//
    public static ExpressionNode parseUnaryExpression() throws ParserException {
        Token currentToken = grabCurrentToken();

        // Check for unary operators
        switch (currentToken.getValue()){
            case "!":
                String operator = advance().getValue(); // Consume the operator
                ExpressionNode operand = parseLogicalExpression(); // Recursively parse the operand
                return new UnaryExpressionNode(operator, operand, true);
            case "-":
                String negop = advance().getValue(); // Consume the operator
                ExpressionNode negativeoperand = parseExpression(); // Recursively parse the operand
                return new UnaryExpressionNode(negop, negativeoperand, false);
            default: throw new ParserException("Unexpected operator in expression: "+currentToken);
        }

    }

    // -------------- Additives ---------------//
    public static ExpressionNode parseAdditiveExpression() throws ParserException{
        ExpressionNode left = parseMultiplicativeExpression();
        while (grabCurrentToken().getValue().equals("+") | grabCurrentToken().getValue().equals("-") | grabCurrentToken().getValue().equals("-*")){
            String op = advance().getValue();
            ExpressionNode right = parseMultiplicativeExpression();
            left = new BinaryExpressionNode(left, op, right, false);
        }
        return left;
    }

    // -------------- Multiplicatives ---------------//
    public static ExpressionNode parseMultiplicativeExpression() throws ParserException{
        ExpressionNode left = parseExpression();
        while (grabCurrentToken().getValue().equals("/") | grabCurrentToken().getValue().equals("*") | grabCurrentToken().getValue().equals("%")){
            String op = advance().getValue();
            ExpressionNode right = parseExpression();
            left = new BinaryExpressionNode(left, op, right, false);
        }
        return left;
    }

    // -------------- Literals ---------------//
    public static ExpressionNode parseExpression() throws ParserException{
        // id just like to break down whats going on here for future me

        // we create a switch statement which has cases depending on the type of the current token.
        // also its gonna yell at you to use an "enhanced switch" but don't because its more readable without it :)
        switch (grabCurrentToken().getType()) {
            case OP: return parseUnaryExpression();
            case ID:
                if (peekAhead().getType()==Type.LGROUPING){
                    return parseSubroutineCallStatement();
                }
                return new IdentifierNode(advance().getValue());

            case VOID: advance(); return new VoidLiteralNode();

            // if the current token is an identifier, then return an identifier node with the value of the token.
            // this value is obtained by calling "advance" which is just a fancy way of saying:
            // "give me the value, but also increment current by one in the process"

            case FLOAT: return new FloatingLiteralNode(Float.parseFloat(advance().getValue()));

            case NUM: return new NumericalLiteralNode(Integer.parseInt(advance().getValue())); // all token values are strings. dont forget that.

            case STRING: return new StringLiteralNode(advance().getValue());


            case LGROUPING: advance(); ExpressionNode value = parseGeneralExpression(); eat(Type.RGROUPING, ")"); return value;
            case LCOMPARE: advance(); ExpressionNode logicvalue = parseLogicalExpression(); eat(Type.RCOMPARE, "]"); return logicvalue;
            default: throw new ParserException("Unexpected ExpressionNode at "+grabCurrentToken());
        }
    }

    // -------------- Comparatives ---------------//
    public static ExpressionNode parseLogicalExpression() throws ParserException{
        ExpressionNode left = parseSpecificLogicalExpression();
        while (grabCurrentToken().getValue().equals("&") |
                grabCurrentToken().getValue().equals("==") |
                grabCurrentToken().getValue().equals(">") |
                grabCurrentToken().getValue().equals("<") |
                grabCurrentToken().getValue().equals("<=") |
                grabCurrentToken().getValue().equals(">=") |
                grabCurrentToken().getValue().equals("|")
        ){
            String op = advance().getValue();
            ExpressionNode right = parseSpecificLogicalExpression();
            left = new BinaryExpressionNode(left, op, right, true);
        }
        return left;
    }

    public static ExpressionNode parseSpecificLogicalExpression() throws ParserException{
        switch (grabCurrentToken().getType()){
            case OP:
                if (grabCurrentToken().getValue().equals("!")){
                    String op = advance().getValue();
                    ExpressionNode right;
                    if (peekAhead().getType()==Type.LCOMPARE){
                        advance();
                        right = parseLogicalExpression();
                        eat(Type.RCOMPARE);
                    } else {
                        right = parseSpecificLogicalExpression();
                    }

                    return new UnaryExpressionNode(op, right, true);
                }
            case ID: return new IdentifierNode(advance().getValue());

            case VOID: advance(); return new VoidLiteralNode();
            case LCOMPARE: advance(); ExpressionNode stuff = parseLogicalExpression(); eat(Type.RCOMPARE); return stuff;
            default: return parseExpression();
        }
    }

    public static ExpressionNode parseNegativeGeneralExpression() throws ParserException{
        try {
            ExpressionNode negativeValue = parseGeneralExpression();
            return new UnaryExpressionNode("-", negativeValue, false);

        } catch (ClassCastException e) {
            throw new ParserException("Cannot negate a "+grabCurrentToken()+" type.");
        }
    }

    public static Token advance(){
        Token prev = grabCurrentToken();
        current++;
        return prev;
    }

    public static Token previous(){
        Token prev = grabCurrentToken();
        current--;
        return prev;
    }

    public static Token eat(Type t, String v) throws ParserException{
        if (grabCurrentToken().getValue().equals(v) && grabCurrentToken().getType() == t){
            return advance();
        }
        throw new ParserException("Expected \'"+v+"\' of type "+t+" but got "+grabCurrentToken()+" instead.");
    }
    public static void eatEndOfStatement() throws ParserException{
        if (grabCurrentToken().type!=Type.EOF){
            try {
                eat(Type.SEMICOLON, Type.NEWLINE);
            } catch (ParserException e){
                throw new ParserException("Expected indicator that the statement ended.");
            }
        }
    }
    public static boolean isStatementEnder() throws ParserException{
        return grabCurrentToken().getType() == Type.NEWLINE | grabCurrentToken().getType() == Type.SEMICOLON;
    }
    public static Token eat(Type... types) throws ParserException {
        for (Type t : types){
            if (grabCurrentToken().getType() == t) {
                return advance();
            }
        }
        throw new ParserException("Expected type "+types[0]+" but got "+grabCurrentToken().getType()+" instead.");
    }

    public static Token eat(Type t) throws ParserException{
        if (grabCurrentToken().getType() == t){
            return advance();
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
    public static Token peekBehind(){
        if (!isBeginning()) return tokens.grab(current-1);
        return tokens.grab(current);
    }
    public static boolean isEnd(){
        return tokens.grab(current).type == Type.EOF;
    }
    public static boolean isBeginning(){
        return current==0;
    }
    public static int find(Type t){
        int last = current;
        while (grabCurrentToken().getType()!=t){
            advance();
        }
        int result = current;
        current = last;
        return result;
    }


    // rethink the entire parser because i forgot
    // we know the TokenizedList can be indexed using grab(index)
    // we want to go through each token.
    // need a function to check if the token equals a type and lexeme, before current++;
}
