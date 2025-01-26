package SalamiEvaluator.types.ast;

import java.util.ArrayList;
import java.util.List;

public class ReturnStatement extends StatementNode {
    public StatementNode statement;

    public ReturnStatement(StatementNode state) {
        super(NodeType.RETURNSTATEMENT);
        statement = state;
    }

    @Override
    public String toString() {
        return "ReturnStatement{" +
                "statement=" + statement +
                '}';
    }
}
