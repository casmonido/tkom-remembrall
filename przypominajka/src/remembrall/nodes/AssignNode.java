package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class AssignNode implements Node {
	public VariableNode var;
	public Node val;

	
	public AssignNode(VariableNode var, Node val) {
		this.var = var;
		this.val = val;
	}

	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		TypedValue newVal = val.evalNode(env);
		TypedValue curVal = env.resolve(((IdentNode)var.ident).ident);
		if (curVal != null)
			if (!curVal.getType().equals(newVal.getType()))
				throw new RuntimeException("Próba przypisania wartości typu " + newVal.getType().toString() +
						" do obiektu typu " + curVal.getType().toString());
		env.bind(((IdentNode)var.ident).ident, newVal);
		return newVal;
	}

}
