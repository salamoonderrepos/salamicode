package SalamiRuntime.Runtime;

/**
 * String values store text.
 */
public class StringValue extends Value{
    public String value;
    public StringValue(String v){
        super(RuntimeType.STRING);
        value = v;
    }

    @Override
    public String toString() {
        return "StringValue ("+value+')';
    }
    public static StringValue parseStringValue(Value a) throws ValueException{
        if (a instanceof NumberValue numbervalue_a) {
            if (String.valueOf(numbervalue_a.value).contains("E")){
                return new StringValue(String.valueOf(numbervalue_a.value));
            }
            return new StringValue(String.valueOf((int) numbervalue_a.value));
        } else if (a instanceof FloatingValue floatingvalue_a){
            return new StringValue(String.valueOf(floatingvalue_a.value));
        } else if (a instanceof StringValue stringvalue_a){
            return new StringValue(stringvalue_a.value);
        } else if (a instanceof BooleanValue boolvalue_a){
            return new StringValue(String.valueOf(boolvalue_a.value));
        }else if (a instanceof VoidValue voidValue_a){
            return new StringValue("VOID");
        }
        throw new ValueException(a+" <-- cannot be converted to a string value.");
    }
}
