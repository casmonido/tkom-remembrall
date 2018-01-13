package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class LessThanNode extends ComparisonNode {

	public LessThanNode(Node l, Node r, Environment e) {
		super(l, r, e);
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		Object l = left.evalNode(env).v;
		Object r = right.evalNode(env).v;
		if (!l.getClass().isInstance(r))
			throw new RuntimeException("Porównanie między obiektami różych typów");
		if (l instanceof Long)
			if ((Long)l < (Long)r)
				return new TypedValue(true);
		return new TypedValue(false);
	}
}
