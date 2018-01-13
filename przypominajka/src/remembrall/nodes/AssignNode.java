package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
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
	public TypedValue evalNode(Environment env) throws RuntimeException {
		TypedValue value = val.evalNode(env);
		env.bind(((IdentNode)var.ident).ident, value);
		return value;
	}

}
