package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.types.AtomType;
import remembrall.types.Type;

public interface Node {

	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException;
	
}
