package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class EqualsNode extends ComparisonNode {

	public EqualsNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (!l.getClass().isInstance(r))
			return new TypedValue(false);
		if (l.equals(r))
			return new TypedValue(true);
		return new TypedValue(false);
	}

}
