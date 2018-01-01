package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.IdentValue;

public class CastNode implements Node {
	protected Atom type;
	protected Node right;
	protected Environment env;
	
	public CastNode(Atom t, Node r, Environment e) {
		type = t;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception { // jak rzutowac??
		if (right.evalNode().v != null)
			return new IdentValue(right.evalNode().v); 
		return new IdentValue(right.evalNode().vArr);
	}
}
