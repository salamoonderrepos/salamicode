package SalamiRuntime.Runtime;

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
        }
        throw new ValueException("Cannot parse a floating value that isn't a FloatingValue or a NumberValue.");
    }

    public void add(float num){
        value += num;
    }

}
