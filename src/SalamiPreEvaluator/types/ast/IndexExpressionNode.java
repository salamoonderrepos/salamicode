package SalamiPreEvaluator.types.ast;

/**
 *
 */
public class IndexExpressionNode extends ExpressionNode{
    public ExpressionNode index;
    public ExpressionNode collection;
    public IndexExpressionNode(ExpressionNode _index, ExpressionNode _collection) {
        super(NodeType.INDEXEXPRESSION);
        index = _index;
        collection = _collection;
    }
    @Override
    public String toString() {
        return "IndexExpressionNode{" +
                "index=" + index +
                ", collection=" + collection +
                '}';
    }



}
