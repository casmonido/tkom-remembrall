package remembrall.nodes.builtin;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;

public class GetDuration implements Node {

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(new Long(4));
	}

}
