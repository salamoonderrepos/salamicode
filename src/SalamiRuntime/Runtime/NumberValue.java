package SalamiRuntime.Runtime;

import SalamiRuntime.Interpreter;

/**
 * A value which holds numbers. Techincally it can store decimals, but all decimals should be converted to <code>FloatingValue</code>s.
 * @see FloatingValue
 */
public class NumberValue extends Value{
    public double value;
    public NumberValue(double v) throws ValueException{
        super(RuntimeType.NUMBER);
        value = v;
    }

    @Override
    public String toString() {
        return "NumberValue ("+value+')';
    }

    public void add(double num){
        value += num;
    }

    public static NumberValue parseNumberValue(Value a) throws ValueException{
        if (a instanceof NumberValue numbervalue_a) {
            return new NumberValue(numbervalue_a.value);
        } else if (a instanceof FloatingValue floatingvalue_a){
            if (Math.floor(floatingvalue_a.value)==floatingvalue_a.value) {
                return new NumberValue(floatingvalue_a.value);
            }
            throw new ValueException("Cannot parse floating value into number because it contains decimals.");
        }
        throw new ValueException("Cannot parse "+a+" into a number value.");
    }
}
