package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;

public class RepeatNode implements Node {
	public Node left;
	public List<Node> right;
	protected Environment env;
	
	public RepeatNode(Node l, List<Node> r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		while ((int)left.evalNode().v > 0)
			for (Node r : right)
				r.evalNode();
		return null;
	}
}
