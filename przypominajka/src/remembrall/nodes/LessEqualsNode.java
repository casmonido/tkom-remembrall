package remembrall.nodes;

import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;

public class LessEqualsNode extends ComparisonNode {

	public LessEqualsNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected boolean immediateCompareDouble(Double ll, Double rr) {
		return ll <= rr;
	}

	@Override
	protected boolean immediateCompareInt(Integer ll, Integer rr) {
		return ll <= rr;
	}

	@Override
	protected String getOperator() {
		return "<=";
	}
}
