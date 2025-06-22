package SalamiPreEvaluator.types.ast;

public class SubroutineEndIndicator extends StatementNode{
    public SubroutineEndIndicator(){
        super(NodeType.SUBROUTINEENDINDICATOR);
    }

    @Override
    public String toString() {
        return "SubroutineEndIndicator{}";
    }
}
