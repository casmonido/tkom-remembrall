package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class MoreEqualsNode extends ComparisonNode {

	public MoreEqualsNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode().v;
		Object r = right.evalNode().v;
		if (!l.getClass().isInstance(r.getClass()))
			throw new RuntimeException("Porównanie między obiektami różych typów");
		if (l instanceof Long)
			if ((Long)l >= (Long)r)
				return new IdentValue(true);
		return new IdentValue(false);
	}
}