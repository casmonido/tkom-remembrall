package remembrall.nodes;


public class LessThanNode extends ComparisonNode {

	public LessThanNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected boolean immediateCompareDouble(Double ll, Double rr) {
		return ll < rr;
	}

	@Override
	protected boolean immediateCompareInt(Integer ll, Integer rr) {
		return ll < rr;
	}

	@Override
	protected String getOperator() {
		return "<";
	}
}
