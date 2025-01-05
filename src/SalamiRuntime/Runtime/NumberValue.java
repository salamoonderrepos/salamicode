package SalamiRuntime.Runtime;

public class NumberValue extends Value{
    public double value;
    public NumberValue(double v){
        super(RuntimeType.NUMBER);
        value = v;
    }

    @Override
    public String toString() {
        return "NumberValue ("+value+')';
    }
}
