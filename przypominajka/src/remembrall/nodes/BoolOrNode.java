package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

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
	public IdentValue evalNode() {
		return new IdentValue(((boolean) left.evalNode().v) ||
				((boolean) right.evalNode().v));
	}

}
