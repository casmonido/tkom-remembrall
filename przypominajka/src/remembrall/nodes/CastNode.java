package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.tokens.Token;
import remembrall.types.Type;

public class CastNode implements Node {
	protected Token typ;
	protected Node right;
	protected Environment env;
	
	public CastNode(Token typ,  Node r, Environment e) {
		typ = typ;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException { // jak rzutowac??
		if (right.evalNode(env).v != null)
			return new IdentValue(right.evalNode(env).v); 
		return new IdentValue(right.evalNode(env).vArr);
	}
}
