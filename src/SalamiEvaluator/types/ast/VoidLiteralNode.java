package SalamiEvaluator.types.ast;

public class VoidLiteralNode extends ExpressionNode {
    String value;
    public VoidLiteralNode(){
        super(NodeType.VOIDLITERAL);
        value = "void";
    }

    @Override
    public String toString() {
        return "VoidLiteralNode{}";
    }
}
