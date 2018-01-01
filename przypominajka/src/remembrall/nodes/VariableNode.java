package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class VariableNode implements Node {
	protected String ident;
	protected Environment env;
	
	public VariableNode(String i, Environment e) {
		ident = i;
		env = e;
	}

	@Override
	public IdentValue evalNode() {
		if (env.resolve(ident).v != null)
			return new IdentValue(env.resolve(ident).v);
		return new IdentValue(env.resolve(ident).vArr);
	}
}
