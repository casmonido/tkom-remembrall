package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class SelfSubstractionNode implements Node {
	public VariableNode var;
	Environment env;
	
	public SelfSubstractionNode(VariableNode var, Environment e) {
		this.var = var;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		Object l = env.resolve(var.ident).v;
		if (l instanceof Long || l instanceof Integer) {
			env.bind(var.ident, ((Long)env.resolve(var.ident).v) - 1); 
			return new IdentValue(env.resolve(var.ident));
		}
		if (l instanceof Double) {
			env.bind(var.ident, ((Double)env.resolve(var.ident).v) - 1); 
			return new IdentValue(env.resolve(var.ident));
		}
		else
			throw new RuntimeException
				("Operator '--' użyty na obiekcie o niewspieranym typie");
	}
}
