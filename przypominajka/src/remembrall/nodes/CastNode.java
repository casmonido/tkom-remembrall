package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public class CastNode implements Node {
	protected Atom typ;
	protected Node right;
	
	public CastNode(Atom typ,  Node r) {
		this.typ = typ;
		right = r;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException { // jak rzutowac??
		return new TypedValue(right.evalNode(env).getValue(), new AtomType(typ));
		//xlsl
	}
}
