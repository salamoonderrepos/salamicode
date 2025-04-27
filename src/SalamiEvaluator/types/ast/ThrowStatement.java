package SalamiEvaluator.types.ast;

public class ThrowStatement extends StatementNode {
    public ExpressionNode value;
    public ThrowStatement(ExpressionNode d){
        super(NodeType.THROWSTATEMENT);
        value = d;
    }

    @Override
    public String toString() {
        return "ThrowStatement{" +
                "message=" + value +
                "}";
    }
}
