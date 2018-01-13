package remembrall.nodes;


public class UnaryAdditionNode extends UnaryOperatorNode {
	
	public UnaryAdditionNode(VariableNode var) {
		super(var);
	}
	
	@Override
	protected Double immediateEvalDouble(Double ll) {
		return ll + 1;
	}

	@Override
	protected Integer immediateEvalInt(Integer ll) {
		return ll + 1;
	}

	@Override
	protected String getOperator() {
		return "++";
	}
}
