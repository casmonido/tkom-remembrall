package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class CastNode implements Node {
	protected Atom type;
	protected Node right;
	protected boolean array;
	protected Environment env;
	
	public CastNode(Atom t, boolean a, Node r, Environment e) {
		type = t;
		right = r;
		array = a;
		env = e;
	}

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException { // jak rzutowac??
		if (right.evalNode(env).v != null)
			return new IdentValue(right.evalNode(env).v); 
		return new IdentValue(right.evalNode(env).vArr);
	}
}
