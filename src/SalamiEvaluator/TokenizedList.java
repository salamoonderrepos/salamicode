package SalamiEvaluator;

import java.util.ArrayList;
import java.util.List;

public class TokenizedList {
    List<Token> tokens = new ArrayList<>();
    public TokenizedList(){
    }

    public boolean addToken(Token t){
        try {
            tokens.add(t);
            return true;
        } catch (Error e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public void get(){
        for (Token t : tokens){
            System.out.println(t.toString());
        }
    }

    public Token grab(int ind){
        return tokens.get(ind);
    }
}
