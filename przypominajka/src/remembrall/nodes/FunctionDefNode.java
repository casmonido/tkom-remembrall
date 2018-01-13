package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedIdent;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.Type;

public class FunctionDefNode implements Node {
	private Type retType;
	public List<TypedIdent> args; 
	public List<Node> body;
	private String name;

	public FunctionDefNode(Type retType, String name, List<TypedIdent> args, List<Node> body) {
		this.retType = retType;
		this.name = name;
		this.args = args;
		this.body = body;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		for (Node n: body) {
			if (n instanceof ReturnNode)
				return n.evalNode(env);
			n.evalNode(env);
		}
		return null;
	}

	public Type getType() {
		return retType;
	}

}
