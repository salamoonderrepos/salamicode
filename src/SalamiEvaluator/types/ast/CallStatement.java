package SalamiEvaluator.types.ast;

import java.util.ArrayList;

public class CallStatement extends ExpressionNode{
    public String identifier;
    public ArrayList<ExpressionNode> parameters;
    public CallStatement(String id, ArrayList<ExpressionNode> params){
        super(NodeType.CALLSTATEMENT);
        identifier = id;
        parameters = params;

    }

    @Override
    public String toString() {
        return "CallStatement{" +
                "identifier='" + identifier + '\'' +
                ", arguments=" + parameters +
                '}';
    }
}
