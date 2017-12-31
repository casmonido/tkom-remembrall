package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;

public class DoubleToken extends BasicToken {
	
	Double value;

	public DoubleToken(Atom a, TextPos tp, Double v) {
		super(a, tp);
		value = v;
	}
	
	@Override
	public Double getValue() {
		return value;
	}
	
	public String toString() {
		return super.toString() + "\n" + value.toString();
	}

}
