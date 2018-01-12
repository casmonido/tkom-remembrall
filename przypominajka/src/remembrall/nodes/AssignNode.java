package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;

public class AssignNode implements Node {
	public VariableNode var;
	public Node val;
	private Environment env;

	
	public AssignNode(VariableNode var, Node val, Environment e) {
		this.var = var;
		this.val = val;
		this.env = e;
	}

	
	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		IdentValue value = val.evalNode(env);
		if (value.v != null)
			env.bind(((IdentNode)var.ident).ident, value.v);
		else
			env.bind(((IdentNode)var.ident).ident, value.vArr);
		return value;
	}

}
