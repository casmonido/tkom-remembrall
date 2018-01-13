package remembrall.nodes;

import java.util.LinkedList;
import java.util.List;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.tokens.Token;

public class ArrayNode implements Node {

	protected List<Token> value;
	
	public ArrayNode(List<Token> list) {
		value = list;
	}

	public ArrayNode(int num) {
		value = new LinkedList<Token>();
	}
	
	@Override
	public TypedValue evalNode(Environment env) {
		return new TypedValue(value);
	}
}
