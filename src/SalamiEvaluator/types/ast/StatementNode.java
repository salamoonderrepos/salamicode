package SalamiEvaluator.types.ast;

public class StatementNode extends ASTNode{
    public NodeType type;
    public StatementNode(NodeType d){
        type = d;
    }

    @Override
    public String toString() {
        return "StatementNode{" +
                "type=" + type +
                '}';
    }
}
