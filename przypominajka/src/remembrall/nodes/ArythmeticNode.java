package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public abstract class ArythmeticNode implements Node {
	public Node left;
	public Node right;
	
	public ArythmeticNode(Node l, Node r) {
		left = l;
		right = r;
	}
	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		TypedValue l = left.evalNode(env);
		TypedValue r = right.evalNode(env);
		if (!l.getType().equals(r.getType()))
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
					" między obiektami różnych typów: " +
					l.getType().toString() + " i " + r.getType().toString());
		if (!(l.getType() instanceof AtomType))
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
					" do obiektów typu " + l.getType().toString());
		switch (((AtomType)l.getType()).getAtom()) {
		case typeInt:
			return new TypedValue(
					immediateEvalInt((Integer)l.getValue(), (Integer)r.getValue()),
					new AtomType(Atom.typeInt));
		case typeDouble:
			if (l.getValue() instanceof Double)
				return new TypedValue(
						immediateEvalDouble((Double) l.getValue(), (Double) r.getValue()), 
						new AtomType(Atom.typeDouble));
		case typeString:
			if (l.getValue() instanceof String)
				return new TypedValue(
						immediateEvalString((String) l.getValue(), (String) r.getValue()), 
						new AtomType(Atom.typeString));
		default:
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
					" do obiektów typu " + l.getType().toString());
		}
	}
	
	protected abstract Double immediateEvalDouble(Double ll, Double rr);
	protected abstract Integer immediateEvalInt(Integer ll, Integer rr);
	protected String immediateEvalString(String ll, String rr) throws RuntimeException {
		throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
				" do obiektów typu string");
	}
	protected abstract String getOperator();
}
