package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class AdditionNode extends ArythmeticNode {

	public AdditionNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (l instanceof Long && r instanceof Long)
			return new TypedValue((Long)l + (Long)r);
		else
			return new TypedValue((Double)l + (Double)r);
	}
}
