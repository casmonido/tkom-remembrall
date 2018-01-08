package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public class ArrayNode implements Node {

	protected Object [] value;
	
	public ArrayNode(Object [] v) {
		value = v;
	}

	public ArrayNode(int num) {
		value = new Object [num];
	}
	
	@Override
	public IdentValue evalNode(Environment env) {
		return new IdentValue(value);
	}
}
