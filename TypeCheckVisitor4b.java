import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class TypeCheckVisitor4b extends DepthFirstAdapter {
    private Hashtable<String, String> functionReturnTypes;

    public TypeCheckVisitor4b(Hashtable<String, String> table) {
        this.functionReturnTypes = table;
    }

    @Override
    public void inADefFunction(ADefFunction node) {
        String functionName = node.getId().getText();
        String returnType = inferReturnType(node);
        functionReturnTypes.put(functionName, returnType);

    }
    @Override
    public void inAAssignStatement(AAssignStatement node) {
        String variableName = node.getId().getText();
        String valueType = getType(node.getExpression());
        functionReturnTypes.put(variableName, valueType);
    }

    private String inferReturnType(ADefFunction node) {
        PStatement stmt = node.getStatement();
        if (stmt instanceof AReturnStatement) {
            AReturnStatement returnStmt = (AReturnStatement) stmt;
            PExpression expr = returnStmt.getExpression();

            if (expr instanceof AAddExpression) {
                return "add";
            }
            if (expr instanceof AMinvalueExpression) {
                return "min";
            }
            if (expr instanceof AMaxvalueExpression) {
                return "max";
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

    // Function call: check argument types
    @Override
    public void inAFunctioncallStatement(AFunctioncallStatement node) {
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
                boolean atLeastOneNone=false;
                boolean atLeastOneString=false;
                for (String x : argumentTypes) {
                    if(!x.equals("string")){
                        allString=false;
                    }else{ 
                        atLeastOneString=true;
                    }
                    if(x.equals("none")){
                        atLeastOneNone=true;
                    }
                }
                if(!value.equals("string") && !value.equals("number") && !value.equals("unknown")){
                    
                    if(allString){
                        if(!value.equals("add")&&!value.equals("min") &&!value.equals("max")){
                            System.err.println("Error 4b: Attempting to perform operation '" + value + "' on incompatible types: string and string");
                        }else{
                            return;
                        }
                        
                    }else{
                        if(atLeastOneString){
                            System.err.println("Error 4b: Attempting to perform operation '" + value + "' on incompatible types: number and string");
                        }
                    }
                    if(value.equals("add")||value.equals("min") || value.equals("max")||value.equals("sub")||value.equals("mul") || value.equals("div") || value.equals("exp")){
                        if(atLeastOneNone){
                            System.err.println("Error 4b: Attempting to perform operation '" + value + "' with None");
                        }
                        
                    }
                }
            
            }

        }
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
            }else if (actualNode instanceof ANoneExpression){
                return "none";
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