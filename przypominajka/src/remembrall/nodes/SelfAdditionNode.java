package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class SelfAdditionNode implements Node {
	String ident;
	String attr;
	Node valNode;
	Environment env;
	
	public SelfAdditionNode(String ident, Node n, String attr, Environment e) {
		this.ident = ident;
		this.attr = attr;
		this.valNode = n;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode() throws RuntimeException {
		Object l = env.resolve(ident);
		if (l instanceof Long || l instanceof Double) {
			env.resolve(ident).v = ((Long)env.resolve(ident).v) + 1; 
			return new IdentValue(env.resolve(ident));
		}
		else
			throw new RuntimeException
				("Operator '++' użyty na obiekcie o niewspieranym typie");
	}
}
