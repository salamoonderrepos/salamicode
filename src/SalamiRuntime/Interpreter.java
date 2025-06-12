package SalamiRuntime;


import Helper.Logger.Logger;
import SalamiEvaluator.Lexer;
import SalamiEvaluator.LexerException;
import SalamiEvaluator.Parser;
import SalamiEvaluator.ParserException;
import SalamiEvaluator.types.ast.*;
import SalamiPackager.Packager;
import SalamiPackager.Packages.SalamiPackage;
import SalamiRuntime.Manager.EnvironmentFactory;
import SalamiRuntime.Runtime.*;
import SalamiRuntime.Runtime.Method.MethodValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Helper.Logger.Timer;

import javax.sound.sampled.Port;


/**
 * <p>
 *     Handles interpreting of an AST structure. Pass in any {@link StatementNode} to the <code>evaluate(StatementNode)</code> method to begin interpreting.
 * </p>
 * @see Parser
 * @see Lexer
 */
public class Interpreter {
    final Logger programlogger;
    public final Logger logger;
    public final Scanner reader;
    private Environment localEnvironment;

    public Interpreter(String location){
        programlogger = new Logger("RuntimeProgram/"+location);
        logger = new Logger("Interpreter/"+location);
        reader = new Scanner(System.in);
        localEnvironment = EnvironmentFactory.createGlobalEnvironment();
    }
    public Interpreter(String location, Environment env){
        programlogger = new Logger("RuntimeProgram/"+location);
        logger = new Logger("Interpreter/"+location);
        reader = new Scanner(System.in);
        localEnvironment = env;
    }
    public Interpreter(Environment env){
        programlogger = new Logger("RuntimeProgram");
        logger = new Logger("Interpreter");
        reader = new Scanner(System.in);
        localEnvironment = env;
    }
    public Interpreter(){
        programlogger = new Logger("RuntimeProgram");
        logger = new Logger("Interpreter");
        reader = new Scanner(System.in);
        localEnvironment = EnvironmentFactory.createGlobalEnvironment();
    }





