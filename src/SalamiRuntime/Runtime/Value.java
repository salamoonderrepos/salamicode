package SalamiRuntime.Runtime;


/**
 * The base value class.
 */
abstract public class Value {
    public RuntimeType type;
    public Value(RuntimeType t){
        type = t;
    }
    @Override
    public String toString() {
        return "EmptyValue()";
    }
}
