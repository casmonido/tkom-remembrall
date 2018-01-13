package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
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
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(((boolean) left.evalNode(env).v) &&
				((boolean) right.evalNode(env).v));
	}

}
