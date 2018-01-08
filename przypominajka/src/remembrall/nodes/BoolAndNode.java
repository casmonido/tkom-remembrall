package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class BoolAndNode implements Node {

	protected Node left;
	protected Node right;
	protected Environment env;
	
	public BoolAndNode(Node l, Node r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws RuntimeException {
		return new IdentValue(((boolean) left.evalNode().v) &&
				((boolean) right.evalNode().v));
	}

}
