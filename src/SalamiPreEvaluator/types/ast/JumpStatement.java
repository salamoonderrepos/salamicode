package SalamiPreEvaluator.types.ast;

public class JumpStatement extends StatementNode{
    public String identifier;
    public JumpStatement(String id){
        super(NodeType.JUMPSTATEMENT);
        identifier = id;

    }

    @Override
    public String toString() {
        return "JumpStatement{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
