package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;

public class RepeatUntilNode implements Node {
	protected Node left;
	protected List<Node> right;
	
	public RepeatUntilNode(Node l, List<Node> r) {
		left = l;
		right = r;
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		env.addLayer();
		while ((boolean)left.evalNode(env).getValue() == false)
			for (Node r : right)
				r.evalNode(env);
		env.removeLayer();
		return null;
	}
}
