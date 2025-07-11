package SalamiPreEvaluator.types.ast;

public class PortStatement extends StatementNode {
    public String value;
    public PortStatement(String d){
        super(NodeType.PORTSTATEMENT);
        value = d;
    }

    @Override
    public String toString() {
        return "PortStatement{" +
                "value=" + value +
                "}";
    }
}
