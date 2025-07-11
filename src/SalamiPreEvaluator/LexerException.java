package SalamiPreEvaluator;

import Structure.BaseException;

public class LexerException extends BaseException {
    public LexerException(String s, int[] lff) {
        super(s,lff);
    }
}
