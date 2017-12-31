package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class AssignExpNode implements Node {
	protected Node left;
	protected Node right;
	protected Environment env;
	
	public AssignExpNode(Node l, Node r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() {
		env.bind((String)left.evalNode().v, right.evalNode().v);
		return right.evalNode();
	}

}
