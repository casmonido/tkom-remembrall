package remembrall.nodes.builtin;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Time;

public class GetSunset implements Node {

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(new Time(new Long(2), new Long(10)));
	}

}
