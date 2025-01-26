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
}
