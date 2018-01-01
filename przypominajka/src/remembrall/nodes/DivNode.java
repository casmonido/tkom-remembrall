package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class DivNode extends ArythmeticNode {

	public DivNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode() throws Exception {
		Object l = left.evalNode().v;
		Object r = right.evalNode().v;
		return new IdentValue((Double)l + (Double)r);
	}
}
