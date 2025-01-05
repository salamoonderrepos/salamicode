package SalamiRuntime.Runtime;

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
}
