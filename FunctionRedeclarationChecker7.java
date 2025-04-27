import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class FunctionRedeclarationChecker7 extends DepthFirstAdapter {
    private Hashtable<String, List<Integer>> functionArguments;

    public FunctionRedeclarationChecker7(Hashtable<String, List<Integer>> table) {
        this.functionArguments = table;
    }
    // 7. Same function name and same number of arguments
    @Override
    public void inADefFunction(ADefFunction node) {
        String functionName = node.getId().getText();

        int nonDefaultArguments = 0;
        int totalArgs = 0;
        if (!node.getArgument().isEmpty()) {
            AArgArgument argument = (AArgArgument) node.getArgument().getFirst();

            if (argument.getEqvalue().size() != 0) {
                totalArgs++;
            } else {
                nonDefaultArguments++;
                totalArgs++;
            }

            if (argument.getNextvalue() != null) {
                for (PNextvalue arg : argument.getNextvalue()) {
                    ANextvNextvalue next = (ANextvNextvalue) arg;
                    if (next.getEqvalue().size() != 0) {
                        totalArgs++;
                    } else {
                        nonDefaultArguments++;
                        totalArgs++;
                    }
                }

            }
        }

        if (functionArguments.containsKey(functionName)) {
            for (int existingCount : functionArguments.get(functionName)) {
                if (existingCount == totalArgs || existingCount == nonDefaultArguments) {
                    System.err.println("Error: Function '" + functionName + "' is redeclared with the same number of arguments");
                    return;
                }
            }
        } else {
            functionArguments.put(functionName, new ArrayList<>());
        }

        functionArguments.get(functionName).add(totalArgs);
        functionArguments.get(functionName).add(nonDefaultArguments);
    }

}
