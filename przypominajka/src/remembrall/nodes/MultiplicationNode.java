package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class MultiplicationNode extends ArythmeticNode {

	public MultiplicationNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (l instanceof Long && r instanceof Long)
			return new IdentValue((Long)l + (Long)r);
		else
			return new IdentValue((Double)l + (Double)r);
	}
}
