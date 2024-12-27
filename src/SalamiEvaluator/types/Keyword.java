package SalamiEvaluator.types;

public class Keyword {
    String keywordlit;
    Function f;
    public Keyword(String s, Function _f){
        keywordlit = s;
        f = _f;
    }
    public boolean isKey(String foo){
        return foo.equals(keywordlit);
    }

    public void run(){
        f.execute();
    }
}
