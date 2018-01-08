package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class StartNode implements Node {
	public Node [] earlyAssign;
	public Node left;
	public Node [] right;
	protected Environment env;
	
	public StartNode(Node [] asgn, Node l, Node [] r, Environment e) {
		left = l;
		right = r;
		env = e;
		earlyAssign = asgn;
	}

	
	@Override
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		for (Node r : earlyAssign)
			r.evalNode();
		if ((boolean)left.evalNode().v == true)
			for (Node r : right)
				r.evalNode();
		return null;
	}

}
