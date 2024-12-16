public class Token<T> {
    public T data;
    public Token(T d){
        data = d;
    }
    public boolean isInt(){
        return data instanceof Integer;
    }
    public boolean dataEquals(Object thing){
        return data.equals(thing);
    }

    public T getValue(){
        return data;
    }
}
