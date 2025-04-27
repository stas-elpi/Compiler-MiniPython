import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor2 extends DepthFirstAdapter {
    private Hashtable<String, String> symtable;

    public myvisitor2(Hashtable<String, String> table) {
        this.symtable = table;
    }

    @Override
    public void inADefFunction(ADefFunction node){
        String fName = node.getId().getText();
        symtable.put(fName,"true");
    }

    @Override
    public void inAFunctioncallStatement(AFunctioncallStatement node) {
		String functionName = node.getId().getText();
	
		// Check if the function is declared-2
		if (!symtable.containsKey(functionName)) {
			symtable.put(functionName,"false");
        }
    }

    public void outStart(Start node) {
        for (String functionName : symtable.keySet()) {
            if (symtable.get(functionName).equals("false")) {
                System.err.println("Error: Function '" + functionName + "' is called but not declared.");
            }
        }
    }

}