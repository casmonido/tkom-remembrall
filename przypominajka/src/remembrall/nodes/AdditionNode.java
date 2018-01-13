package remembrall.nodes;


public class AdditionNode extends ArythmeticNode {

	public AdditionNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll+rr;
	}

	@Override
	protected Long immediateEvalLong(Long ll, Long rr) {
		return ll+rr;
	}

	@Override
	protected String immediateEvalString(String ll, String rr) {
		return ll+rr;
	}

	@Override
	protected String getOperator() {
		return "+";
	}


}
