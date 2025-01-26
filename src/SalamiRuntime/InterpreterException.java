package SalamiRuntime;

/**
 * <p>
 *     An error which occurs when the interpreter fails to evaluate something.
 * </p>
 * @see SalamiEvaluator.ParserException
 * @see SalamiEvaluator.LexerException
 */
public class InterpreterException extends Throwable {
    public InterpreterException(String s) {
        super(s);
    }
}
