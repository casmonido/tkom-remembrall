package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public abstract class AssignSelfNode implements Node {
	public VariableNode var;
	public Node val;

	public AssignSelfNode(VariableNode var, Node val) {
		this.var = var;
		this.val = val;
	}

	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		TypedValue value = val.evalNode(env);
		TypedValue existing = env.resolve(((IdentNode)var.ident).ident);
		if (existing == null)
			throw new RuntimeException("Zmienna " + ((IdentNode)var.ident).ident + " nie została zainicjalizowana!");
		if (value.getType().equals(existing.getType()))
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() + " między obiektami różnych typów: " +
					value.getType().toString() + " i " + existing.getType().toString());
		if (!(value.getType() instanceof AtomType))
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
					" do obiektów typu " + value.getType().toString());
		TypedValue newVal = null;
		switch (((AtomType)value.getType()).getAtom()) {
		case typeInt:
			newVal = new TypedValue(
						immediateEvalInt((Integer) value.getValue(), (Integer) existing.getValue()), 
						new AtomType(Atom.typeInt));
			break;
		case typeDouble:
			newVal = new TypedValue(
						immediateEvalDouble((Double) value.getValue(), (Double) existing.getValue()),
						new AtomType(Atom.typeDouble));
			break;
		case typeString:
			newVal = new TypedValue(
						immediateEvalString((String) value.getValue(), (String) existing.getValue()),
						new AtomType(Atom.typeString));
			break;
		default:
			throw new RuntimeException("Próba zastosowania operatora " + getOperator() + " do obiektów typu " +
					value.getType().toString());
		}
		env.bind(((IdentNode)var.ident).ident, newVal);
		return newVal;
	}
	
	protected abstract Double immediateEvalDouble(Double ll, Double rr);
	protected abstract Integer immediateEvalInt(Integer ll, Integer rr);
	protected String immediateEvalString(String ll, String rr) throws RuntimeException {
		throw new RuntimeException("Próba zastosowania operatora " + getOperator() +
				" do obiektów typu string");
	}
	protected abstract String getOperator();
}