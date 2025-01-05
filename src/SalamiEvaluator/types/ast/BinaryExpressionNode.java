package SalamiEvaluator.types.ast;

public class BinaryExpressionNode extends ExpressionNode{
    public ExpressionNode leftExpression;
    public String op;
    public ExpressionNode rightExpression;
    public BinaryExpressionNode(ExpressionNode leftEx, String _op, ExpressionNode rightEx) {
        super(NodeType.BINARYEXPRESSION);
        leftExpression = leftEx;
        op = _op;
        rightExpression = rightEx;
    }

    @Override
    public String toString() {
        return "BinaryExpressionNode{" +
                "leftExpression=" + leftExpression +
                ", op='" + op + '\'' +
                ", rightExpression=" + rightExpression +
                '}';
    }
}
