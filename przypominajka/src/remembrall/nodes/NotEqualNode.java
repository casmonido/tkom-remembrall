package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class NotEqualNode extends ComparisonNode {

	public NotEqualNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode().v;
		Object r = right.evalNode().v;
		if (!l.getClass().isInstance(r.getClass()))
			return new IdentValue(false);
		if (l != r)
			return new IdentValue(true);
		return new IdentValue(false);
	}

}