package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;

public class StartNode implements Node {
	public List<Node> earlyAssign;
	public Node left;
	public List<Node> right;
	protected Environment env;
	
	public StartNode(List<Node> asgn, Node l, List<Node> r, Environment e) {
		left = l;
		right = r;
		env = e;
		earlyAssign = asgn;
	}

	
	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		for (Node r : earlyAssign)
			r.evalNode(env);
		if ((boolean)left.evalNode(env).v == true)
			for (Node r : right)
				r.evalNode(env);
		return null;
	}

}
