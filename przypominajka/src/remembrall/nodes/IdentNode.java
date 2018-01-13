package remembrall.nodes;


import remembrall.Environment;
import remembrall.TypedValue;

public class IdentNode implements Node {
	public String ident;

	public IdentNode(String i) {
		ident = i;
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException { 
		return env.resolve(ident);
	}
}
