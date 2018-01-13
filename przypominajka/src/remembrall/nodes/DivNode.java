package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class DivNode extends ArythmeticNode {

	public DivNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		return new TypedValue(Double.parseDouble(l.toString()) / Double.parseDouble(r.toString()));
	}
}
