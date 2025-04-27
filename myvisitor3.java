import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class myvisitor3 extends DepthFirstAdapter {
    private Hashtable<String, FunctionSignature> declaredFunctions;

    public myvisitor3(Hashtable<String, FunctionSignature> symtable) {
        this.declaredFunctions = symtable;
    }

    private static class FunctionSignature {
        int requiredArgs;
        int totalArgs;

        public FunctionSignature(int requiredArgs, int totalArgs) {
            this.requiredArgs = requiredArgs;
            this.totalArgs = totalArgs;
        }
		public int getRequired(){
			return this.requiredArgs;
		}
		public int getTotal(){
			return this.totalArgs;
		}
    }

    @Override
    public void inADefFunction(ADefFunction node) {
        String functionName = node.getId().getText();
        int requiredArgs = 0;
        int totalArgs = 0;
		//System.out.println(node.getArgument());
        if (!node.getArgument().isEmpty()) {
			
            AArgArgument argument = (AArgArgument) node.getArgument().getFirst();

            if (argument.getEqvalue().size()!=0) {
                totalArgs++; 
				//if for example y=2, doesn't count as req arg
            } else {
                //If eqvalue is not empty, it's a required argument
                requiredArgs++;  
                totalArgs++;    
            }



            if (argument.getNextvalue() !=null) {
                for (PNextvalue nextArg : argument.getNextvalue()) {
                    ANextvNextvalue next = (ANextvNextvalue) nextArg;
					if (next.getEqvalue().size()!=0) {
						totalArgs++; 
						
					} else {
						requiredArgs++;  
						totalArgs++;    
					}
                }
            }
			
        }

        declaredFunctions.put(functionName, new FunctionSignature(requiredArgs, totalArgs));
    }

    @Override
	public void inAFunctioncallStatement(AFunctioncallStatement node) {
		String functionName = node.getId().getText();
	
		// Retrieve function signature
		int providedArgs = 0;
		if(!node.getArgslist().isEmpty()){
			AArgslistArgslist argsList = (AArgslistArgslist) node.getArgslist().getFirst();
			String a = argsList.getL().toString();
			if (a.contains(",")) {
				String[] tabl = a.split(",");
				providedArgs += tabl.length;
			}else{
				providedArgs++;
			}
			if (!argsList.getR().isEmpty()) {
				String c = argsList.getR().toString();
				if (c.contains(",")) {
					String[] tabl2 = c.split(",");
					providedArgs += tabl2.length;
				}else{
					providedArgs++;
				}
			}

		}
		declaredFunctions.put(functionName+"1", new FunctionSignature(providedArgs, 0));

	}
	public void outStart(Start node){
				// Validate argument counts
		for (Map.Entry<String, FunctionSignature> entry : declaredFunctions.entrySet()) {
			String key = entry.getKey(); // The variable name (key)
			FunctionSignature value = entry.getValue(); // The type of the variable (value)
			for (Map.Entry<String, FunctionSignature> en : declaredFunctions.entrySet()) {
				String key1 = en.getKey(); // The variable name (key)
				FunctionSignature value1 = en.getValue(); // The type of the variable (value)
				if (key1.equals(key + "1")) {
					if (value1.getRequired() < value.getRequired()) {
						System.err.println("Error: Function '" + key + "' is missing required arguments.");
					} else if (value1.getRequired() > value.getTotal()) {
						System.err.println("Error: Function '" + key + "' has too many arguments.");
					} 
				}
			}
		}
	}
}