package SalamiEvaluator;

import SalamiEvaluator.types.Type;

public class Token{
    Type type;
    String data;
    public Token(Type t, String d){
        type = t;
        data = d;
    }
    @Override
    public String toString() {
        return "[" + type + ": " + data + "]";
    }
    public String getValue () {
        return data;
    }
    public Type getType (){
        return type;
    }
}
