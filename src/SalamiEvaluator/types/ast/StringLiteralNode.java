package SalamiEvaluator.types.ast;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteralNode that = (StringLiteralNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
