package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class SelfAdditionNode implements Node {
	public VariableNode var;
	Environment env;
	
	public SelfAdditionNode(VariableNode var, Environment e) {
		this.var = var;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode() throws RuntimeException {
		Object l = env.resolve(var.ident);
		if (l instanceof Long || l instanceof Double) {
			env.resolve(var.ident).v = ((Long)env.resolve(var.ident).v) + 1; 
			return new IdentValue(env.resolve(var.ident));
		}
		else
			throw new RuntimeException
				("Operator '++' użyty na obiekcie o niewspieranym typie");
	}
}
