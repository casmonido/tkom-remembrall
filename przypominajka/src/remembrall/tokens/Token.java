package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;

public interface Token {

	public Atom getAtom();
	public TextPos getTextPos();
	public Object getValue();
}
