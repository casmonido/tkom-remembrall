package remembrall.nodes;

import remembrall.IdentValue;

public class LiteralNode implements Node {

	protected Object value;
	protected Object type;
	
	public LiteralNode(Object v, Object t) {
		value = v;
		type = t;
	}

	@Override
	public IdentValue evalNode() {
		return new IdentValue(value);
	}
}
