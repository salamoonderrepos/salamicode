package SalamiEvaluator.types.ast;

import SalamiEvaluator.types.Type;

public class CommentNode extends StatementNode{
    String raw;
    String content;
    public CommentNode(String r) {
        super(NodeType.COMMENT);
        raw = r;
        content = r.substring(2);
    }

    @Override
    public String toString() {
        return "CommentNode{" +
                "raw='" + raw + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
