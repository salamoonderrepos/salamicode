package SalamiPreEvaluator.types.ast;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoidLiteralNode that = (VoidLiteralNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
