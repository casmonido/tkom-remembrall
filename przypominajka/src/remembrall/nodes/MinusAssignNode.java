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
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		IdentValue value = val.evalNode();
		if (value.v instanceof Long)
			env.bind(var.ident, (Long) value.v - (Long) env.resolve(var.ident).v);
		else
			if (value.v instanceof Double)
				env.bind(var.ident, (Double) value.v - (Double) env.resolve(var.ident).v);
			else
				throw new remembrall.exceptions.RuntimeException("Operator -= zastosowany do obiektów złego typu");
		return value;
	}


}
