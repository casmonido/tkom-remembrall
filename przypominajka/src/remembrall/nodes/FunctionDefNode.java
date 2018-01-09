package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class FunctionDefNode implements Node {
	public List<String> args; 
	public List<Node> body;

	public FunctionDefNode(List<String> args, List<Node> body) {
		this.args = args;
		this.body = body;
	}
	
	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		for (Node n: body) {
			if (n instanceof ReturnNode)
				return n.evalNode(env);
			n.evalNode(env);
		}
		return null;
	}

}
