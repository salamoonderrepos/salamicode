package SalamiEvaluator.types.ast;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FloatingLiteralNode that = (FloatingLiteralNode) o;
        return Float.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
