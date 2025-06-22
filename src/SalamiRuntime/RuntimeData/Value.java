package SalamiRuntime.RuntimeData;


import java.util.HashMap;

/**
 * The base value class.
 */
abstract public class Value {
    public RuntimeType type;
    public HashMap<String, AttributeValue> attributes = new HashMap<>();
    public Value(RuntimeType t){
        type = t;
    }
    public Value(RuntimeType t, HashMap<String, AttributeValue> customattributes){
        type = t;
        attributes = customattributes;
    }
    @Override
    public String toString() {
        return "EmptyValue()";
    }
}
