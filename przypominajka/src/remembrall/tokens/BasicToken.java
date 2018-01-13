package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;
import remembrall.types.AtomType;

public class BasicToken implements Token {

		private Atom atom;
		private TextPos position;
		private Object val;
		
		public BasicToken(Atom a, TextPos tp) {
			atom = a;
			position = tp;
		}
		
		public BasicToken(Atom a, TextPos tp, Object v) {
			this(a, tp);
			val = v;
		}
		
		public Atom getAtom() {
			return atom;
		}
		
		public TextPos getTextPos() {
			return position;
		}
		
		public Object getValue() {
			return val;
		}
		
		public String toString() {
			return atom.toString() + "\n" + position.toString();
		}


}
