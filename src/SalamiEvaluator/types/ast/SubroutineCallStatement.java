package SalamiEvaluator.types.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubroutineCallStatement extends ExpressionNode{
    public String identifier;
    public ArrayList<ExpressionNode> parameters;
    public SubroutineCallStatement(String id, ArrayList<ExpressionNode> params){
        super(NodeType.CALLSTATEMENT);
        identifier = id;
        parameters = params;

    }

    @Override
    public String toString() {
        return "SubroutineCallStatement{" +
                "identifier='" + identifier + '\'' +
                ", arguments=" + parameters +
                '}';
    }
}
