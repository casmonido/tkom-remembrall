package remembrall.nodes;


public class SubstractionNode extends ArythmeticNode {

	public SubstractionNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll-rr;
	}

	@Override
	protected Integer immediateEvalInt(Integer ll, Integer rr) {
		return ll-rr;
	}

	@Override
	protected String getOperator() {
		return "-";
	}
}
