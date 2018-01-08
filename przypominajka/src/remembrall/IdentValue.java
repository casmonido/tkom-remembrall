package remembrall;

public class IdentValue {
	
	public Object [] vArr;
	public Object v;
	public Atom typ;
	public boolean array;
	
	
	public IdentValue(Object v) {
		this.v = v;
	}
	
	public IdentValue(Object [] vA) {
		this.vArr = vA;
	}	
}
