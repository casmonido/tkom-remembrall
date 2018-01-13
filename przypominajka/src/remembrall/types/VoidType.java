package remembrall.types;


public class VoidType implements Type {
	
	public static VoidType type = new VoidType();
	
	private VoidType() {}
	
	public String toString() {
		return "void";
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (obj instanceof VoidType) {
	        return true;
	    }
	    return false;
	}

	@Override
	public int hashCode() {
	    return 2;
	}

}
