package remembrall.tokens;

import remembrall.Atom;
import remembrall.TextPos;

public class BasicToken implements Token {

		private Atom atom;
		private TextPos position;
		
		private BasicToken() {};
		public BasicToken(Atom a, TextPos tp) {
			atom = a;
			position = tp;
		}
		
		public Atom getAtom() {
			return atom;
		}
		public TextPos getTextPos() {
			return position;
		}
		public Object getValue() {
			return "";
		}
		
		public String toString() {
			return atom.toString() + "\n" + position.toString();
		}
}
