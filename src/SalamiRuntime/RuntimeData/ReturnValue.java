package SalamiRuntime.RuntimeData;

/**
 * A boolean runtime value. A <code>true</code> or <code>false</code> value.
 */
public class ReturnValue extends Value{
    public Value value;
    public ReturnValue(Value v){
        super(RuntimeType.RETURNVALUE);
        value = v;
    }

    @Override
    public String toString() {
        return "ReturnValue ("+value+')';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReturnValue that = (ReturnValue) o;
        return value == that.value;
    }
}
