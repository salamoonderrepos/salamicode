package SalamiRuntime.RuntimeData;

import java.util.HashMap;
import java.util.Objects;

/**
 * String values store text.
 */
public class StringValue extends Value{
    public String value;
    public StringValue(String v){
        super(RuntimeType.STRING);
        HashMap<String, AttributeValue> temp = new HashMap<>();
        temp.put("length", new AttributeValue(((params, logger) -> {
            StringValue parentValue = StringValue.parseStringValue(params.get(0));
            return new NumberValue(parentValue.value.length());

        } ), this));
        attributes = temp;
        value = v;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
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
        } else if (a instanceof ArrayValue arrayvalue_a){
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            for (Value v : arrayvalue_a.values){
                sb.append(parseStringValue(v).value);
                sb.append(" ");
            }
            sb.append('}');
            return new StringValue(sb.toString());
        }else if (a instanceof VoidValue voidValue_a){
            return new StringValue("VOID");
        }
        throw new ValueException(a+" <-- cannot be converted to a string value.");
    }
}
