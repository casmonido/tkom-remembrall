package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;


public class IfNode implements Node {
	protected Node cond;
	protected List<Node>  iflist;
	protected List<Node>  elselist;
	
	public IfNode(Node c, List<Node> il, List<Node> el) {
		cond = c;
		iflist = il;
		elselist = el;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		if ((boolean)cond.evalNode(env).getValue() == true) 
		{
			env.addLayer();
			for (Node r : iflist)
				r.evalNode(env);
			env.removeLayer();
		}
		else
		{
			env.addLayer();
			for (Node r : elselist)
				r.evalNode(env);
			env.removeLayer();
		}
		return null;
	}
}
