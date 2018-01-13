package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public class BoolNotNode implements Node {
	protected Node left;
	
	public BoolNotNode(Node l) {
		left = l;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(!((boolean) left.evalNode(env).getValue()), new AtomType(Atom.typeBool));
	}
}
