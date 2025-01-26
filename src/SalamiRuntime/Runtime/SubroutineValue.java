package SalamiRuntime.Runtime;

import SalamiEvaluator.types.ast.ProgramNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This acts as an independent manager for subroutines.
 * Subroutines have their own unique scope, or {@link Environment}. They also store the code to be run
 * using a {@link ProgramNode}.</p>
 * @see Environment
 * @see SalamiRuntime.Interpreter#evaluate_subroutine
 */
public class SubroutineValue extends Value{
    public Environment env;
    public ArrayList<String> parameters;
    public int location;
    public ProgramNode code;
    public SubroutineValue(Environment parent, ArrayList<String> params, int loc, ProgramNode _code) throws ValueException {
        super(RuntimeType.SUBROUTINE);
        env = new Environment(parent);
        parameters = params;
        location = loc;
        code = _code;

    }
    public Environment reset(){
        Environment lastenv = env;
        env = new Environment();
        return lastenv;
    }

    @Override
    public String toString() {
        return "SubroutineValue{" +
                "env=" + env +
                ", parameters=" + parameters +
                ", location=" + location +
                ", code=" + code +
                '}';
    }
}
