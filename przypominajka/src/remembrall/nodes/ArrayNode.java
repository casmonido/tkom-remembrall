package remembrall.nodes;

import remembrall.IdentValue;

public class ArrayNode implements Node {

	protected Object [] value;
	
	public ArrayNode(Object [] v) {
		value = v;
	}

	public ArrayNode(int num) {
		value = new Object [num];
	}
	
	public ArrayNode() {}
	
	@Override
	public IdentValue evalNode() {
		return new IdentValue(value);
	}
}
