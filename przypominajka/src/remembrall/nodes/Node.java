package remembrall.nodes;

import remembrall.IdentValue;

public interface Node {

	public IdentValue evalNode() throws remembrall.exceptions.RuntimeException;
}
