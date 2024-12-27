package SalamiEvaluator.types.ast;

public class SetNode<T> extends ASTNode {
    String variable;
    T value;
    public SetNode(String v, T va){
        variable = v;
        value = va;
    }
}
