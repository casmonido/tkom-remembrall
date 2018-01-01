package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class SelfSubstractionNode implements Node {
	String ident;
	Environment env;
	
	public SelfSubstractionNode(String ident, Environment e) {
		this.ident = ident;
		this.env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception {
		Object l = env.resolve(ident);
		if (l instanceof Long) {
			env.resolve(ident).v = ((Long)env.resolve(ident).v) - 1; 
			return new IdentValue(env.resolve(ident));
		}
		else
			throw new Exception();
	}
}
