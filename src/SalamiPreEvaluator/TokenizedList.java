package SalamiPreEvaluator;

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
    public Token getLastToken(){
        return grab(tokens.size()-1);
    }
    public int[] getEndLocation(){
        return getLastToken().loc;
    }

    public void addTokens(TokenizedList t){
        for (Token token : t.tokens){
            addToken(token);
        }
    }

    public void clear(){
        tokens.clear();
    }

    public Token grab(int ind){
        return tokens.get(ind);
    }

    @Override
    public String toString() {
        return "TokenizedList{" +
                "tokens=" + tokens +
                '}';
    }
}
