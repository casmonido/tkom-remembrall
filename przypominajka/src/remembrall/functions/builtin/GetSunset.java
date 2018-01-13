package remembrall.functions.builtin;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Time;
import remembrall.types.AtomType;

public class GetSunset implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(new Time(new Long(2), new Long(10)), new AtomType(Atom.typeTime));
	}

}
