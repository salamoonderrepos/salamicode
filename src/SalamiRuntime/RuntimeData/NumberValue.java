package SalamiRuntime.RuntimeData;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumberValue that = (NumberValue) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
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
        } else if ( a instanceof StringValue stringvalue_a){
            try {

                return new NumberValue(Integer.parseInt(stringvalue_a.value));

            } catch (NumberFormatException e) {
                throw new ValueException("Could not properly parse the string value into a numerical literal.");
            }
        }
        throw new ValueException("Cannot parse "+a.type+" into a number value.");
    }
}
