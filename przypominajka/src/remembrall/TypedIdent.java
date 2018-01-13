package remembrall;

import remembrall.types.Type;

public class TypedIdent {

	private String ident;
	private Type typ;
	
	
	public TypedIdent(String i, Type t) {
		this.ident = i;
		this.typ = t;
	}
	
	public String getName() {
		return ident;
	}
	
	public Type getType() {
		return typ;
	}
	
}
