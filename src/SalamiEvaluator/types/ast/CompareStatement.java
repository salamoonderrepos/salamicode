package SalamiEvaluator.types.ast;

public class CompareStatement extends StatementNode{
    public ExpressionNode comp;
    public StatementNode execute;
    public CompareStatement(ExpressionNode val, StatementNode ex){
        super(NodeType.COMPARESTATEMENT);
        comp = val;
        execute = ex;

    }


    @Override
    public String toString() {
        return "CompareStatement{" +
                "comp=" + comp +
                ", execute=" + execute +
                '}';
    }
}
