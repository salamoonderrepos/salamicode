package SalamiPreEvaluator.types.ast;

public class ExpressionIncrementStatement extends StatementNode{
    public String identifier;
    public ExpressionIncrementStatement(String id){
        super(NodeType.EXPRESSIONINCREMENTSTATEMENT);
        identifier = id;

    }

    @Override
    public String toString() {
        return "ExpressionIncrementStatement{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
