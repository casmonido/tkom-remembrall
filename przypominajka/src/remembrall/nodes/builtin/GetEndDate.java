package remembrall.nodes.builtin;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Datetime;

public class GetEndDate implements Node {

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(new Datetime(
				new Long(2), new Long(10), new Long(2018)));
	}

}
