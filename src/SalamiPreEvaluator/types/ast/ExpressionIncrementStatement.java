package SalamiPreEvaluator.types.ast;

public class ExpressionIncrementStatement extends ExpressionNode{
    public String identifier;
    public boolean incrementBeforeUsage;

    public ExpressionIncrementStatement(String id, boolean _incrementBeforeUsage){
        super(NodeType.EXPRESSIONINCREMENTSTATEMENT);
        identifier = id;
        incrementBeforeUsage = _incrementBeforeUsage;

    }
    @Override
    public String toString() {
        return "ExpressionIncrementStatement{" +
                "identifier='" + identifier + '\'' +
                ", incrementBeforeUsage=" + incrementBeforeUsage +
                '}';
    }
}
