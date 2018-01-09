package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class FunctionCallNode implements Node {
	
	public FunctionDefNode func;
	public Node builtinFunc;
	public String funcName;
	public List<Node> args;
	private Environment env;

	public FunctionCallNode(String funcName, Node f, List<Node> args) {
		this.funcName = funcName;
		if (f instanceof FunctionDefNode)
			this.func = (FunctionDefNode) f;
		else
			this.builtinFunc = f;
		this.args = args;
	}

	
	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		if (builtinFunc != null)
			return builtinFunc.evalNode(env);
		Environment newEnv = new Environment();
		for (int i = 0; i < func.args.size(); i++) {
			String argName = func.args.get(i);
			Node val = args.get(i);
			try {
				newEnv.bind(argName, val.evalNode(env));
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		IdentValue obj = func.evalNode(newEnv); // kiedy vArr?
		return func.evalNode(newEnv);
	}

}
