package SalamiPreEvaluator;

import Structure.BaseException;

public class ParserException extends BaseException {
    public ParserException(String s, int[] lff) {
        super(s,lff);
    }
}
