package remembrall;


import java.util.HashMap;
import java.util.Map;



public class Environment {

	Map<String, Object> identTable = new HashMap<String, Object>(5);
	Map<String, Object []> arrTable = new HashMap<String, Object []>(5);
	
	public Environment() {}
	
	public Environment(Environment env) {
		identTable.putAll(env.identTable);
		arrTable.putAll(env.arrTable);
	}
	
	public void bind(String ident, Object val) {
		identTable.put(ident, val);
	}
	
	public void bind(String ident, Object [] val) {
		arrTable.put(ident, val);
	}
	
	public IdentValue resolve(String ident) {
		Object obj = identTable.get(ident);
		Object [] arr = arrTable.get(ident);	
		if (obj != null)
			return new IdentValue(obj);
		return new IdentValue(arr);
	}
}
