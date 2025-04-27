import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor1_5 extends DepthFirstAdapter {
    private Hashtable<String, String> declaredVariables;
    private String obj= "true"; 
    myvisitor1_5(Hashtable<String, String> table) {
        this.declaredVariables = table;
    }
    // 1. Variable is used but not declared.

    // functions
    @Override
    public void inADefFunction(ADefFunction node) {
        // parameters
        if (!node.getArgument().isEmpty()) {

            AArgArgument argument = (AArgArgument) node.getArgument().getFirst();
            if (argument.getNextvalue() != null) {
                for (PNextvalue nextarg : argument.getNextvalue()) {
                    ANextvNextvalue next = (ANextvNextvalue) nextarg;
                    
                    declaredVariables.put(next.getId().getText(), obj);
                }
            }
            
            declaredVariables.put(argument.getId().getText(), obj);
        }
    }

    // assign
    @Override
    public void inAAssignStatement(AAssignStatement node) {
        declaredVariables.put(node.getId().getText(), obj);
    }

    // table
    @Override
    public void inATableStatement(ATableStatement node) {
        declaredVariables.put(node.getId().getText(), obj);
    }

    // checking
    @Override
    public void inAIdentifierExpression(AIdentifierExpression node) {
        String variableName = node.getId().getText();
        
        if (!declaredVariables.containsKey(variableName)) {
            System.err.println("Error: Variable '" + variableName + "' is used but not declared.");
        }

    }

    // for-loop
    @Override
    public void inAForStatement(AForStatement node) {
        declaredVariables.put(node.getId1().getText(), obj);
    }

    // 5. Operand is None
    @Override
    public void inAAddExpression(AAddExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "+");
    }

    @Override
    public void inASubExpression(ASubExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "-");
    }

    @Override
    public void inAMulExpression(AMulExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "*");
    }

    @Override
    public void inADivExpression(ADivExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "/");
    }

    @Override
    public void inAModExpression(AModExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "%");
    }

    @Override
    public void inAExponentExpression(AExponentExpression node) {
        checkNoneInExpression(node.getL(), node.getR(), "**");
    }
    @Override
    public void inAMaxvalueExpression(AMaxvalueExpression node){
        List<PExpression> values= new ArrayList<>();
        values.add(node.getExpression());
        for(PCommaValue comma_val:node.getCommaValue()) {
            ACommaValueCommaValue val= (ACommaValueCommaValue) comma_val;
            values.add(val.getExpression());
        }
        checkBinaryOperationMinMax(values,"max");
    }

    @Override
    public void inAMinvalueExpression(AMinvalueExpression node) {
        List<PExpression> values = new ArrayList<>();
        values.add(node.getExpression());
        for (PCommaValue comma_val : node.getCommaValue()) {
            ACommaValueCommaValue val = (ACommaValueCommaValue) comma_val;
            values.add(val.getExpression());
        }
        checkBinaryOperationMinMax(values, "min");
    }

    private void checkBinaryOperationMinMax(List<PExpression> values, String operator) {
        boolean atLeastOneNone = false;
        for (PExpression val : values) {
            
            if (isNone(val)) {
                atLeastOneNone = true;
            }
        }
        if (atLeastOneNone) {
            System.err.println("Error 5: There is at least one operand which is None" );
        } else {
            return;
        }  
    }
    
    private void checkNoneInExpression(PExpression left, PExpression right, String operator) {
        if (isNone(left)) {
            System.err.println("Error: Left operand of '" + operator + "' is None.");
        }
        if (isNone(right)) {
            System.err.println("Error: Right operand of '" + operator + "' is None.");
        }
    }

    
    private boolean isNone(PExpression expression) {
        if (expression instanceof ANoneExpression) {
            return true;
        }
        return false;
    }
    
}