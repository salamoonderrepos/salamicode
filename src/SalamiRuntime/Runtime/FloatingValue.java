package SalamiRuntime.Runtime;

public class FloatingValue extends Value{
    public float value;
    public FloatingValue(float v){
        super(RuntimeType.FLOAT);
        value = v;
    }

    @Override
    public String toString() {
        return "FloatingValue ("+value+')';
    }

    public static FloatingValue parseFloatingValue(Value a) throws ValueException{
        if (a instanceof NumberValue numbervalue_a) {
            return new FloatingValue((float) numbervalue_a.value);
        } else if (a instanceof FloatingValue floatingvalue_a){
            return new FloatingValue(floatingvalue_a.value);
        }
        throw new ValueException("Cannot parse a floating value that isn't a FloatingValue or a NumberValue.");
    }

}
