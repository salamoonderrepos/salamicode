package SalamiRuntime;


import SalamiEvaluator.types.ast.ExpressionNode;
import SalamiEvaluator.types.ast.NumericalLiteralNode;
import SalamiEvaluator.types.ast.StatementNode;
import SalamiRuntime.Runtime.NumberValue;
import SalamiRuntime.Runtime.Value;
import SalamiRuntime.Runtime.VoidValue;

public class Interpreter {
    public Interpreter(){}

    public static Value evaluate(ExpressionNode s){
        switch (s.type){

            case NUMERICALLITERAL:
                NumericalLiteralNode numericNode = (NumericalLiteralNode) s; // tell java that we for sure have a numerical literal node here
                return new NumberValue(numericNode.value); // that way its happy when we ask it for the value
            default: return new VoidValue();
        }
    }
}
