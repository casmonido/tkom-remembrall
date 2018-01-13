package remembrall.functions.builtin;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.AtomType;
import remembrall.types.VoidType;
import remembrall.types.Weather;

public class SendSMS implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(null, VoidType.type);
	}
	
}
