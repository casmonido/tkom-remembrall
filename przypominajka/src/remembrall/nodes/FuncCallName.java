package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;

public class FuncCallName implements Node {
	protected String ident;
	protected List<Node> args;
	protected Environment env;
	
	public FuncCallName(String i, List<Node> a, Environment e) {
		ident = i;
		args = a;
		env = e;
	}

	@Override
	public IdentValue evalNode() {
		//if (env.resolve(ident).v != null)
		return null;
		//return new IdentValue(env.resolve(ident).vArr);
	}
}
