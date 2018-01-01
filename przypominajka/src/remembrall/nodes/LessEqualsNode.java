package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class LessEqualsNode extends ComparisonNode {

	public LessEqualsNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode() throws Exception {
		Object l = left.evalNode().v;
		Object r = right.evalNode().v;
		if (!l.getClass().isInstance(r.getClass()))
			throw new Exception();
		if (l instanceof Long)
			if ((Long)l <= (Long)r)
				return new IdentValue(true);
		return new IdentValue(false);
	}
}
