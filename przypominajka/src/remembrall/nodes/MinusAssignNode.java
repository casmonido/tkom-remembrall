package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class MinusAssignNode implements Node {
	private VariableNode var;
	private Node val;
	private Environment env;

	
	public MinusAssignNode(VariableNode c, Node val, Environment e) {
		this.var = c;
		this.val = val;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		IdentValue value = val.evalNode(env);
		if (value.v instanceof Long)
			env.bind(((IdentNode)var.ident).ident, (Long) value.v - (Long) env.resolve(((IdentNode)var.ident).ident).v);
		else
			if (value.v instanceof Double)
				env.bind(((IdentNode)var.ident).ident, (Double) value.v - (Double) env.resolve(((IdentNode)var.ident).ident).v);
			else
				throw new remembrall.exceptions.RuntimeException("Operator -= zastosowany do obiektów złego typu");
		return value;
	}


}
