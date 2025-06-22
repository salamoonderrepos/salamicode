package SalamiRuntime.RuntimeData;

/**
 * Thrown when an exception occurs within value processing. Things like type promotion not working within string conversions or something like that.
 */
public class ValueException extends RuntimeException {
    public ValueException(String message) {
        super(message);
    }
}
