import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class MyFunctionVisitor6 extends DepthFirstAdapter {
    private Hashtable<String, String> functionReturnTypes;

    public MyFunctionVisitor6(Hashtable<String, String> table) {
        this.functionReturnTypes = table;
    }

    // 6. Wrong use of function

    @Override
    public void inADefFunction(ADefFunction node) {
        String functionName = node.getId().getText();
        String returnType = inferReturnType(node);
        functionReturnTypes.put(functionName, returnType);
    }

    private String inferReturnType(ADefFunction node) {
        PStatement stmt = node.getStatement();
        if (stmt instanceof AReturnStatement) {
            AReturnStatement returnStmt = (AReturnStatement) stmt;
            PExpression expr = returnStmt.getExpression();

            if (expr instanceof AAddExpression) {
                return "add";
            }
            if (expr instanceof ASubExpression) {
                return "sub";
            }
            if (expr instanceof AMulExpression) {
                return "mul";
            }
            if (expr instanceof ADivExpression) {
                return "div";
            }
            if (expr instanceof AModExpression) {
                return "mod";
            }
            if (expr instanceof AExponentExpression) {
                return "exp";
            }
            if (expr instanceof AStringExpression) {
                return "string";
            } else if (expr instanceof ANumberExpression) {
                return "number";
            } else {
                return "unknown";
            }
        }

        return "void";
    }

    @Override
    public void inAAddExpression(AAddExpression node) {
        checkExpression(node.getL(), node.getR(), "+");
    }

    @Override
    public void inASubExpression(ASubExpression node) {
        checkExpression(node.getL(), node.getR(), "-");
    }

    @Override
    public void inAMulExpression(AMulExpression node) {
        checkExpression(node.getL(), node.getR(), "");
    }

    @Override
    public void inADivExpression(ADivExpression node) {
        checkExpression(node.getL(), node.getR(), "/");
    }

    @Override
    public void inAModExpression(AModExpression node) {
        checkExpression(node.getL(), node.getR(), "%");
    }

    @Override
    public void inAExponentExpression(AExponentExpression node) {
        checkExpression(node.getL(), node.getR(), "*");
    }

    private void checkExpression(PExpression left, PExpression right, String operator) {
        String leftType = getExpressionType(left);
        String rightType = getExpressionType(right);

        
        for (Map.Entry<String, String> entry : functionReturnTypes.entrySet()) {
            String key = entry.getKey(); // The variable name (key)
            String value = entry.getValue(); // The type of the variable (value)
            for (Map.Entry<String, String> en : functionReturnTypes.entrySet()) {
                String key1 = en.getKey(); // The variable name (key)
                String value1 = en.getValue(); // The type of the variable (value)
                if (key1.equals(key + "1")) {
                    if(!value.equals(value1)){
                        if(leftType.equals("function")){
                            leftType = value1;
                        }else if(rightType.equals("function")){
                            rightType = value1;
                        }
                        
                    }else{
                        if(leftType.equals("function")){
                            leftType = value;
                        }else if(rightType.equals("function")){
                            rightType = value;
                        }
                    }
                }
            }
        }
        

        if (!operator.equals("+") ) {
            // string + string ή string * int
            if (leftType.equals("string") && rightType.equals("string")) {
                System.err.println("Error: Operator '" + operator + "' used with incompatible types: " + leftType + " and " + rightType);
            }
        }

        if (leftType.equals("number") && rightType.equals("string")|| rightType.equals("number") && leftType.equals("string")) {
            System.err.println("Error: Operator '" + operator + "' used with incompatible types: " + leftType + " and "
                    + rightType);
        }
    }

    private String getExpressionType(PExpression expr) {
        if (expr instanceof AFunctionCallExpression) {

            AFunctionCallExpression functionCall = (AFunctionCallExpression) expr;
            AFunctioncallStatement p_function = (AFunctioncallStatement) functionCall.getStatement();
            String functionName = p_function.getId().getText() + "1";
            String type = findTypeofFunctionCall(p_function);
            functionReturnTypes.put(functionName, type);
            return "function";
        } else if (expr instanceof AStringExpression) {
            return "string";
        } else if (expr instanceof ANumberExpression) {
            return "number";
        }
        return "unknown";
    }

    public String findTypeofFunctionCall(AFunctioncallStatement node) {
        String functionName = node.getId().getText();

        
        for (Map.Entry<String, String> entry : functionReturnTypes.entrySet()) {
            String key = entry.getKey(); // The variable name (key)
            String value = entry.getValue(); // The type of the variable (value)

            if (functionName.equals(key)) {
                

                List<String> argumentTypes = new ArrayList<>();

                if (!node.getArgslist().isEmpty()) {
                    AArgslistArgslist argsList = (AArgslistArgslist) node.getArgslist().getFirst();
                    if (getType(argsList.getL()).equals("string")) {
                        String a = argsList.getL().toString();
                        if (a.contains(",")) {
                            String[] tabl = a.split(",");
                            for (String d : tabl) {
                                if (d.contains("\"") || d.contains("'")) {
                                    argumentTypes.add("string");
                                } else {
                                    argumentTypes.add("number");
                                }
                            }
                        } else {
                            argumentTypes.add(getType(argsList.getL()));
                        }
                    } else {
                        argumentTypes.add(getType(argsList.getL()));
                    }
                    if (!argsList.getR().isEmpty()) {
                        for (PCommaExpression commaExp : argsList.getR()) {
                            ACommaExpCommaExpression comma = (ACommaExpCommaExpression) commaExp;
                            String a = comma.getExpression().toString();
                            if (a.contains(",")) {
                                String[] tabl = a.split(",");
                                for (String d : tabl) {
                                    if (d.contains("\"") || d.contains("'")) {
                                        argumentTypes.add("string");
                                    } else {
                                        argumentTypes.add("number");
                                    }
                                }
                            } else {
                                argumentTypes.add(getType(comma.getExpression()));
                            }
                        }
                    }
                }
                boolean allString = true;
                boolean atLeastOneString=false;
                for (String x : argumentTypes) {
                    if(!x.equals("string")){
                        allString=false;
                    }else{ 
                        atLeastOneString=true;
                    }
                }
                if(!value.equals("string") && !value.equals("number") && !value.equals("unknown")){
                    if(allString){
                        if(!value.equals("add")){
                           
                            return "error";
                        }else{
                            return "string";
                        }
                        
                    }else{
                        if(atLeastOneString){
                            return "error";
                        }else{
                            return "number";
                        }
                    }
                }else{
                    return value;
                }
            
            }
            return "other";

        }
        return "other";
    }

    private String getType(Object node) {
        if (node instanceof Node) {
            Node actualNode = (Node) node;
            if (actualNode instanceof AIdentifierExpression) {
                String variableName = ((AIdentifierExpression) actualNode).getId().getText();
                return functionReturnTypes.getOrDefault(variableName, "unknown");
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
}
