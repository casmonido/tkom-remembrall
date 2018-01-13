package remembrall.nodes;

import java.util.LinkedList;
import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.tokens.Token;
import remembrall.types.ArrayType;
import remembrall.types.AtomType;
import remembrall.types.Type;

public class ArrayNode implements Node {

	protected Node [] value;
	
	public ArrayNode(List<Node> list) {
		value = list.toArray(new Node [list.size()]);
	}

	public ArrayNode(int num) {
		value = new Node [num];
	}
	
	@Override
	public TypedValue evalNode(Environment env) {
		return new TypedValue(value, ArrayType.type);
	}

}
