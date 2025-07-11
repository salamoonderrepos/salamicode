package SalamiPreEvaluator.types.ast;

public class UnaryExpressionNode extends ExpressionNode{
    public String op;
    public ExpressionNode rightExpression;
    public boolean logical = false;
    public UnaryExpressionNode(String _op, ExpressionNode rightEx, boolean log) {
        super(NodeType.UNARYEXPRESSION);
        op = _op;
        rightExpression = rightEx;
        logical = log;
    }

    @Override
    public String toString() {
        return "UnaryExpressionNode{" +
                "op='" + op + '\'' +
                ", rightExpression=" + rightExpression +
                ", logical=" + logical +
                '}';
    }
}
