package remembrall.nodes;

public class DivNode extends ArythmeticNode {

	public DivNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll/rr;
	}

	@Override
	protected Integer immediateEvalInt(Integer ll, Integer rr) {
		return ll/rr;
	}

	@Override
	protected String getOperator() {
		return "/";
	}
}
