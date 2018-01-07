package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class AssignNode implements Node {
	private String ident;
	private Node num;
	private String attrib;
	private Node val;
	private Environment env;

	
	public AssignNode(String i, Node n, String a, Node v, Environment e) {
		this.ident = i;
		this.num = n;
		this.attrib = a;
		this.val = v;
		this.env = e;
	}
	
	@Override
	public IdentValue evalNode() throws Exception {
		IdentValue value = val.evalNode();
		env.bind(ident, num, attrib, value);
		return value;
	}

}
