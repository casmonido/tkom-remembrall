package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class BoolOrNode implements Node {

	protected Node left;
	protected Node right;
	protected Environment env;
	
	public BoolOrNode(Node l, Node r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(((boolean) left.evalNode(env).v) ||
				((boolean) right.evalNode(env).v));
	}

}
