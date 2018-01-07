package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class MinusAssignNode implements Node {
	String ident;
	String attr;
	Node numNode;
	Node valNode;
	Environment env;
	
	public MinusAssignNode(String ident, Node n, String attr, Node val, Environment e) {
		this.ident = ident;
		this.attr = attr;
		this.valNode = val;
		this.numNode = n;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
