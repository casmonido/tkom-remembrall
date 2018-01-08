package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class LiteralNode implements Node {

	public Object value;
	
	public LiteralNode(Object v) {
		value = v;
	}

	@Override
	public IdentValue evalNode(Environment env) {
		return new IdentValue(value);
	}
}
