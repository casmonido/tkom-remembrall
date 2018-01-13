package remembrall.nodes;

public class UnarySubstractionNode extends UnaryOperatorNode {

	public UnarySubstractionNode(VariableNode var) {
		super(var);
	}

	@Override
	protected Double immediateEvalDouble(Double ll) {
		return ll - 1;
	}

	@Override
	protected Integer immediateEvalInt(Integer ll) {
		return ll - 1;
	}

	@Override
	protected String getOperator() {
		return "--";
	}

}
