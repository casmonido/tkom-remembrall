package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class StartNode implements Node {
	protected Node left;
	protected Node [] right;
	protected Environment env;
	
	public StartNode(Node l, Node [] r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception {
		if ((boolean)left.evalNode().v == true)
			for (Node r : right)
				r.evalNode();
		return null;
	}

}
