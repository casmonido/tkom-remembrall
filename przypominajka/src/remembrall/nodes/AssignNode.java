package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class AssignNode implements Node {
	private VariableNode var;
	private Node val;
	private Environment env;

	
	public AssignNode(VariableNode var, Node val, Environment e) {
		this.var = var;
		this.val = val;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode() throws RuntimeException {
		IdentValue value = val.evalNode();
		if (value.v != null)
			env.bind(var.ident, value.v);
		else
			env.bind(var.ident, value.vArr);
		return value;
	}

}
