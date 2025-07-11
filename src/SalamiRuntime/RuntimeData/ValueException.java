package SalamiRuntime.RuntimeData;

import SalamiRuntime.InterpreterException;

/**
 * Thrown when an exception occurs within value processing. Things like type promotion not working within string conversions or something like that.
 */
public class ValueException extends InterpreterException {
    public ValueException(String message, int[] loc) {
        super(message,loc);
    }
    public ValueException(String message) {
        super(message);
    }
}
