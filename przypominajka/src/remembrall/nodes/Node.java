package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;

public interface Node {

	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException;
}
