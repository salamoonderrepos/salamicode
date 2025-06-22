package SalamiRuntime.RuntimeData;

import java.util.Objects;

/**
 * A value that holds floating point information. Anything that has a decimal is a floating point number.
 */
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

    /**Parses a value into a <code>FloatingValue</code>.
     * @param a The value to be parsed.
     * @return the new floating point value.
     * @throws ValueException If the type of value can't be parsed into a floating point.
     */
    public static FloatingValue parseFloatingValue(Value a) throws ValueException{
        if (a instanceof NumberValue numbervalue_a) {
            return new FloatingValue((float) numbervalue_a.value);
        } else if (a instanceof FloatingValue floatingvalue_a){
            return new FloatingValue(floatingvalue_a.value);
        } else if ( a instanceof StringValue stringvalue_a){
            try {

                return new FloatingValue(Float.parseFloat(stringvalue_a.value));

            } catch (NumberFormatException e) {
                throw new ValueException("Could not properly parse the string value into a floating point literal.");
            }
        }
        throw new ValueException("Cannot parse "+a+" into a number value.");
    }

    public void add(float num){
        value += num;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FloatingValue that = (FloatingValue) o;
        return Float.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
