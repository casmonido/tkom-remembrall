package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedIdent;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;


public class FunctionCallNode implements Node {
	
	public FunctionDefNode func;
	public Node builtinFunc;
	public String funcName;
	public List<Node> args;

	public FunctionCallNode(String funcName, Node f, List<Node> args) {
		this.funcName = funcName;
		if (f instanceof FunctionDefNode)
			this.func = (FunctionDefNode) f;
		else
			this.builtinFunc = f;
		this.args = args;
	}

	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		if (builtinFunc != null)
			return builtinFunc.evalNode(env);
		Environment newEnv = new Environment();
		for (int i = 0; i < func.args.size(); i++) {
			TypedIdent arg = func.args.get(i);
			TypedValue val = args.get(i).evalNode(env);
			if (!arg.getType().equals(val.getType()))
				throw new RuntimeException("Na pozycji " + i + 
						" funkcja " + func.getName() + 
						" oczekuje argumentu o typie " + val.getType().toString());
			newEnv.bind(arg.getName(), val);
		}
		return func.evalNode(newEnv);
	}

}
