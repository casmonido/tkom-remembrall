package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
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
	public TypedValue evalNode(Environment env) throws RuntimeException { // jak rzutowac??
		return new TypedValue(right.evalNode(env).v);
	}
}
