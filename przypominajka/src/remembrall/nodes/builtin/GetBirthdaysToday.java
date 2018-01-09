package remembrall.nodes.builtin;

import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Datetime;
import remembrall.types.Weather;

public class GetBirthdaysToday implements Node {

	@Override
	public IdentValue evalNode(Environment env) throws RuntimeException {
		return new IdentValue(new String [] {"osoba1", "osoba2"});
	}

}
