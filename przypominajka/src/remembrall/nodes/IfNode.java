package remembrall.nodes;

import java.util.List;

import remembrall.Environment;
import remembrall.IdentValue;


public class IfNode implements Node {
	protected Node cond;
	protected List<Node>  iflist;
	protected List<Node>  elselist;
	protected Environment env;
	
	public IfNode(Node c, List<Node> ifl, List<Node> elsel, Environment e) {
		cond = c;
		iflist = ifl;
		elselist = elsel;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws Exception {
		if ((boolean)cond.evalNode().v == true)
			for (Node r : iflist)
				r.evalNode();
		else
			for (Node r : elselist)
				r.evalNode();
		return null;
	}
}
