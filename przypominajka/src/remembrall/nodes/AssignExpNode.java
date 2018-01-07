package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class AssignExpNode implements Node {
	String ident;
	String attr;
	Node numNode;
	Node valNode;
	Environment env;
	
	public AssignExpNode(String ident, Node n, String attr, Node val, Environment e) {
		this.ident = ident;
		this.attr = attr;
		this.valNode = val;
		this.numNode = n;
		this.env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception {
		env.bind((String)left.evalNode().v, right.evalNode().v);
		return right.evalNode();
	}

}
