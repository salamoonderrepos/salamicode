package SalamiEvaluator.types.ast;

public class IdentifierNode extends ExpressionNode {
    public String value;
    public IdentifierNode(String d){
        super(NodeType.IDENTIFIER);
        value = d;
    }

    @Override
    public String toString() {
        return "IdentifierNode{" +
                "value='" + value + '\'' +
                '}';
    }
}
