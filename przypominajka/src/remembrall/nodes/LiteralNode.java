package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public class LiteralNode implements Node {

	public Object value;
	
	public LiteralNode(Object v) {
		value = v;
	}

	@Override
	public TypedValue evalNode(Environment env) {
		return new TypedValue(value);
	}
}
