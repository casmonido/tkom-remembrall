package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public class BoolAndNode implements Node {

	public Node left;
	public Node right;
	
	public BoolAndNode(Node l, Node r) {
		left = l;
		right = r;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(((boolean) left.evalNode(env).getValue()) &&
				((boolean) right.evalNode(env).getValue()), new AtomType(Atom.typeBool));
	}

}
