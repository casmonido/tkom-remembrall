package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;
import remembrall.types.AtomType;

public class StringToken extends BasicToken {
	
	String value;

	public StringToken(Atom a, TextPos tp, String v) {
		super(a, tp);
		value = v;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	public String toString() {
		return super.toString() + "\n" + value;
	}
	
}
