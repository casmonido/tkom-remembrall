package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class BoolAndNode implements Node {

	public Node left;
	public Node right;
	protected Environment env;
	
	public BoolAndNode(Node l, Node r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(((boolean) left.evalNode(env).v) &&
				((boolean) right.evalNode(env).v));
	}

}
