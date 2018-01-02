package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class ReturnNode implements Node {
	protected Node node;
	protected Environment env;
	
	public ReturnNode(Node n, Environment e) {
		node = n;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception {
		return node.evalNode(); //whaaaaaaaa
	}

}
