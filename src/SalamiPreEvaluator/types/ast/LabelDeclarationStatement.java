package SalamiPreEvaluator.types.ast;

public class LabelDeclarationStatement extends StatementNode{
    public String identifier;
    public LabelDeclarationStatement(String id){
        super(NodeType.LABELDECLARATIONSTATEMENT);
        identifier = id;

    }

    @Override
    public String toString() {
        return "LabelDeclarationStatement{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
