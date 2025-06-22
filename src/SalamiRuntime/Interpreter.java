package SalamiRuntime;


import Helper.Logger.Logger;
import SalamiPackager.PackageException;
import SalamiPreEvaluator.Lexer;
import SalamiPreEvaluator.Parser;
import SalamiPreEvaluator.types.ast.*;
import SalamiPackager.Packager;
import SalamiPackager.Packages.SalamiPackage;
import SalamiRuntime.RuntimeData.*;
import SalamiRuntime.RuntimeData.Method.MethodValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import Helper.Logger.Timer;


/**
 * <p>
 *     Handles interpreting of an AST structure. Pass in any {@link StatementNode} to the <code>evaluate(StatementNode)</code> method to begin interpreting.
 * </p>
 * @see Parser
 * @see Lexer
 */
public class Interpreter {
    static final Logger programlogger = new Logger("RuntimeProgram");
    public static final Logger logger = new Logger("Interpreter");
    static final Scanner reader = new Scanner(System.in);





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
    public static Value evaluate(StatementNode s, Environment environment, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException, ValueException, RuntimeDisruptedException{
        switch (s.type){
            case PROGRAM:
                throw new InterpreterException("`PROGRAM` AST Node has been given to the interpreter. This was not you. Don't worry. It's Salamoonder's fault. Yell at him please.");
            case PORTSTATEMENT:
                return new VoidValue();
                // DONT HANDLE PORTS CUZ THEY SHOULD BE DONE BY INITIALIZER
            case THROWSTATEMENT:
                ThrowStatement throwst = (ThrowStatement) s;
                return evaluate_throw_statement(throwst, environment, pc, program, location);
            case RETURNSTATEMENT:
                ReturnStatement ret = (ReturnStatement) s;
                return evaluate_return_statement(ret, environment, pc, program, location);
            case SUBROUTINEDECLARATIONSTATEMENT:
                return new VoidValue();
            // DONT HANDLE DECLARATION STATEMENTS BECAUSE THEY SHOULD ALREADY BE HANDLES
            case CALLSTATEMENT:
                CallStatement call = (CallStatement) s;
                return evaluate_call_statement(call, environment, pc, program, location);
            case PRINTSTATEMENT:
                PrintStatement printstat = (PrintStatement) s;
                return evaluate_print_statement(printstat, environment, pc, program, location);
            case JUMPSTATEMENT:
                JumpStatement jumpstat = (JumpStatement) s;
                return evaluate_jump_statement(jumpstat, environment, pc);
            case LABELDECLARATIONSTATEMENT:
                LabelDeclarationStatement labdec = (LabelDeclarationStatement) s;
                return evaluate_label_statement(labdec, environment, pc);
            case VARIABLEDECLARATIONSTATEMENT:
                VariableDeclarationStatement vardec = (VariableDeclarationStatement) s;
                return evaluate_set_statement(vardec, environment, pc, program, location);
            case EXPRESSIONINCREMENTSTATEMENT:
                ExpressionIncrementStatement expinc = (ExpressionIncrementStatement) s;
                return evaluate_increment_statement(expinc, environment, pc);
            case IDENTIFIER:
                IdentifierNode identifier_node = (IdentifierNode) s;
                return evaluate_identifier(identifier_node, environment);
            case ATTRIBUTEEXPRESSION:
                return evaluate_index_attributed_expression((AttributeExpressionNode) s, environment, pc, program, location);
            case INDEXEXPRESSION:
                return evaluate_index_expression((IndexExpressionNode) s, environment, pc, program, location);

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
                    valueList.add(evaluate(a, environment, pc, program, location));
                }
                return new ArrayValue(valueList);
            case BINARYEXPRESSION:
                BinaryExpressionNode binaryNode = (BinaryExpressionNode) s;
                return evaluate_binary_expression(binaryNode, environment, pc, program, location);
            case UNARYEXPRESSION:
                UnaryExpressionNode unaryNode = (UnaryExpressionNode) s;
                return evaluate_unary_expression(unaryNode, environment, pc, program, location);
            case COMPARESTATEMENT:
                CompareStatement compNode = (CompareStatement) s;
                return evaluate_compare_statement(compNode, environment, pc, program, location);
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

    public static Value evaluate_call_statement(CallStatement callStatement, Environment env, ProgramCounter pc, ProgramNode programNode, String location) throws InterpreterException{
        if (env.hasMethod(callStatement.identifier)){
            return evaluate_method_call_statement(callStatement, env, pc, programNode, location);
        }
        return evaluate_subroutine_call_statement(callStatement, env, pc, programNode, location);
    }
    public static Value evaluate_port_statement(PortStatement portStatement, Environment environment) throws InterpreterException, PackageException {
        String thingtoport = portStatement.value;
        if ((thingtoport.endsWith(".scpkg") || thingtoport.endsWith(".spkg"))) {

            File packfile = Packager.findPackage(thingtoport);

            SalamiPackage pack = Packager.unzipPackage(packfile);
            Packager.loadPackage(pack, environment);
            // this feels better

        } else if ((thingtoport.endsWith(".salami") || thingtoport.endsWith(".sal"))) {
            File file = new File("packages\\"+thingtoport);
            Packager.loadFile(file, environment);
            // same here
        } else {
            throw new InterpreterException("Port statement does not reference a correct file.");
        }
        return new VoidValue();
    }
//    public static Value evaluate_port_statement_from_package(PortStatement portStatement, Environment environment, SalamiPackage sourcePackage) throws InterpreterException, PackageException {
//        String thingtoport = portStatement.value;
//        if (!(sourcePackage.contents.containsKey(thingtoport))){
//            ProgramNode programtoport = sourcePackage.contents.get(thingtoport);
//        }
//        return new VoidValue();
//    }

    public static Value evaluate_method_call_statement(CallStatement methodCallStatement, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
        MethodValue method = env.lookupMethod(methodCallStatement.identifier);
        if (methodCallStatement.parameters.size() != method.parameters.size()){
            throw new InterpreterException("Parameter mismatch with method: "+methodCallStatement.identifier);
        }
        List<Value> passins = new ArrayList<>(List.of());
        for (int i = 0; i<methodCallStatement.parameters.size(); i++){
            passins.add(evaluate(methodCallStatement.parameters.get(i), env, pc, program, location));
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
    public static Value evaluate_return_statement(ReturnStatement returnStatement, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
        return new ReturnValue(evaluate(returnStatement.statement, env, pc, program, location));
    }
    public static Value evaluate_subroutine_call_statement(CallStatement subroutineCallStatement, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
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
            Value arguement = evaluate(subroutineCallStatement.parameters.get(i), env, pc, program, location);
            subroutine.env.declareVariable(subroutine.parameters.get(i), arguement, false);
        }


        Value returnvalue = evaluate_subroutine(subroutine.code, subroutine.env, new ProgramCounter(0), location);
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

    /**
     * Evaluates print statements like: <br>
     * <code>print 'Hello, world!</code>
     *
     * @param printNode The <code>PrintStatement</code> AST node to be evaluated.
     * @param env       The environment to grab identifiers from.
     * @param pc        The program counter. Does not have to be a specific counter, as it will never be used.
     * @param location
     * @return a {@link VoidValue}.
     * @throws InterpreterException In case the evaluation of what needs to be printed goes wrong.
     */
    public static VoidValue evaluate_print_statement(PrintStatement printNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException {
        programlogger.logExtra(StringValue.parseStringValue(evaluate(printNode.value, env, pc, program, location)).value, location);
        return new VoidValue();
    }

    public static VoidValue evaluate_throw_statement(ThrowStatement throwNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException, RuntimeDisruptedException {
        StringValue message = StringValue.parseStringValue(evaluate(throwNode.value, env, pc, program, location));
        throw new RuntimeDisruptedException("["+location+"] "+ message.value);
    }

    /**
     * Evaluates variable declaration statements like: <br>
     * <code>set message to 'Hello, world!"</code>
     *
     * @param declarationNode The <code>VariableDeclarationNode</code> AST node to be evaluated.
     * @param env             The environment to declare the variable too.
     * @param pc              The program counter. Does not have to be a specific counter, as it will never be used.
     * @param location
     * @return a {@link VoidValue}.
     * @throws InterpreterException If what we are defining the variable as can't be evaluated, then it throws an error.
     */
    public static VoidValue evaluate_set_statement(VariableDeclarationStatement declarationNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
        env.declareVariable(declarationNode.identifier, evaluate(declarationNode.value, env, pc, program, location), declarationNode.isFinal);
        return new VoidValue();
    }

    /**
     * @param compareNode
     * @param env
     * @param pc
     * @param location
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_compare_statement(CompareStatement compareNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
        if (evaluate(compareNode.comp, env, pc, program, location) instanceof BooleanValue bval){
            if (bval.value) {
                return evaluate(compareNode.execute, env, pc, program, location);
            }
        }
        return new VoidValue();
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

    public static Value evaluate_subroutine(ProgramNode p, Environment environment, ProgramCounter startingpc, String location) throws InterpreterException, ValueException {

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        //Initializer.initialize_program(p, environment, new ProgramCounter(startingpc.get())); // declare labels ahead of time
        //
        while (pc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(pc.get());
            eval = evaluate(statement, environment, pc, p, location);
            if (eval.type==RuntimeType.RETURNVALUE){
                ReturnValue rv = (ReturnValue) eval;
                return rv.value;
            }
            pc.increment();
        }
        throw new InterpreterException("Subroutine found EOF before returning.");
    }
    public static Value evaluate_index_expression(IndexExpressionNode node, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException, ValueException {
        Value indexVal = evaluate(node.index, env, pc, program, location);
        Value collectionVal = evaluate(node.collection, env, pc, program, location);

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

    public static Value evaluate_index_attributed_expression(AttributeExpressionNode node, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException, ValueException {
        String attribute = node.attribute;
        Value val = evaluate(node.collection, env, pc, program, location);

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
     * @param location
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_unary_expression(UnaryExpressionNode unaryNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException {
        Value preevalright = evaluate(unaryNode.rightExpression, env, pc, program, location);
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
    public static BooleanValue evaluate_logical_expression(BinaryExpressionNode binaryNode, Environment env, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException{
        Value left = (Value) evaluate(binaryNode.leftExpression, env, pc, program, location);
        Value right = (Value) evaluate(binaryNode.rightExpression, env, pc, program, location);
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

    public static BooleanValue evaluate_arrayedly_logical_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException{
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

    public static Value evaluate_arrayedly_binary_expression(Value preEvalLeft, Value preEvalRight, String op) throws InterpreterException, ValueException{
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
            case "-*": throw new InterpreterException("-* Operator only works on string or array values.");
            default: throw new InterpreterException("Unexpected Operator in Binary Expression Node");
        }
    }


    /**
     * @param binaryNode
     * @param environment
     * @param pc
     * @param location
     * @return
     * @throws InterpreterException
     */
    public static Value evaluate_binary_expression(BinaryExpressionNode binaryNode, Environment environment, ProgramCounter pc, ProgramNode program, String location) throws InterpreterException, ValueException {
        if (binaryNode.logical){return evaluate_logical_expression(binaryNode, environment, pc, program, location);}
        Value preEvalLeft = evaluate(binaryNode.leftExpression, environment, pc, program, location); // pre evaluate the expressions because we dont know what type of value it will be
        Value preEvalRight = evaluate(binaryNode.rightExpression, environment, pc, program, location);
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








}

