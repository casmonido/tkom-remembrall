package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;

public class IntToken extends BasicToken {
	
	Long value;

	public IntToken(Atom a, TextPos tp, Long v) {
		super(a, tp);
		value = v;
	}
	
	public IntToken(Atom a, TextPos tp, Integer v) {
		super(a, tp);
		value = new Long(v);
	}
	
	@Override
	public Long getValue() {
		return value;
	}
	
	public String toString() {
		return super.toString() + "\n" + value.toString();
	}

}
