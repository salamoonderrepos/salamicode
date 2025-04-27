package SalamiRuntime.Runtime;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoidValue voidValue = (VoidValue) o;
        return Objects.equals(value, voidValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
