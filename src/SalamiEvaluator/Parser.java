package SalamiEvaluator;

import SalamiEvaluator.types.Type;
import SalamiEvaluator.types.ast.*;
import java.text.ParseException;
import java.util.Arrays;

class ParserException extends Throwable{
    public ParserException(String s){
        super(s);
    }
}



public class Parser {
    Lexer l;
    TokenizedList tokens;
    int current = 0;

    public Parser(Lexer a){
        l = a;
        tokens = l.lex();
    }

    // create a list of variables which can easily be created and getted
    // create a list of functions which can be made with a certain id which refernce an actual function call
    // kind of like:
    // new KeyFunction(functionthathandlesjumpning, "jump");
    // create an AST tree and classes for the such
    public void parse(){

    }

    //finish parse statements for stuff
    public ASTNode parseStatement() throws ParserException{
        if(tokenEqual(Type.ID, "set")) return parseSetStatement();
        throw new ParserException("Unexpected Token at "+peek());
    }

    public ASTNode parseSetStatement() throws ParserException{
        consume(Type.ID, "set");
        String variable = consume(Type.ID).data;
        consume(Type.ID, "to");
        Token valueToken = consume(Type.NUM, Type.FLOAT);
        return new SetNode<>(variable, valueToken.data);
    }

    public Token consume(Type type) throws ParserException{
        if (tokenEqual(type)) return advance();
        throw new ParserException("Expected '" + type + "' but got '" +peek()+"'");
    }
    private Token consume(Type type, String lexeme) throws ParserException{
        if (tokenEqual(type) && peek().data.equals(lexeme)) return advance();
        throw new ParserException("Expected '" + lexeme + "' but got '" + peek().data + "'");
    }
    private Token consume(Type... types) {
        for (Type type : types) {
            if (tokenEqual(type)) return advance();
        }
        throw new RuntimeException("Expected " + Arrays.toString(types) + " but got " + peek().type);
    }
    private Token advance(){
        if (!isAtEnd()) current++;
        return tokens.grab(current-1);
    }
    private boolean tokenEqual(Type type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    private boolean tokenEqual(Type type, String lexeme) {
        if (isAtEnd()) return false;
        return (peek().type == type) && (peek().data.equals(lexeme));
    }

    private boolean isAtEnd(){
        return peek().type == Type.EOF;
    }

    private Token peek() {
        return tokens.grab(current);
    }
}
