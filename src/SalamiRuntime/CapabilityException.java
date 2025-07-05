package SalamiRuntime;

public class CapabilityException extends InterpreterException {
    public CapabilityException(String message, int[] loc) {
        super(message, loc);
    }
    public CapabilityException(String message) {
        super(message);
    }
}
