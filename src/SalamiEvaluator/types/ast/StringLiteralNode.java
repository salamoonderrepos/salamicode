package SalamiEvaluator.types.ast;

public class StringLiteralNode extends ExpressionNode {
    public String value;
    public StringLiteralNode(String d){
        super(NodeType.STRINGLITERAL);
        value = d;
    }

    @Override
    public String toString() {
        return "StringLiteralNode{" +
                "value=\"" + value +
                "\"}";
    }
}
