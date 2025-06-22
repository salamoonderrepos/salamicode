package SalamiRuntime;

/**
 * <p>
 *     An error which occurs when the interpreter fails to evaluate something.
 * </p>
 * @see SalamiPreEvaluator.ParserException
 * @see SalamiPreEvaluator.LexerException
 */
public class InterpreterException extends Throwable {
    public InterpreterException(String s) {
        super(s);
    }
}
