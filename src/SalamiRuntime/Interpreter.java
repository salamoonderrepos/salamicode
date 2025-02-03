package SalamiRuntime;


import Logger.Logger;
import SalamiEvaluator.Lexer;
import SalamiEvaluator.Parser;
import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Runtime.*;
import SalamiRuntime.Runtime.Method.MethodValue;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Logger.Timer;


/**
 * <p>
 *     Handles interpreting of an AST structure. Pass in any {@link StatementNode} to the <code>evaluate(StatementNode)</code> method to begin interpreting.
 * </p>
 * @see Parser
 * @see Lexer
 */
public class Interpreter {
    static final Logger programlogger = new Logger("RuntimeProgram");
    static final Logger logger = new Logger("Interpreter");
    static final Scanner reader = new Scanner(System.in);
    public Interpreter(){}





    /**
     * <p>
     *     Evaluates a single statement. A
     * </p>
     * @param s The statement to be evaluated.
     * @param environment The environment which should be modified.
     * @param pc The program counter so we know where we are in the program.
     * @return the correctly evaluated statement.
     * @throws InterpreterException If it encounters a statement which it doesn't have an evaluator function for, it throws an error.
     * @throws ValueException If at any point two values are mismatched, or some other error like that, this gets thrown.
     */
    public static Value evaluate(StatementNode s, Environment environment, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException{
        switch (s.type){
            case PROGRAM:
                ProgramNode p = (ProgramNode) s;
                return evaluate_program(p, environment, pc, true);
            case RETURNSTATEMENT:
                ReturnStatement ret = (ReturnStatement) s;
                return evaluate_return_statement(ret, environment, pc, program);
            case SUBROUTINEDECLARATIONSTATEMENT:
                return new VoidValue();
            // DONT HANDLE DECLARATION STATEMENTS BECAUSE THEY SHOULD ALREADY BE HANDLES
            case CALLSTATEMENT:
                CallStatement call = (CallStatement) s;
                return evaluate_call_statement(call, environment, pc, program);
            case PRINTSTATEMENT:
                PrintStatement printstat = (PrintStatement) s;
                return evaluate_print_statement(printstat, environment, pc, program);
            case JUMPSTATEMENT:
                JumpStatement jumpstat = (JumpStatement) s;
                return evaluate_jump_statement(jumpstat, environment, pc);
            case LABELDECLARATIONSTATEMENT:
                LabelDeclarationStatement labdec = (LabelDeclarationStatement) s;
                return evaluate_label_statement(labdec, environment, pc);
            case VARIABLEDECLARATIONSTATEMENT:
                VariableDeclarationStatement vardec = (VariableDeclarationStatement) s;
                return evaluate_set_statement(vardec, environment, pc, program);
            case EXPRESSIONINCREMENTSTATEMENT:
                ExpressionIncrementStatement expinc = (ExpressionIncrementStatement) s;
                return evaluate_increment_statement(expinc, environment, pc);
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
                return evaluate_binary_expression(binaryNode, environment, pc, program);
            case UNARYEXPRESSION:
                UnaryExpressionNode unaryNode = (UnaryExpressionNode) s;
                return evaluate_unary_expression(unaryNode, environment, pc, program);
            case COMPARESTATEMENT:
                CompareStatement compNode = (CompareStatement) s;
                return evaluate_compare_statement(compNode, environment, pc, program);
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

    public static Value evaluate_call_statement(CallStatement callStatement, Environment env, ProgramCounter pc, ProgramNode programNode) throws InterpreterException{
        if (env.hasMethod(callStatement.identifier)){
            return evaluate_method_call_statement(callStatement, env, pc, programNode);
        }
        return evaluate_subroutine_call_statement(callStatement, env, pc, programNode);
    }

    public static Value evaluate_method_call_statement(CallStatement methodCallStatement, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        MethodValue method = env.lookupMethod(methodCallStatement.identifier);
        if (methodCallStatement.parameters.size() != method.parameters.size()){
            throw new InterpreterException("Parameter mismatch with method: "+methodCallStatement.identifier);
        }
        List<Value> passins = new java.util.ArrayList<>(List.of());
        for (int i = 0; i<methodCallStatement.parameters.size(); i++){
            passins.add(evaluate(methodCallStatement.parameters.get(i), env, pc, program));
        }
        return method.run(passins, programlogger);


    }

    /**
     * Evaluates jump statements, which set the program counter to the label passed in. <br>
     * <code>jump loopName</code>
     * @param jumpNode The <code>JumpStatement</code> AST node to be evaluated.
     * @param env The environment to look up the label to jump too.
     * @param pc The program counter to be set so it can actually jump.
     * @return a {@link VoidValue}.
     */
    public static VoidValue evaluate_jump_statement(JumpStatement jumpNode, Environment env, ProgramCounter pc){
        pc.set(env.lookupLabel(jumpNode.identifier));
        return new VoidValue();
    }


    public static SubroutineValue evaluate_subroutine_declaration_statement(SubroutineDeclarationStatement subroutineDeclarationStatement, Environment env, ProgramCounter pc){
        SubroutineValue subroutine = env.declareSubroutine(subroutineDeclarationStatement.identifier,pc.get(),subroutineDeclarationStatement.parameters, subroutineDeclarationStatement.code);
        return subroutine;
    }
    public static Value evaluate_return_statement(ReturnStatement returnStatement, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        return evaluate(returnStatement.statement, env, pc, program);
    }
    public static Value evaluate_subroutine_call_statement(CallStatement subroutineCallStatement, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        // order of action for a subroutine call statement
        // 1. find the corresponding subroutine in the environment
        // 2. declare the parameters being passed in into the subroutines environment
        // 2.5 find a way to declare labels seperate from subroutines
        // 3. set the program counter to the subroutines location
        // 4. execute each statement until we reach a return statement
        // 5. collapse the subroutine call as the value being returned
        // 6. reset the subroutines environment to a blank slate

        // order of action new
        // 1. find the subroutine in the environment
        // 2. evaluate its code using evaluateprogramnode
        // 3. collapse the subroutine using the return value
        // 4. forget the parameters passed in

        SubroutineValue subroutine = env.lookupSubroutine(subroutineCallStatement.identifier);
        if (subroutineCallStatement.parameters.size() != subroutine.parameters.size()){
            throw new InterpreterException("Parameter mismatch with subroutine: "+subroutineCallStatement.identifier);
        }
        for (int i = 0; i<subroutineCallStatement.parameters.size(); i++){
            Value arguement = evaluate(subroutineCallStatement.parameters.get(i), env, pc, program);
            subroutine.env.declareVariable(subroutine.parameters.get(i), arguement, false);
        }


        Value returnvalue = evaluate_subroutine(subroutine.code, subroutine.env, new ProgramCounter(0));
        for (int i = 0; i<subroutineCallStatement.parameters.size(); i++){
            subroutine.env.forgetVariable(subroutine.parameters.get(i));
        }
        return returnvalue;
    }


    /** Evaluates label declaration statements like: <br>
     * <code>label loopName</code>
     * @param declarationNode The <code>LabelDeclarationStatement</code> AST node to be evaluated.
     * @param env The environment to declare the label to.
     * @param pc The program counter to tell the label where it is in the program.
     * @return a {@link VoidValue}.
     */
    public static VoidValue evaluate_label_statement(LabelDeclarationStatement declarationNode, Environment env, ProgramCounter pc){
        env.declareLabel(declarationNode.identifier, pc.get());
        return new VoidValue();
    }

    /** Evaluates print statements like: <br>
     * <code>print 'Hello, world!</code>
     * @param printNode The <code>PrintStatement</code> AST node to be evaluated.
     * @param env The environment to grab identifiers from.
     * @param pc The program counter. Does not have to be a specific counter, as it will never be used.
     * @return a {@link VoidValue}.
     * @throws InterpreterException In case the evaluation of what needs to be printed goes wrong.
     */
    public static VoidValue evaluate_print_statement(PrintStatement printNode, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException {
        programlogger.log(StringValue.parseStringValue(evaluate(printNode.value, env, pc, program)).value);
        return new VoidValue();
    }

    /** Evaluates variable declaration statements like: <br>
     * <code>set message to 'Hello, world!"</code>
     * @param declarationNode The <code>VariableDeclarationNode</code> AST node to be evaluated.
     * @param env The environment to declare the variable too.
     * @param pc The program counter. Does not have to be a specific counter, as it will never be used.
     * @return a {@link VoidValue}.
     * @throws InterpreterException If what we are defining the variable as can't be evaluated, then it throws an error.
     */
    public static VoidValue evaluate_set_statement(VariableDeclarationStatement declarationNode, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        env.declareVariable(declarationNode.identifier, evaluate(declarationNode.value, env, pc, program), declarationNode.isFinal);
        return new VoidValue();
    }

    /**
     * @param compareNode 
     * @param env
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_compare_statement(CompareStatement compareNode, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        if (evaluate(compareNode.comp, env, pc, program) instanceof BooleanValue bval){
            if (bval.value) {
                evaluate(compareNode.execute, env, pc, program);
                return new BooleanValue(true);
            }
        }
        return new BooleanValue(false);
    }

    /**
     * @param incrementNode
     * @param env
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public static VoidValue evaluate_increment_statement(ExpressionIncrementStatement incrementNode, Environment env, ProgramCounter pc) throws InterpreterException{
        Value variable_value = env.lookupVariale(incrementNode.identifier);
        switch (variable_value.type){
            case NUMBER:
                NumberValue number_value = (NumberValue) variable_value;
                env.assignVariable(incrementNode.identifier, new NumberValue(number_value.value+1));
                return new VoidValue();
            case FLOAT:
                FloatingValue float_value = (FloatingValue) variable_value;
                env.assignVariable(incrementNode.identifier, new FloatingValue(float_value.value+1F));
                return new VoidValue();
            case STRING:
                StringValue stringValue = (StringValue) variable_value;
                env.assignVariable(incrementNode.identifier, new StringValue(stringValue.value + stringValue.value));
                return new VoidValue();
            default: throw new InterpreterException("Increment node applied to types that AREN'T number/float.");
        }
    }


    ///-----------------------------
    ///
    ///         EXPRESSIONS
    ///
    ///-----------------------------

    public static Value evaluate_subroutine(ProgramNode p, Environment environment, ProgramCounter startingpc) throws InterpreterException, ValueException {

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        //Initializer.initialize_program(p, environment, new ProgramCounter(startingpc.get())); // declare labels ahead of time
        //
        while (pc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(pc.get());
            eval = evaluate(statement, environment, pc, p);
            if (statement.type==NodeType.RETURNSTATEMENT){
                return eval;
            }
            pc.increment();
        }
        throw new InterpreterException("Subroutine found EOF before returning.");
    }

    /**
     *
     * @param node
     * @param environment
     * @return
     * @throws ValueException
     */
    public static Value evaluate_identifier(IdentifierNode node, Environment environment) throws ValueException{
        return environment.lookupVariale(node.value);
    }

    /**
     * @param unaryNode
     * @param env
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_unary_expression(UnaryExpressionNode unaryNode, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException {
        Value preevalright = evaluate(unaryNode.rightExpression, env, pc, program);
        switch (unaryNode.op){
            case "!":
                switch (preevalright.type) {
                    case BOOLEAN:
                        BooleanValue v = (BooleanValue) preevalright;
                        return new BooleanValue(!v.value);
                    default:
                        throw new InterpreterException("'!' operator cannot be applied to given types: "+preevalright.type);
                }
            case "-":
                switch (preevalright.type){
                    case FLOAT:
                        FloatingValue f = (FloatingValue) preevalright;
                        return new FloatingValue(-(f.value));
                    case STRING:
                        StringValue str = (StringValue) preevalright;
                        byte[] arr = str.value.getBytes();

                        byte[] res = new byte[arr.length];

                        for (int i = 0; i < arr.length; i++) {
                            res[i] = arr[arr.length - i - 1];
                        }
                        return new StringValue(new String(res));
                    case NUMBER:
                        NumberValue num = (NumberValue) preevalright;
                        return new NumberValue(-(num.value));
                    case VOID:
                        VoidValue vo = (VoidValue) preevalright;
                        return new VoidValue();
                    default:
                        throw new InterpreterException("Cannot negate a "+preevalright.type+" type of value.");
                }
            default: throw new InterpreterException("Unexpected Operator in Unary Expression Node");
        }
    }

    /**
     * @param binaryNode
     * @param env
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_logical_expression(BinaryExpressionNode binaryNode, Environment env, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        Value left = (Value) evaluate(binaryNode.leftExpression, env, pc, program);
        Value right = (Value) evaluate(binaryNode.rightExpression, env, pc, program);
        if (left.type == RuntimeType.VOID | right.type == RuntimeType.VOID){ // if the expression contains a void value then it must be resolved to void

            throw new InterpreterException("Void Value used inside of a logical expression");
        }
        if (left.type == RuntimeType.NUMBER && right.type == RuntimeType.NUMBER){ // if the expression contains a void value then it must be resolved to void

            return evaluate_numeric_logical_expression(left, right, binaryNode.op);
        }
        if (left.type == RuntimeType.FLOAT | right.type == RuntimeType.FLOAT){ // if the expression contains a void value then it must be resolved to void

            return evaluate_floating_logical_expression(left, right, binaryNode.op);
        }
        if (left.type == RuntimeType.STRING | right.type == RuntimeType.STRING){ // if the expression contains a void value then it must be resolved to void

            return evaluate_string_logical_expression(left, right, binaryNode.op);
        }
        if (left.type != right.type){
            throw new InterpreterException("Comparative contains types that can't be directly compaired.");
        }
        return evaluate_boolean_logical_expression(left, right, binaryNode.op);


    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_numeric_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        NumberValue left = (NumberValue) preEvalLeft;
        NumberValue right = (NumberValue) preEvalRight;
        switch (op){
            case "==": return new BooleanValue(left.value == right.value);
            case ">": return new BooleanValue(left.value > right.value);
            case "<": return new BooleanValue(left.value < right.value);
            case ">=": return new BooleanValue(left.value >= right.value);
            case "<=": return new BooleanValue(left.value <= right.value);
            default: throw new InterpreterException("Cannot use "+op+" operator on type Number.");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_floating_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        FloatingValue left = FloatingValue.parseFloatingValue(preEvalLeft);
        FloatingValue right = FloatingValue.parseFloatingValue(preEvalRight);
        switch (op){
            case "==": return new BooleanValue(left.value == right.value);
            case ">": return new BooleanValue(left.value > right.value);
            case "<": return new BooleanValue(left.value < right.value);
            case ">=": return new BooleanValue(left.value >= right.value);
            case "<=": return new BooleanValue(left.value <= right.value);
            default: throw new InterpreterException("Cannot use "+op+" operator on type Float.");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_string_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        StringValue left = StringValue.parseStringValue(preEvalLeft);
        StringValue right = StringValue.parseStringValue(preEvalRight);
        switch (op){
            case "==": return new BooleanValue(Objects.equals(left.value, right.value));
            default: throw new InterpreterException("Cannot use "+op+" operator on type String.");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static BooleanValue evaluate_boolean_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        BooleanValue left = (BooleanValue) preEvalLeft;
        BooleanValue right = (BooleanValue) preEvalRight;
        switch (op){
            case "==": return new BooleanValue(left.value == right.value);
            case "&": return new BooleanValue(left.value & right.value);
            case "|": return new BooleanValue(left.value | right.value);
            default: throw new InterpreterException("Cannot use "+op+" operator on type Boolean.");
        }
    }


    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_numeric_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{



        NumberValue left = (NumberValue) preEvalLeft; // pre evaluate the expressions because we dont know what type of value it will be
        NumberValue right = (NumberValue) preEvalRight;
        switch (op){
            case "+": return new NumberValue(left.value+right.value);
            case "-": return new NumberValue(left.value-right.value);
            case "*": return new NumberValue(left.value*right.value);
            case "/":
                float calc = (float) left.value / (float) right.value; // divide the two numbers
                if (right.value==0){
                    throw new InterpreterException("Division by zero");
                } // if its a division by zero we throw an error
                if (Math.floor(calc) != calc){ // if the result is a decimal, we give a floating value
                    return new FloatingValue((float) calc);
                }
                return new NumberValue(calc); // else we return a normal value
            case "%": return new NumberValue(left.value%right.value);
            case "-*": throw new InterpreterException("-* Operator only works on string values.");
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_string_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
        StringValue left = StringValue.parseStringValue(preEvalLeft);
        StringValue right = StringValue.parseStringValue(preEvalRight);
        int index;
        switch (op){
            case "+": return new StringValue(left.value+right.value);
            case "-":
                index = left.value.indexOf(right.value);
                if (index != -1) {
                    return new StringValue(left.value.substring(0, index) + left.value.substring(index + right.value.length()));
                }
                return new StringValue(left.value);
            case "*": throw new InterpreterException("Cannot use multiplication on string values");
            case "/": throw new InterpreterException("Cannot use division on string values");
            case "%": throw new InterpreterException("Cannot use modulus on string values");
            case "-*":return new StringValue(left.value.replace(right.value,""));
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_floating_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
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


    /**
     * @param binaryNode
     * @param environment
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_binary_expression(BinaryExpressionNode binaryNode, Environment environment, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException {
        if (binaryNode.logical){return evaluate_logical_expression(binaryNode, environment, pc, program);}
        Value preEvalLeft = evaluate(binaryNode.leftExpression, environment, pc, program); // pre evaluate the expressions because we dont know what type of value it will be
        Value preEvalRight = evaluate(binaryNode.rightExpression, environment, pc, program);
        if (preEvalLeft.type == RuntimeType.VOID | preEvalRight.type == RuntimeType.VOID){ // if the expression contains a void value then it must be resolved to void

            return new VoidValue();
        }
        if (preEvalRight.type == RuntimeType.STRING | preEvalLeft.type == RuntimeType.STRING){ // if either side is a string then it must be a string type
            return evaluate_string_binary_expression(preEvalLeft, preEvalRight, binaryNode.op);
        }
        if (preEvalLeft.type == RuntimeType.FLOAT | preEvalRight.type == RuntimeType.FLOAT){ // check if either are floating values
            return evaluate_floating_binary_expression(preEvalLeft, preEvalRight, binaryNode.op); // if it is then we call the floating binary expression calculator.
        }
        if (preEvalRight.type != preEvalLeft.type){ // if they arent floats or strings or voids, and they arent the same, then assume its a node
            // we havent handled yet
            throw new InterpreterException("Type promotion is not available for these types. Are you performing operations on different type?");
        }
        return evaluate_numeric_binary_expression(preEvalLeft, preEvalRight, binaryNode.op);
        // if NONE of the conditions apply, we have to assume its just plain old numeric evaluation


    }

    /**
     * <p>
     *     Takes in a {@link ProgramNode} and evaluates each {@link StatementNode} in the program.
     *     Pass in an {@link Environment} and a {@link ProgramCounter} so it can properly modify variables and use <code>jump</code> statements.
     * </p>
     * @param p The program node which needs to be evaluated.
     * @param environment The environment which can be changed as the program gets evaluated.
     * @param startingpc The starting program counter, in case we wanted to start from a different program counter for any reason.
     * @param initializeProgram Controls whether the program should be initialized or not. This will be off for subroutines because they are already initialized in the first loop.
     * @return the evaluation of the last statement.
     * @throws InterpreterException If a statement fails to be evaluated, an InterpreterException will occur.
     *
     * @see Initializer
     */
    public static Value evaluate_program(ProgramNode p, Environment environment, ProgramCounter startingpc, boolean initializeProgram) throws InterpreterException, ValueException {
        Timer evaltimer = new Timer("InterpreterTimer");

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        if (initializeProgram) Initializer.initialize_program(p, environment, new ProgramCounter(startingpc.get())); // declare labels ahead of time
        //
        while (pc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(pc.get());
            eval = evaluate(statement, environment, pc, p);
            pc.increment();
        }
        logger.whisperImportant("Took "+evaltimer.time()+" milliseconds");

        return eval;
    }

}

