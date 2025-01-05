package SalamiRuntime;


import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Runtime.*;

public class Interpreter {
    public Interpreter(){}
    public static final Environment initialize(){
        Environment env = new Environment();
        env.declareVariable("pi", new FloatingValue(3.14159F));
        env.declareVariable("true", new BooleanValue(true));
        env.declareVariable("false", new BooleanValue(false));
        env.declareVariable("thirteen", new NumberValue(13));
        env.declareVariable("cookie", new NumberValue(23));
        return env;
    }
    public static Value evaluate(StatementNode s, Environment environment) throws InterpreterException, ValueException{
        switch (s.type){
            case PROGRAM:
                ProgramNode p = (ProgramNode) s;
                return evaluate_program(p, environment);
            case VARIABLEDECLARATIONSTATEMENT:
                VariableDeclarationStatement vardec = (VariableDeclarationStatement) s;
                return evaluate_set_statement(vardec, environment);
            case IDENTIFIER:
                IdentifierNode identifier_node = (IdentifierNode) s;
                return evaluate_identifier(identifier_node, environment);
            case STRINGLITERAL:
                StringLiteralNode stringLiteralNode = (StringLiteralNode) s;
                return new StringValue(stringLiteralNode.value);
            case NUMERICALLITERAL:
                NumericalLiteralNode numericNode = (NumericalLiteralNode) s; // tell java that we for sure have a numerical literal node here
                return new NumberValue(numericNode.value); // that way its happy when we ask it for the value
            case FLOATINGPOINTLITERAL:
                FloatingLiteralNode floatNode = (FloatingLiteralNode) s;
                return new FloatingValue(floatNode.value);
            case BINARYEXPRESSION:
                BinaryExpressionNode binaryNode = (BinaryExpressionNode) s;
                return evaluate_binary_expression(binaryNode, environment);
            case VOIDLITERAL, COMMENT:
                return new VoidValue();
            default: throw new InterpreterException("Node '" +s.type+ "' was not evaluated correctly. Contact the development team.");
        }
    }

    ///-----------------------------
    ///
    ///         STATEMENTS
    ///
    ///-----------------------------

    public static VoidValue evaluate_set_statement(VariableDeclarationStatement declarationNode, Environment env) throws InterpreterException{
        env.declareVariable(declarationNode.identifier, evaluate(declarationNode.value, env));
        return new VoidValue();
    }


    ///-----------------------------
    ///
    ///         EXPRESSIONS
    ///
    ///-----------------------------

    public static Value evaluate_identifier(IdentifierNode node, Environment environment) throws ValueException{
        return environment.lookupVariale(node.value);
    }


    public static Value evaluate_numeric_binary_expression(BinaryExpressionNode binaryNode, Environment environment) throws InterpreterException{

        Value preEvalLeft = evaluate(binaryNode.leftExpression, environment); // pre evaluate the expressions because we dont know what type of value it will be
        Value preEvalRight = evaluate(binaryNode.rightExpression, environment);
        if (preEvalLeft.type == RuntimeType.VOID | preEvalRight.type == RuntimeType.VOID){

            return new VoidValue();
        }
        if (preEvalLeft.type == RuntimeType.FLOAT | preEvalRight.type == RuntimeType.FLOAT){ // check if either are floating values
            return evaluate_floating_binary_expression(preEvalLeft, preEvalRight, binaryNode.op); // if it is then we call the floating binary expression calculator.
        }
        if (preEvalRight.type == RuntimeType.STRING && preEvalLeft.type == RuntimeType.STRING){
            return evaluate_string_binary_expression(preEvalLeft, preEvalRight, binaryNode.op);
        }
        if (preEvalRight.type != preEvalLeft.type){
            throw new InterpreterException("Type promotion is not available for these types. Are you performing operations on different type?");
        }

        NumberValue left = (NumberValue) preEvalLeft; // pre evaluate the expressions because we dont know what type of value it will be
        NumberValue right = (NumberValue) preEvalRight;
        switch (binaryNode.op){
            case "+": return new NumberValue(left.value+right.value);
            case "-": return new NumberValue(left.value-right.value);
            case "*": return new NumberValue(left.value*right.value);
            case "/":
                if (right.value==0){
                    throw new InterpreterException("Division by zero");
                }
                return new NumberValue(left.value/right.value);
            case "%": return new NumberValue(left.value%right.value);
            case "-*": throw new InterpreterException("-* Operator only works on string values.");
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }

    public static Value evaluate_string_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        StringValue left = (StringValue) preEvalLeft;
        StringValue right = (StringValue) preEvalRight;
        int index;
        switch (op){
            case "+": return new StringValue(left.value+right.value);
            case "-":
                // Check if bar is in foo
                index = left.value.indexOf(right.value);
                if (index != -1) {
                    // Remove the first occurrence of bar
                    return new StringValue(left.value.substring(0, index) + left.value.substring(index + right.value.length()));
                }
                // Return foo unchanged if bar is not found
                return new StringValue(left.value);
            case "*": throw new InterpreterException("Cannot use multiplication on string values");
            case "/": throw new InterpreterException("Cannot use division on string values");
            case "%": throw new InterpreterException("Cannot use modulus on string values");
            case "-*":                 // Check if bar is in foo
                return new StringValue(left.value.replace(right.value,""));
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }

    public static Value evaluate_floating_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        FloatingValue left = FloatingValue.parseFloatingValue(preEvalLeft);
        FloatingValue right = FloatingValue.parseFloatingValue(preEvalRight);
        switch (op){
            case "+": return new FloatingValue(left.value+right.value);
            case "-": return new FloatingValue(left.value-right.value);
            case "*": return new FloatingValue(left.value*right.value);
            case "/":
                if (right.value==0){
                    throw new InterpreterException("Division by zero");
                }
                return new FloatingValue(left.value/right.value);
            case "%": throw new InterpreterException("Cannot use modulus on floating point values");
            case "-*": throw new InterpreterException("-* Operator only works on string values.");
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }



    public static Value evaluate_binary_expression(BinaryExpressionNode binaryNode, Environment environment) throws InterpreterException {
        // every binary expression has to evaluate to a number eventually
        // unless its like a boolean or something but dont worry about that just yet ;)
        return evaluate_numeric_binary_expression(binaryNode, environment);


    }

    public static Value evaluate_program(ProgramNode p, Environment environment) throws InterpreterException {
        Value eval = new VoidValue();
        for (StatementNode statement : p.statements){
            eval = evaluate(statement, environment);
        }
        return eval;
    }
}