package SalamiEvaluator.types.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubroutineDeclarationStatement extends StatementNode{
    public String identifier;
    public ArrayList<String> parameters;
    public ProgramNode code;
    public SubroutineDeclarationStatement(String id, ArrayList<String> params, ProgramNode c){
        super(NodeType.SUBROUTINEDECLARATIONSTATEMENT);
        parameters = params;
        identifier = id;
        code = c;

    }

    @Override
    public String toString() {
        return "SubroutineDeclarationStatement{" +
                "identifier='" + identifier + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
