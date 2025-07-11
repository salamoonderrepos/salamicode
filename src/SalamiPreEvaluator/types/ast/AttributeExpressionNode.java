package SalamiPreEvaluator.types.ast;

/**
 *
 */
public class AttributeExpressionNode extends ExpressionNode{
    public String attribute;
    public ExpressionNode collection;
    public AttributeExpressionNode(String _attribute, ExpressionNode _collection) {
        super(NodeType.ATTRIBUTEEXPRESSION);
        attribute = _attribute;
        collection = _collection;
    }


    @Override
    public String toString() {
        return "AttributeExpressionNode{" +
                "attribute='" + attribute + '\'' +
                ", collection=" + collection +
                '}';
    }
}
