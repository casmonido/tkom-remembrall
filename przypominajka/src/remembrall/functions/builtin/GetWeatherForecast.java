package remembrall.functions.builtin;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Time;
import remembrall.types.Weather;

public class GetWeatherForecast implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(new Weather());
	}

}
