package remembrall.nodes;

import remembrall.IdentValue;

public class LiteralNode implements Node {

	protected Object value;
	
	public LiteralNode(Object v) {
		value = v;
	}

	@Override
	public IdentValue evalNode() {
		return new IdentValue(value);
	}
}
