package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class BoolNotNode implements Node {
	protected Node left;
	protected Environment env;
	
	public BoolNotNode(Node l, Environment e) {
		left = l;
		env = e;
	}

	@Override
	public IdentValue evalNode() {
		return new IdentValue(!((boolean) left.evalNode().v));
	}
}
