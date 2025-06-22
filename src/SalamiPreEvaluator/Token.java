package SalamiPreEvaluator;

import SalamiPreEvaluator.types.TokenType;

public class Token{
    TokenType type;
    String data;
    // {line, column}
    int[] loc = new int[2];
    public Token(TokenType t, String d){
        type = t;
        data = d;
    }
    public Token(TokenType t, String d, int[] loc){
        type = t;
        data = d;
        loc = loc;
    }
    @Override
    public String toString() {
        return "[" + type + ": " + data + "]";
    }
    public String getValue () {
        return data;
    }
    public TokenType getType (){
        return type;
    }
}
