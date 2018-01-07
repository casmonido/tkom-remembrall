package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class VariableNode implements Node {
	protected String ident;
	protected String attrib;
	protected Node numVal;
	protected Environment env;
	
	

	public VariableNode(String i, Node v, String a, Environment e) {
		ident = i;
		attrib = a;
		env = e;
		numVal = v;
	}

	@Override
	public IdentValue evalNode() {
		if (env.resolve(ident).v != null)
			return new IdentValue(env.resolve(ident).v);
		return new IdentValue(env.resolve(ident).vArr);
	}
}
