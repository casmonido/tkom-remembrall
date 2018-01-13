package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class DivNode extends ArythmeticNode {

	public DivNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll/rr;
	}

	@Override
	protected Long immediateEvalLong(Long ll, Long rr) {
		return ll/rr;
	}

	@Override
	protected String getOperator() {
		return "/";
	}
}
