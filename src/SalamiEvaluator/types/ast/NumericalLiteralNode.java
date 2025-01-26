package SalamiEvaluator.types.ast;

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
}
