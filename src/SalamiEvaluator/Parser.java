package SalamiEvaluator;

class ParserException extends Throwable{
    public ParserException(String s){
        super(s);
    }
}

public class Parser {
    Lexer l;
    TokenizedList tokens;
    int pc = 0;

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
}
