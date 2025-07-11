package SalamiRuntime.RuntimeData;

import Helper.Logger.Logger;
import SalamiRuntime.RuntimeData.Method.Function;

import java.util.List;

/**
 * <p>Manages attributes of values. Referencing using the {@link SalamiPreEvaluator.types.ast.IndexExpressionNode}.
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

    public Value getValue(Logger logger, String location) throws ValueException{
        return func.execute(List.of(parent), logger, location);
    }

}
