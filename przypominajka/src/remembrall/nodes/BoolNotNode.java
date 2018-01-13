package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class BoolNotNode implements Node {
	protected Node left;
	protected Environment env;
	
	public BoolNotNode(Node l, Environment e) {
		left = l;
		env = e;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(!((boolean) left.evalNode(env).v));
	}
}
