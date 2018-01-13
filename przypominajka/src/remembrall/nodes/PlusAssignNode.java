package remembrall.nodes;


public class PlusAssignNode extends AssignSelfNode {

	public PlusAssignNode(VariableNode c, Node val) {
		super(c, val);
	}

	@Override
	protected Double immediateEvalDouble(Double ll, Double rr) {
		return ll + rr;
	}

	@Override
	protected Integer immediateEvalInt(Integer ll, Integer rr) {
		return ll + rr;
	}

	@Override
	protected String getOperator() {
		return "+=";
	}

}
