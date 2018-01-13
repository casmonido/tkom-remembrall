package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;

public class StartNode implements Node {
	public List<Node> earlyAssign;
	public Node left;
	public List<Node> right;
	
	public StartNode(List<Node> asgn, Node l, List<Node> r) {
		left = l;
		right = r;
		earlyAssign = asgn;
	}

	
	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		for (Node r : earlyAssign)
			r.evalNode(env);
		if ((boolean)left.evalNode(env).getValue() == true)
			for (Node r : right)
				r.evalNode(env);
		return null;
	}

}
