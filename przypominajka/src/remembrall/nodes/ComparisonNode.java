package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public abstract class ComparisonNode implements Node {
	public Node left;
	public Node right;
	
	public ComparisonNode(Node l, Node r) {
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
					immediateCompareInt((Integer)l.getValue(), (Integer)r.getValue()),
					new AtomType(Atom.typeBool));
		case typeDouble:
			if (l.getValue() instanceof Double)
				return new TypedValue(
						immediateCompareDouble((Double) l.getValue(), (Double) r.getValue()), 
						new AtomType(Atom.typeBool));
		default:
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
					" do obiektów typu " + l.getType().toString());
		}
	}
	
	
	protected abstract boolean immediateCompareDouble(Double ll, Double rr);
	protected abstract boolean immediateCompareInt(Integer ll, Integer rr);
	protected abstract String getOperator();
}
