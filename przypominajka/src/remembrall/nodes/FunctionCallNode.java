package remembrall.nodes;

import java.util.List;

import remembrall.IdentValue;

public class FunctionCallNode implements Node {
	
	public Node functions;
	public String funcStr;
	public List<Node> args;

	public FunctionCallNode(Node f, List<Node> args) {
		functions = f;
		this.args = args;
	}

	public FunctionCallNode(String string, List<Node> args) {
		funcStr = string;
		this.args = args;
	}

	
	@Override
	public IdentValue evalNode() {
		// TODO Auto-generated method stub
		return null;
	}

}
