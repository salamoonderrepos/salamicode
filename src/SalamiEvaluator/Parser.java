package SalamiEvaluator;

public class Parser {
    Lexer l;
    TokenizedList tokens;
    public Parser(Lexer a){
        l = a;
        tokens = l.lex();
    }
}
