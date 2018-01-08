package remembrall.nodes;

import remembrall.Environment;
import remembrall.IdentValue;

public interface Node {

	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException;
}
