package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class ReturnNode implements Node {
	protected Node node;
	
	public ReturnNode(Node n) {
		node = n;
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		return node.evalNode(env); //whaaaaaaaa
	}

}
