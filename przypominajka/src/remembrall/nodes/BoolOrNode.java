package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
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
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(((boolean) left.evalNode(env).v) ||
				((boolean) right.evalNode(env).v));
	}

}
