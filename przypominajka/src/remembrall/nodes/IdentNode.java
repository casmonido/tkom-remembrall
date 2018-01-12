package remembrall.nodes;


import remembrall.Environment;
import remembrall.IdentValue;

public class IdentNode implements Node {
	public String ident;

	public IdentNode(String i) {
		ident = i;
	}

	@Override
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException { 
		IdentValue objVal = env.resolve(ident);
		return new IdentValue(objVal);
	}
}
