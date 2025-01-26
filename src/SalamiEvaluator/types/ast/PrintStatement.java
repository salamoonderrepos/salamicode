package SalamiEvaluator.types.ast;

import SalamiRuntime.Runtime.Value;

public class PrintStatement extends StatementNode {
    public ExpressionNode value;
    public PrintStatement(ExpressionNode d){
        super(NodeType.PRINTSTATEMENT);
        value = d;
    }

    @Override
    public String toString() {
        return "PrintStatement{" +
                "value=\"" + value +
                "\"}";
    }
}
