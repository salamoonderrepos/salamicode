package SalamiRuntime.RuntimeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A value that holds multiple other values.
 */
public class ArrayValue extends Value{
    public List<Value> values;

    public ArrayValue(List<Value> v){
        super(RuntimeType.ARRAY);
        HashMap<String, AttributeValue> temp = new HashMap<>();
        temp.put("length", new AttributeValue(((params, logger) -> {
            ArrayValue parentValue = ArrayValue.parseArrayValue(params.get(0));
            return new NumberValue(parentValue.values.size());

        } ), this));
        attributes = temp;
        values = v;
    }

    @Override
    public String toString() {
        return "ArrayValue{" +
                "values=" + values +
                '}';
    }
    public Value getArrayValue(int index) throws ValueException, IndexOutOfBoundsException{
        return values.get(index);
    }
    public static ArrayValue parseArrayValue(Value a) throws ValueException{
        if (a instanceof NumberValue numbervalue_a) {
            List<Value> tempArray = new ArrayList<>();
            tempArray.add(numbervalue_a);
            return new ArrayValue(tempArray);

        } else if (a instanceof FloatingValue floatingvalue_a){
            List<Value> tempArray = new ArrayList<>();
            tempArray.add(floatingvalue_a);
            return new ArrayValue(tempArray);

        } else if (a instanceof StringValue stringvalue_a){
            List<Value> tempArray = new ArrayList<>();
            tempArray.add(stringvalue_a);
            return new ArrayValue(tempArray);

        } else if (a instanceof BooleanValue boolvalue_a){
            List<Value> tempArray = new ArrayList<>();
            tempArray.add(boolvalue_a);
            return new ArrayValue(tempArray);

        }else if (a instanceof VoidValue voidValue_a){
            List<Value> tempArray = new ArrayList<>();
            return new ArrayValue(tempArray);
        }else if (a instanceof ArrayValue arrayValue_a){
            return new ArrayValue(arrayValue_a.values);
        }
        throw new ValueException(a+" <-- cannot be converted to a array value.");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArrayValue that = (ArrayValue) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
