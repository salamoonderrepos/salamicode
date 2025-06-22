package SalamiPreEvaluator.types.ast;


/**
 * The node type for each AST node. Helps keep the checking of different node types neat and concise.
 */
public enum NodeType {
    AST,
    BINARYEXPRESSION,
    EXPRESSION,
    PROGRAM,
    STATEMENT,
    VARIABLEDECLARATIONSTATEMENT,
    EXPRESSIONINCREMENTSTATEMENT,
    /** A number. (3, 20, 139, etc...) */
    NUMERICALLITERAL,
    /** A series of characters. ("Hello, World", "Foo", "Bar", etc...) */
    STRINGLITERAL,
    /** A decimal. (13.2, 289.1302, 3.14159, etc...) */
    FLOATINGPOINTLITERAL,
    /** A collection of characters that isn't a string. Used for variables and such. (varName, x, y, etc...) */
    IDENTIFIER,
    COMMENT,
    VOIDLITERAL,
    LABELDECLARATIONSTATEMENT,
    JUMPSTATEMENT,
    UNARYEXPRESSION,
    COMPARESTATEMENT,
    PORTSTATEMENT,
    SUBROUTINEDECLARATIONSTATEMENT,
    CALLSTATEMENT,
    CLASSDECLARATIONSTATEMENT,
    PRINTSTATEMENT,
    SUBROUTINEENDINDICATOR,
    RETURNSTATEMENT,
    INDEXEXPRESSION,
    ARRAYLITERAL,
    THROWSTATEMENT,
    ATTRIBUTEEXPRESSION,
}
