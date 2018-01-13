package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;

public class SubstractionNode extends ArythmeticNode {

	public SubstractionNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll-rr;
	}

	@Override
	protected Long immediateEvalLong(Long ll, Long rr) {
		return ll-rr;
	}

	@Override
	protected String getOperator() {
		return "-";
	}
}