    /**
     * <p>
     *     Evaluates a single statement. A
     * </p>
     * @param s The statement to be evaluated.
     * @param pc The program counter so we know where we are in the program.
     * @return the correctly evaluated statement.
     * @throws InterpreterException If it encounters a statement which it doesn't have an evaluator function for, it throws an error.
     * @throws ValueException If at any point two values are mismatched, or some other error like that, this gets thrown.
     */
    public Value evaluate(StatementNode s, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException, RuntimeDisruptedException{
        switch (s.type){
            case PROGRAM:
                ProgramNode p = (ProgramNode) s;
                return evaluate_program(p, pc, true);
            case PORTSTATEMENT:
                return new VoidValue();
                // DONT HANDLE PORTS CUZ THEY SHOULD BE DONE BY INITIALIZER
            case THROWSTATEMENT:
                ThrowStatement throwst = (ThrowStatement) s;
                return evaluate_throw_statement(throwst, pc, program);
            case RETURNSTATEMENT:
                ReturnStatement ret = (ReturnStatement) s;
                return evaluate_return_statement(ret, pc, program);
            case SUBROUTINEDECLARATIONSTATEMENT:
                return new VoidValue();
            // DONT HANDLE DECLARATION STATEMENTS BECAUSE THEY SHOULD ALREADY BE HANDLES
            case CALLSTATEMENT:
                CallStatement call = (CallStatement) s;
                return evaluate_call_statement(call, pc, program);
            case PRINTSTATEMENT:
                PrintStatement printstat = (PrintStatement) s;
                return evaluate_print_statement(printstat, pc, program);
            case JUMPSTATEMENT:
                JumpStatement jumpstat = (JumpStatement) s;
                return evaluate_jump_statement(jumpstat, pc);
            case LABELDECLARATIONSTATEMENT:
                LabelDeclarationStatement labdec = (LabelDeclarationStatement) s;
                return evaluate_label_statement(labdec, pc);
            case VARIABLEDECLARATIONSTATEMENT:
                VariableDeclarationStatement vardec = (VariableDeclarationStatement) s;
//                if (!environment.canVariable){
//                    if (environment.exitCapabilitiesQuietly){
//                        try {
//                            return evaluate(Parser.generalize_node(vardec), environment, pc, program);
//                        } catch (ParserException e) {
//                            throw new InterpreterException(e.getMessage());
//                        }
//                    } else {
//                        throw new CapabilityException("Variable capabilities are not allowed when declaring \""+vardec.identifier+'"');
//                    }
//                }
                // THIS HAS TO BE SIMPLER :(
                return evaluate_set_statement(vardec, pc, program);
            case EXPRESSIONINCREMENTSTATEMENT:
                ExpressionIncrementStatement expinc = (ExpressionIncrementStatement) s;
                return evaluate_increment_statement(expinc, pc);
            case IDENTIFIER:
                IdentifierNode identifier_node = (IdentifierNode) s;
                return evaluate_identifier(identifier_node);
            case ATTRIBUTEEXPRESSION:
                return evaluate_index_attributed_expression((AttributeExpressionNode) s, pc, program);
            case INDEXEXPRESSION:
                return evaluate_index_expression((IndexExpressionNode) s, pc, program);

            case STRINGLITERAL:
                StringLiteralNode stringLiteralNode = (StringLiteralNode) s;
                return new StringValue(stringLiteralNode.value);
            case NUMERICALLITERAL:
                NumericalLiteralNode numericNode = (NumericalLiteralNode) s; // tell java that we for sure have a numerical literal node here
                return new NumberValue(numericNode.value); // that way its happy when we ask it for the value
            case FLOATINGPOINTLITERAL:
                FloatingLiteralNode floatNode = (FloatingLiteralNode) s;
                return new FloatingValue(floatNode.value);
            case ARRAYLITERAL:
                List<Value> valueList = new ArrayList<>();
                ArrayLiteralNode arrayNode = (ArrayLiteralNode) s;
                for (ExpressionNode a : arrayNode.values){
                    valueList.add(evaluate(a, pc, program));
                }
                return new ArrayValue(valueList);
            case BINARYEXPRESSION:
                BinaryExpressionNode binaryNode = (BinaryExpressionNode) s;
                return evaluate_binary_expression(binaryNode, pc, program);
            case UNARYEXPRESSION:
                UnaryExpressionNode unaryNode = (UnaryExpressionNode) s;
                return evaluate_unary_expression(unaryNode, pc, program);
            case COMPARESTATEMENT:
                CompareStatement compNode = (CompareStatement) s;
                return evaluate_compare_statement(compNode, pc, program);
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

    public Value evaluate_call_statement(CallStatement callStatement, ProgramCounter pc, ProgramNode programNode) throws InterpreterException{

        if (localEnvironment.hasMethod(callStatement.identifier)){
            return evaluate_method_call_statement(callStatement, pc, programNode);
        }
        return evaluate_subroutine_call_statement(callStatement, pc, programNode);
    }
    public Value evaluate_port_statement(PortStatement s, ProgramCounter pc) throws InterpreterException {
        String packname = s.value;
        if ((packname.endsWith(".scpkg") || packname.endsWith(".spkg"))) {
            SalamiPackage pack = Packager.unzipPackage("packages\\" + packname);
            Packager.loadPackage(pack, this);

        } else if ((packname.endsWith(".salami") || packname.endsWith(".sal"))) {
            File file = new File("packages\\"+packname);
            Packager.loadFile(file, this);
        } else {
            throw new InterpreterException("Port statement does not reference a correct file.");
        }
        return new VoidValue();
    }

    public Value evaluate_method_call_statement(CallStatement methodCallStatement, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        MethodValue method = localEnvironment.lookupMethod(methodCallStatement.identifier);
        if (methodCallStatement.parameters.size() != method.parameters.size()){
            throw new InterpreterException("Parameter mismatch with method: "+methodCallStatement.identifier);
        }
        List<Value> passins = new java.util.ArrayList<>(List.of());
        for (int i = 0; i<methodCallStatement.parameters.size(); i++){
            passins.add(evaluate(methodCallStatement.parameters.get(i), pc, program));
        }
        return method.run(passins, programlogger);


    }

    /**
     * Evaluates jump statements, which set the program counter to the label passed in. <br>
     * <code>jump loopName</code>
     * @param jumpNode The <code>JumpStatement</code> AST node to be evaluated.
     * @param pc The program counter to be set so it can actually jump.
     * @return a {@link VoidValue}.
     */
    public VoidValue evaluate_jump_statement(JumpStatement jumpNode, ProgramCounter pc){
        pc.set(localEnvironment.lookupLabel(jumpNode.identifier));
        return new VoidValue();
    }


    public SubroutineValue evaluate_subroutine_declaration_statement(SubroutineDeclarationStatement subroutineDeclarationStatement, ProgramCounter pc){
        SubroutineValue subroutine = localEnvironment.declareSubroutine(subroutineDeclarationStatement.identifier,pc.get(),subroutineDeclarationStatement.parameters, subroutineDeclarationStatement.code);
        return subroutine;
    }
    public Value evaluate_return_statement(ReturnStatement returnStatement, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        return new ReturnValue(evaluate(returnStatement.statement, pc, program));
    }
    public Value evaluate_subroutine_call_statement(CallStatement subroutineCallStatement, ProgramCounter pc, ProgramNode program) throws InterpreterException{
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



        SubroutineValue subroutine = localEnvironment.lookupSubroutine(subroutineCallStatement.identifier);

        Interpreter subroutine_interpreter = new Interpreter("subroutine/"+subroutineCallStatement.identifier, subroutine.env);

        if (subroutineCallStatement.parameters.size() != subroutine.parameters.size()){
            throw new InterpreterException("Parameter mismatch with subroutine: "+subroutineCallStatement.identifier);
        }
        for (int i = 0; i<subroutineCallStatement.parameters.size(); i++){
            Value arguement = evaluate(subroutineCallStatement.parameters.get(i), pc, program);
            subroutine.env.declareVariable(subroutine.parameters.get(i), arguement, false);
        }


        Value returnvalue = subroutine_interpreter.evaluate_subroutine(subroutine.code, new ProgramCounter(0));
        for (int i = 0; i<subroutineCallStatement.parameters.size(); i++){
            subroutine.env.forgetVariable(subroutine.parameters.get(i));
        }
        return returnvalue;
    }


    /** Evaluates label declaration statements like: <br>
     * <code>label loopName</code>
     * @param declarationNode The <code>LabelDeclarationStatement</code> AST node to be evaluated.
     * @param pc The program counter to tell the label where it is in the program.
     * @return a {@link VoidValue}.
     */
    public VoidValue evaluate_label_statement(LabelDeclarationStatement declarationNode, ProgramCounter pc){
        localEnvironment.declareLabel(declarationNode.identifier, pc.get());
        return new VoidValue();
    }

    /** Evaluates print statements like: <br>
     * <code>print 'Hello, world!</code>
     * @param printNode The <code>PrintStatement</code> AST node to be evaluated.
     * @param pc The program counter. Does not have to be a specific counter, as it will never be used.
     * @return a {@link VoidValue}.
     * @throws InterpreterException In case the evaluation of what needs to be printed goes wrong.
     */
    public VoidValue evaluate_print_statement(PrintStatement printNode, ProgramCounter pc, ProgramNode program) throws InterpreterException {
        programlogger.log(StringValue.parseStringValue(evaluate(printNode.value, pc, program)).value);
        return new VoidValue();
    }

    public VoidValue evaluate_throw_statement(ThrowStatement throwNode, ProgramCounter pc, ProgramNode program) throws InterpreterException, RuntimeDisruptedException {
        StringValue message = StringValue.parseStringValue(evaluate(throwNode.value, pc, program));
        throw new RuntimeDisruptedException(message.value);
    }

    /** Evaluates variable declaration statements like: <br>
     * <code>set message to 'Hello, world!"</code>
     * @param declarationNode The <code>VariableDeclarationNode</code> AST node to be evaluated.
     * @param pc The program counter. Does not have to be a specific counter, as it will never be used.
     * @return a {@link VoidValue}.
     * @throws InterpreterException If what we are defining the variable as can't be evaluated, then it throws an error.
     */
    public VoidValue evaluate_set_statement(VariableDeclarationStatement declarationNode, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        localEnvironment.declareVariable(declarationNode.identifier, evaluate(declarationNode.value, pc, program), declarationNode.isFinal);
        return new VoidValue();
    }

    /**
     * @param compareNode
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public Value evaluate_compare_statement(CompareStatement compareNode, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        if (evaluate(compareNode.comp, pc, program) instanceof BooleanValue bval){
            if (bval.value) {
                return evaluate(compareNode.execute, pc, program);
            }
        }
        return new VoidValue();
    }

    /**
     * @param incrementNode
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public VoidValue evaluate_increment_statement(ExpressionIncrementStatement incrementNode, ProgramCounter pc) throws InterpreterException{
        Value variable_value = localEnvironment.lookupVariale(incrementNode.identifier);
        switch (variable_value.type){
            case NUMBER:
                NumberValue number_value = (NumberValue) variable_value;
                localEnvironment.assignVariable(incrementNode.identifier, new NumberValue(number_value.value+1));
                return new VoidValue();
            case FLOAT:
                FloatingValue float_value = (FloatingValue) variable_value;
                localEnvironment.assignVariable(incrementNode.identifier, new FloatingValue(float_value.value+1F));
                return new VoidValue();
            case STRING:
                StringValue stringValue = (StringValue) variable_value;
                localEnvironment.assignVariable(incrementNode.identifier, new StringValue(stringValue.value + stringValue.value));
                return new VoidValue();
            default: throw new InterpreterException("Increment node applied to types that AREN'T number/float.");
        }
    }


    ///-----------------------------
    ///
    ///         EXPRESSIONS
    ///
    ///-----------------------------

    public Value evaluate_subroutine(ProgramNode p, ProgramCounter startingpc) throws InterpreterException, ValueException {

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        //Initializer.initialize_program(p, environment, new ProgramCounter(startingpc.get())); // declare labels ahead of time
        //
        while (pc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(pc.get());
            eval = evaluate(statement, pc, p);
            if (eval.type==RuntimeType.RETURNVALUE){
                ReturnValue rv = (ReturnValue) eval;
                return rv.value;
            }
            pc.increment();
        }
        throw new InterpreterException("Subroutine found EOF before returning.");
    }
    public Value evaluate_index_expression(IndexExpressionNode node, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException {
        Value indexVal = evaluate(node.index, pc, program);
        Value collectionVal = evaluate(node.collection, pc, program);

        if (collectionVal instanceof ArrayValue array) {
            int indexnumber = (int) NumberValue.parseNumberValue(indexVal).value;
            if (indexnumber < 0 || indexnumber >= array.values.size())
                throw new InterpreterException("Array index out of bounds.");
            return array.values.get(indexnumber);
        }

        if (collectionVal instanceof StringValue str) {
            int indexnumber = (int) NumberValue.parseNumberValue(indexVal).value;
            if (indexnumber < 0 || indexnumber >= str.value.length())
                throw new InterpreterException("String index out of bounds.");
            return new StringValue(Character.toString(str.value.charAt(indexnumber)));
        }
        throw new InterpreterException("Cannot index type: " + collectionVal.type);
    }

    public Value evaluate_index_attributed_expression(AttributeExpressionNode node, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException {
        String attribute = node.attribute;
        Value val = evaluate(node.collection, pc, program);

        if (val.attributes.isEmpty()){
            throw new InterpreterException("Cannot attribute type: " + val.type);
        }

        if (!val.attributes.containsKey(attribute)){
            throw new InterpreterException("Attribute \""+attribute+"\" does not exist for value "+val.type);
        };
        AttributeValue attributeFunction = val.attributes.get(attribute);

        return attributeFunction.getValue(logger);

    }

    /**
     *
     * @param node
     * @return
     * @throws ValueException
     */
    public Value evaluate_identifier(IdentifierNode node) throws ValueException{
        return localEnvironment.lookupVariale(node.value);
    }

    /**
     * @param unaryNode
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public Value evaluate_unary_expression(UnaryExpressionNode unaryNode, ProgramCounter pc, ProgramNode program) throws InterpreterException {
        Value preevalright = evaluate(unaryNode.rightExpression, pc, program);
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
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public BooleanValue evaluate_logical_expression(BinaryExpressionNode binaryNode, ProgramCounter pc, ProgramNode program) throws InterpreterException{
        Value left = (Value) evaluate(binaryNode.leftExpression, pc, program);
        Value right = (Value) evaluate(binaryNode.rightExpression, pc, program);
        if (left.type == RuntimeType.VOID | right.type == RuntimeType.VOID){ // if the expression contains a void value then it must be resolved to void

            throw new InterpreterException("Void Value used inside of a logical expression");
        }
        if (left.type == RuntimeType.ARRAY | right.type == RuntimeType.ARRAY){ // if the expression contains a void value then it must be resolved to void

            return evaluate_arrayedly_logical_expression(left, right, binaryNode.op);
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
    public BooleanValue evaluate_numeric_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
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
    public BooleanValue evaluate_floating_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
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

    public BooleanValue evaluate_arrayedly_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
        FloatingValue left = FloatingValue.parseFloatingValue(preEvalLeft);
        FloatingValue right = FloatingValue.parseFloatingValue(preEvalRight);
        switch (op){
            case "==": return new BooleanValue(left.value == right.value);
            default: throw new InterpreterException("Cannot use "+op+" operator on type Array.");
        }
    }

    /**
     * @param preEvalLeft
     * @param preEvalRight
     * @param op
     * @return
     * @throws InterpreterException
     */
    public BooleanValue evaluate_string_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
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
    public BooleanValue evaluate_boolean_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
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
    public Value evaluate_numeric_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{



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
    public Value evaluate_string_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
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

    public Value evaluate_arrayedly_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
        ArrayValue left = ArrayValue.parseArrayValue(preEvalLeft);
        ArrayValue right = ArrayValue.parseArrayValue(preEvalRight);
        int index;
        switch (op){
            case "+": left.values.addAll(right.values); return left;
            case "-":
                left.values.remove(right.values.get(0)); return left;
            case "*": throw new InterpreterException("Cannot use multiplication on array values");
            case "/": throw new InterpreterException("Cannot use division on array values");
            case "%": throw new InterpreterException("Cannot use modulus on array values");
            case "-*": left.values.removeAll(right.values); return left;
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
    public Value evaluate_floating_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
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
            case "-*": throw new InterpreterException("-* Operator only works on string or array values.");
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }


    /**
     * @param binaryNode
     * @param pc
     * @return
     * @throws InterpreterException
     */
    public Value evaluate_binary_expression(BinaryExpressionNode binaryNode, ProgramCounter pc, ProgramNode program) throws InterpreterException, ValueException {
        if (binaryNode.logical){return evaluate_logical_expression(binaryNode, pc, program);}
        Value preEvalLeft = evaluate(binaryNode.leftExpression, pc, program); // pre evaluate the expressions because we dont know what type of value it will be
        Value preEvalRight = evaluate(binaryNode.rightExpression, pc, program);
        if (preEvalLeft.type == RuntimeType.VOID | preEvalRight.type == RuntimeType.VOID){ // if the expression contains a void value then it must be resolved to void

            return new VoidValue();
        }
        if (preEvalLeft.type == RuntimeType.ARRAY | preEvalRight.type == RuntimeType.ARRAY){ // check if either are floating values
            return evaluate_arrayedly_binary_expression(preEvalLeft, preEvalRight, binaryNode.op); // if it is then we call the floating binary expression calculator.
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
     * @param startingpc The starting program counter, in case we wanted to start from a different program counter for any reason.
     * @param initializeProgram Controls whether the program should be initialized or not. This will be off for subroutines because they are already initialized in the first loop.
     * @return the evaluation of the last statement.
     * @throws InterpreterException If a statement fails to be evaluated, an InterpreterException will occur.
     *
     * @see Initializer
     */
    public Value evaluate_program(ProgramNode p, ProgramCounter startingpc, boolean initializeProgram) throws InterpreterException, ValueException, StackOverflowError{
        Timer evaltimer = new Timer("InterpreterTimer");

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        if (initializeProgram) Initializer.initialize_program(p, this, new ProgramCounter(startingpc.get())); // declare labels ahead of time
        //
        while (pc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(pc.get());

            eval = evaluate(statement, pc, p);
            pc.increment();
        }
        logger.whisper("Took "+evaltimer.time()+" milliseconds");

        return eval;
    }

    public Environment getLocalEnvironment(){
        return localEnvironment;
    }



}

