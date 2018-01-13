package remembrall.nodes;


public class MoreEqualsNode extends ComparisonNode {

	public MoreEqualsNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected boolean immediateCompareDouble(Double ll, Double rr) {
		return ll >= rr;
	}

	@Override
	protected boolean immediateCompareInt(Integer ll, Integer rr) {
		return ll >= rr;
	}

	@Override
	protected String getOperator() {
		return ">=";
	}
}