package remembrall.nodes;

import remembrall.Environment;

public abstract class ComparisonNode implements Node {
	protected Node left;
	protected Node right;
	protected Environment env;
	
	public ComparisonNode(Node l, Node r, Environment e) {
		left = l;
		right = r;
		env = e;
	}
}
