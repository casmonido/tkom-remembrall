package remembrall.functions.builtin;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;

public class GetDuration implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(new Long(4));
	}

}
