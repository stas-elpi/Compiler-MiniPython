import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class TypeCheckVisitor4 extends DepthFirstAdapter {
    private Hashtable<String, String> variableTypes; // Map variable names to their types

    public TypeCheckVisitor4(Hashtable<String, String> table) {
        this.variableTypes = table;
    }

    // Variable assignment: track type
    @Override
    public void inAAssignStatement(AAssignStatement node) {
        String variableName = node.getId().getText();
        String valueType = getType(node.getExpression());
        variableTypes.put(variableName, valueType);
    }

    // Variable usage: check type consistency for all operations
    @Override
    public void inAAddExpression(AAddExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "+");
    }

    @Override
    public void inASubExpression(ASubExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "-");
    }

    @Override
    public void inAMulExpression(AMulExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "*");
    }

    @Override
    public void inADivExpression(ADivExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "/");
    }

    @Override
    public void inAModExpression(AModExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "%");
    }

    @Override
    public void inAExponentExpression(AExponentExpression node) {
        checkBinaryOperation(node.getL(), node.getR(), "**");
    }

    @Override
    public void inAMaxvalueExpression(AMaxvalueExpression node){
        List<Node> values= new ArrayList<>();
        values.add(node.getExpression());
        for(PCommaValue comma_val:node.getCommaValue()) {
            ACommaValueCommaValue val= (ACommaValueCommaValue) comma_val;
            values.add(val.getExpression());
        }
        checkBinaryOperationMinMax(values,"max");
    }

    @Override
    public void inAMinvalueExpression(AMinvalueExpression node) {
        List<Node> values = new ArrayList<>();
        values.add(node.getExpression());
        for (PCommaValue comma_val : node.getCommaValue()) {
            ACommaValueCommaValue val = (ACommaValueCommaValue) comma_val;
            values.add(val.getExpression());
        }
        checkBinaryOperationMinMax(values, "min");
    }

    private void checkBinaryOperationMinMax(List<Node> values, String operator) {
        boolean allString = true;
        boolean atLeastOneString = false;
        for (Node val : values) {
            String type = getType(val);
            if (!type.equals("string")) {
                allString = false;
            } else {
                atLeastOneString = true;
            }
        }
        if (!allString) {
            if (atLeastOneString) {
                System.err.println("Error 4: Attempting to perform operation '" + operator
                        + "' on incompatible types: number and string");
            } else {
                return;
            }

        }
    }

    // Helper method: check binary operations for type compatibility
    private void checkBinaryOperation(Node left, Node right, String operator) {
        String leftType = getType(left);
        String rightType = getType(right);
        // System.out.println("left: " +leftType + "right: " + rightType);
        if (leftType != "unknown" && rightType != "unknown") {
            if (leftType.equals("string") && rightType.equals("string") && operator.equals("+")) {
                return;
            }
            if (!isNumeric(leftType) || !isNumeric(rightType)) {
                System.err
                        .println("Error 4a: Attempting to perform operation '" + operator + "' on incompatible types: "
                                + leftType + " and " + rightType);
            }
        }
    }

    // Helper method: determine type of an expression
    private String getType(Object node) {
        if (node instanceof Node) {
            Node actualNode = (Node) node;
            if (actualNode instanceof AIdentifierExpression) {
                String variableName = ((AIdentifierExpression) actualNode).getId().getText();
                return variableTypes.getOrDefault(variableName, "unknown");

            } else if (actualNode instanceof ANumberExpression) {
                return "number";
            } else if (actualNode instanceof AStringExpression) {
                return "string";
            } else if (actualNode instanceof AAddExpression || actualNode instanceof ASubExpression
                    || actualNode instanceof AMulExpression || actualNode instanceof ADivExpression
                    || actualNode instanceof AModExpression || actualNode instanceof AExponentExpression) {
                return "number"; // Assume numeric operations yield numbers
            }
        } else if (node instanceof List) {
            // Handle LinkedList<PEqValue>
            List<?> nodeList = (List<?>) node;
            if (!nodeList.isEmpty()) {
                Object firstElement = nodeList.get(0);
                return getType(firstElement); // Recursively get type of the first element
            }
        }
        return "unknown";
    }

    // Helper method: check if a type is numeric
    private boolean isNumeric(String type) {
        return "number".equals(type);
    }
}