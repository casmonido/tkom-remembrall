package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class MultiplicationNode extends ArythmeticNode {

	public MultiplicationNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll*rr;
	}

	@Override
	protected Long immediateEvalLong(Long ll, Long rr) {
		return ll*rr;
	}

	@Override
	protected String getOperator() {
		return "*";
	}
}
