package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class ReturnNode implements Node {
	protected Node node;
	protected Environment env;
	
	public ReturnNode(Node n, Environment e) {
		node = n;
		env = e;
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		return node.evalNode(env); //whaaaaaaaa
	}

}
