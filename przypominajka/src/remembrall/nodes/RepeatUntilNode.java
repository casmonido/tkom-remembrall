package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;

public class RepeatUntilNode implements Node {
	protected Node left;
	protected List<Node> right;
	protected Environment env;
	
	public RepeatUntilNode(Node l, List<Node> r, Environment e) {
		left = l;
		right = r;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException {
		while ((boolean)left.evalNode().v == true)
			for (Node r : right)
				r.evalNode();
		return null;
	}
}