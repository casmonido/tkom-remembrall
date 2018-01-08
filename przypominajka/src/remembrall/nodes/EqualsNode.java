package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class EqualsNode extends ComparisonNode {

	public EqualsNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (!l.getClass().isInstance(r.getClass()))
			return new IdentValue(false);
		if (l == r)
			return new IdentValue(true);
		return new IdentValue(false);
	}

}
