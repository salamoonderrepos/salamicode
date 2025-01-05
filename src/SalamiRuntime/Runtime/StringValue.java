package SalamiRuntime.Runtime;

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

}
