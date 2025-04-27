package SalamiRuntime.Runtime;

import java.util.Objects;

/**
 * A boolean runtime value. A <code>true</code> or <code>false</code> value.
 */
public class BooleanValue extends Value{
    public boolean value;
    public BooleanValue(boolean v){
        super(RuntimeType.BOOLEAN);
        value = v;
    }

    @Override
    public String toString() {
        return "BooleanValue ("+value+')';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BooleanValue that = (BooleanValue) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
