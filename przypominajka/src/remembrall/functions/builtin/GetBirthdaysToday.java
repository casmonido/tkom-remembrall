package remembrall.functions.builtin;


import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.ArrayType;


public class GetBirthdaysToday implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(new String [] {"osoba1", "osoba2"}, ArrayType.type);
	}

}
