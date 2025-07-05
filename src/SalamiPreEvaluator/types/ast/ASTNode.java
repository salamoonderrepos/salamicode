package SalamiPreEvaluator.types.ast;

/**
 *  The base class for all AST nodes.
 */
abstract public class ASTNode{
    int[] location_from_file;

    public void setLocation_from_file(int[] location_from_file) {
        this.location_from_file = location_from_file;
    }

    public int[] getLocationFromFile() {
        return this.location_from_file;
    }
}