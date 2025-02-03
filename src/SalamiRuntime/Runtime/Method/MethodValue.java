package SalamiRuntime.Runtime.Method;

import Logger.Logger;
import SalamiRuntime.Runtime.RuntimeType;
import SalamiRuntime.Runtime.Value;
import SalamiRuntime.Runtime.ValueException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This acts as a manager for methods which are non-user written subroutines that run java code instead of
 * a ProgramNode.</p>
 */
public class MethodValue extends Value {

    public List<Class<?>> parameters;
    public Function func;
    public Function validityFunc = (params, logger) -> null;
    public MethodValue(List<Class<?>> params, Function _func) throws ValueException {
        super(RuntimeType.METHOD);
        parameters = params;
        func = _func;
    }

    public Value run(List<Value> args, Logger logger) throws ValueException{
        for (int i = 0; i < args.size(); i++) {
            if (parameters.get(i)!=args.get(i).getClass() && !(parameters.get(i).isAssignableFrom(args.get(i).getClass()))) {
                throw new ValueException("Parameter at index " + i + " is not of expected type " + parameters.get(i).getName());
            }
        }
        validityFunc.execute(args, logger);
        return func.execute(args, logger);
    }

}
