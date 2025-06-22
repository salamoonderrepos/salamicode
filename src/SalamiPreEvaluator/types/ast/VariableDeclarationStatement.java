package SalamiPreEvaluator.types.ast;

public class VariableDeclarationStatement extends StatementNode{
    public boolean isFinal;
    public String identifier;
    public ExpressionNode value;
    public VariableDeclarationStatement(String id, ExpressionNode val, boolean fin){
        super(NodeType.VARIABLEDECLARATIONSTATEMENT);
        isFinal = fin;
        identifier = id;
        value = val;

    }

    @Override
    public String toString() {
        return "VariableDeclarationStatement{" +
                "isFinal=" + isFinal +
                ", identifier='" + identifier + '\'' +
                ", value=" + value +
                '}';
    }
}
