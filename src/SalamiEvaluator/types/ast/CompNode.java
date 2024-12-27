package SalamiEvaluator.types.ast;

public class CompNode extends ASTNode {
    ASTNode leftOperant;  // Variable being compared
    String operator;  // Comparison operator (e.g., "<=")
    ASTNode rightOperant;        // Value being compared against
    String jumpLabel; // Label to jump to if condition is true

    public CompNode(ASTNode variable, String operator, ASTNode value, String jumpLabel) {
        this.leftOperant = variable;
        this.operator = operator;
        this.rightOperant = value;
        this.jumpLabel = jumpLabel;
    }
}
