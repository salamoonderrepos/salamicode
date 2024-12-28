package SalamiRuntime.Runtime;

public class NumberValue extends Value{
    public double value;
    public NumberValue(double v){
        value = v;
    }

    @Override
    public String toString() {
        return "NumberValue ("+value+')';
    }
}
