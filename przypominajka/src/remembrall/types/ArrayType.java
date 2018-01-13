package remembrall.types;


public class ArrayType implements Type {
	
	public static ArrayType type = new ArrayType();
	
	private ArrayType() {}
	
	public String toString() {
		return "[]";
	}
	
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (obj instanceof ArrayType) {
	        return true;
	    }
	    return false;
	}

	@Override
	public int hashCode() {
	    return 1;
	}

}
