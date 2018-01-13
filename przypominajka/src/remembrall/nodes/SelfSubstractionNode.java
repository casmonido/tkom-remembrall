package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class SelfSubstractionNode implements Node {
	public VariableNode var;
	Environment env;
	
	public SelfSubstractionNode(VariableNode var, Environment e) {
		this.var = var;
		this.env = e;
	}
	
	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		Object l = env.resolve(((IdentNode)var.ident).ident).v;
		if (l instanceof Long || l instanceof Integer) {
			env.bind(((IdentNode)var.ident).ident, 
					new TypedValue(((Long)env.resolve(((IdentNode)var.ident).ident).v) - 1)); 
			return new TypedValue(env.resolve(((IdentNode)var.ident).ident));
		}
		if (l instanceof Double) {
			env.bind(((IdentNode)var.ident).ident, 
					new TypedValue(((Double)env.resolve(((IdentNode)var.ident).ident).v) - 1)); 
			return new TypedValue(env.resolve(((IdentNode)var.ident).ident));
		}
		else
			throw new RuntimeException
				("Operator '--' użyty na obiekcie o niewspieranym typie");
	}
}
