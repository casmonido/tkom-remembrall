package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class MoreThanNode extends ComparisonNode {

	public MoreThanNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (!l.getClass().isInstance(r))
			throw new RuntimeException("Porównanie między obiektami różych typów");
		if (l instanceof Long)
			if ((Long)l > (Long)r)
				return new IdentValue(true);
		return new IdentValue(false);
	}
}