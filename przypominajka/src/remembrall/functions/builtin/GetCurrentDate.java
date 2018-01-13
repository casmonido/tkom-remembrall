package remembrall.functions.builtin;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.Node;
import remembrall.types.Datetime;
import remembrall.types.Time;
import remembrall.types.AtomType;

public class GetCurrentDate implements Node {

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return new TypedValue(new Datetime(
				new Long(2), new Long(10), new Long(2018)), new AtomType(Atom.typeDatetime));
	}


}
