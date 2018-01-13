package remembrall.nodes;

public class MoreThanNode extends ComparisonNode {

	public MoreThanNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected boolean immediateCompareDouble(Double ll, Double rr) {
		return ll > rr;
	}

	@Override
	protected boolean immediateCompareInt(Integer ll, Integer rr) {
		return ll > rr;
	}

	@Override
	protected String getOperator() {
		return ">";
	}
}