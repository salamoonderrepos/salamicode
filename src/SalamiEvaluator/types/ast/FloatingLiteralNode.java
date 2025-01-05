package SalamiEvaluator.types.ast;

public class FloatingLiteralNode extends ExpressionNode {
    public float value;
    public FloatingLiteralNode(float d){
        super(NodeType.FLOATINGPOINTLITERAL);
        value = d;
    }

    @Override
    public String toString() {
        return "FloatingLiteralNode{" +
                "value=" + value +
                '}';
    }
}
