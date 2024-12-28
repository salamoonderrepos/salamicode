package SalamiRuntime.Runtime;

public class VoidValue extends Value{
    String value = "void";
    public VoidValue(){}

    @Override
    public String toString() {
        return "VoidValue ()";
    }
}
