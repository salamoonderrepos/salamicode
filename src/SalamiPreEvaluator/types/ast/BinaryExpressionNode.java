package SalamiPreEvaluator.types.ast;

/**
 * <p>
 *     The <code>BinaryExpressionNode</code> handles binary expressions. Binary expressions are
 *     expressions with two operands and one operator. Things like <code>32*29</code> are binary expressions.
 *     Binary expressions can also contain other binary expressions making them recursive.
 *     <code>32*29+987/2</code> would be split up into:
 *<blockquote><pre>BinaryExpression{
 *  32
 *  *
 *  Binary Expression{
 *      Binary Expression{29+987}
 *      /
 *      2
 *  }
 *}</pre></blockquote>
 * </p>
 */
public class BinaryExpressionNode extends ExpressionNode{
    public ExpressionNode leftExpression;
    public String op;
    public ExpressionNode rightExpression;
    public boolean logical;
    public BinaryExpressionNode(ExpressionNode leftEx, String _op, ExpressionNode rightEx, boolean log) {
        super(NodeType.BINARYEXPRESSION);
        leftExpression = leftEx;
        op = _op;
        rightExpression = rightEx;
        logical = log;
    }

    @Override
    public String toString() {
        return "BinaryExpressionNode{" +
                "leftExpression=" + leftExpression +
                ", op='" + op + '\'' +
                ", rightExpression=" + rightExpression +
                ", logical=" + logical +
                '}';
    }
}
