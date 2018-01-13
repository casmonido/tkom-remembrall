package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class NotEqualNode extends ComparisonNode {

	public NotEqualNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (!l.getClass().isInstance(r))
			return new TypedValue(false);
		if (l != r)
			return new TypedValue(true);
		return new TypedValue(false);
	}

}