package SalamiEvaluator.types.ast;

public class ConstantNode extends ASTNode {
    int value;
    public ConstantNode(int v){
        value = v;
    }
}
