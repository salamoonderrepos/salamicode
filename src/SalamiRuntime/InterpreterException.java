package SalamiRuntime;

import Structure.BaseException;

/**
 * <p>
 *     An error which occurs when the interpreter fails to evaluate something.
 * </p>
 * @see SalamiPreEvaluator.ParserException
 * @see SalamiPreEvaluator.LexerException
 */
public class InterpreterException extends BaseException {
    public InterpreterException(String s, int[] loc) {
        super(s, loc);
    }
    public InterpreterException(String s) {
        super(s);
    }
}
