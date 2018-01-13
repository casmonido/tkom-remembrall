package remembrall.nodes;

import remembrall.exceptions.RuntimeException;
import remembrall.Environment;
import remembrall.TypedValue;

public class NotEqualNode extends ComparisonNode {

	public NotEqualNode(Node l, Node r) {
		super(l, r);
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException {
		return super.evalNode(env);
		// i dodatkowe
	}

	@Override
	protected boolean immediateCompareDouble(Double ll, Double rr) {
		return ll != rr;
	}

	@Override
	protected boolean immediateCompareInt(Integer ll, Integer rr) {
		return ll != rr;
	}

	@Override
	protected String getOperator() {
		return "!=";
	}
}