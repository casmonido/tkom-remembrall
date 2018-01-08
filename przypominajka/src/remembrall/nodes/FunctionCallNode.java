package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class FunctionCallNode implements Node {
	
	public FunctionDefNode func;
	public String funcName;
	public List<Node> args;
	private Environment env;

	public FunctionCallNode(FunctionDefNode f, List<Node> args) {
		this.func = f;
		this.args = args;
	}

	public FunctionCallNode(String funcName, List<Node> args) {
		this.funcName = funcName;
		this.args = args;
	}

	
	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
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
		for (Node n: args) {
			if (n instanceof ReturnNode)
				return n.evalNode(newEnv);
			n.evalNode(newEnv);
		}
		return null;
	}

}
