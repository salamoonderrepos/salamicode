package SalamiEvaluator.types.ast;

public class StatementNode extends ASTNode{
    public NodeType type;
    public StatementNode(NodeType d){
        type = d;
    }
    public NodeType getNodeType() {return type;}

    @Override
    public String toString() {
        return "StatementNode{" +
                "type=" + type +
                '}';
    }
}
