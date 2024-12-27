package SalamiEvaluator.types.ast;

public class IncrementNode extends ASTNode {
    String variable;  // Variable to increment

    public IncrementNode(String variable) {
        this.variable = variable;
    }
}
