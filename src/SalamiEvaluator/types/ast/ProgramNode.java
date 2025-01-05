package SalamiEvaluator.types.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends StatementNode {
    public List<StatementNode> statements = new ArrayList<>();

    public ProgramNode() {
        super(NodeType.PROGRAM);
    }

    public void addStatement(StatementNode s){
        statements.add(s);
    }

    @Override
    public String toString() {
        return "ProgramNode{\n\t" +
                "statements=" + statements +
                "\n}";
    }
}
