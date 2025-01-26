package SalamiRuntime.Runtime;

/**
 * An empty value. Means nothing. Useful for placeholders.
 */
public class VoidValue extends Value{
    String value = "void";
    public VoidValue(){
        super(RuntimeType.VOID);
    }

    @Override
    public String toString() {
        return "VoidValue ()";
    }
}
