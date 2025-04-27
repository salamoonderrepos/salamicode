package SalamiEvaluator.types.ast;

import java.util.Objects;

/**
 *
 */
public class NumericalLiteralNode extends ExpressionNode {
    public double value;
    public NumericalLiteralNode(double d){
        super(NodeType.NUMERICALLITERAL);
        value = d;
    }

    @Override
    public String toString() {
        return "NumericalLiteralNode{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumericalLiteralNode that = (NumericalLiteralNode) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
