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
		Object l = env.resolve(((IdentNode)var.ident).ident).v;
		if (l instanceof Long || l instanceof Integer) {
			env.bind(((IdentNode)var.ident).ident, ((Long)env.resolve(((IdentNode)var.ident).ident).v) - 1); 
			return new IdentValue(env.resolve(((IdentNode)var.ident).ident));
		}
		if (l instanceof Double) {
			env.bind(((IdentNode)var.ident).ident, ((Double)env.resolve(((IdentNode)var.ident).ident).v) - 1); 
			return new IdentValue(env.resolve(((IdentNode)var.ident).ident));
		}
		else
			throw new RuntimeException
				("Operator '--' użyty na obiekcie o niewspieranym typie");
	}
}
