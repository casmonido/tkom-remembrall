package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;

public class BoolToken extends BasicToken {
	
	boolean value;

	public BoolToken(Atom a, TextPos tp, boolean v) {
		super(a, tp);
		value = v;
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}
	
	public String toString() {
		return super.toString() + "\n" + value;
	}
}
