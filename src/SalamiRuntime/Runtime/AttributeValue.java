package SalamiRuntime.Runtime;

import Helper.Logger.Logger;
import SalamiRuntime.Runtime.Method.Function;

import java.util.List;

/**
 * <p>Manages attributes of values. Referencing using the {@link SalamiEvaluator.types.ast.IndexExpressionNode}.
 * Call `getValue` to execute the internal function which returns a value.
 * </p>
 */
public class AttributeValue extends Value {
    public Function func;
    public Value parent;
    public AttributeValue(Function _func, Value _parent) throws ValueException {
        super(RuntimeType.METHOD);
        func = _func;
        parent = _parent;
    }

    public Value getValue(Logger logger) throws ValueException{
        return func.execute(List.of(parent), logger);
    }

}
