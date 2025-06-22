package SalamiPreEvaluator.types.ast;

import java.util.List;
import java.util.Objects;

public class ArrayLiteralNode extends ExpressionNode {
    public List<ExpressionNode> values;
    public ArrayLiteralNode(List<ExpressionNode> d){
        super(NodeType.ARRAYLITERAL);
        values = d;
    }

    @Override
    public String toString() {
        return "ArrayLiteralNode{" +
                "values=" + values +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArrayLiteralNode that = (ArrayLiteralNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }
}
