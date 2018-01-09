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
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException {
		int num;
		if (left.evalNode(env).v instanceof Long)
			num = ((Long)left.evalNode(env).v).intValue();
		else
			num = (int)left.evalNode(env).v;
		env.addLayer();
		for (int i = num; i >= 0; i--) {
			for (Node r : right)
				r.evalNode(env);
		}
		env.removeLayer();
		return null;
	}
}
