package remembrall;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class Environment {

	List<Map<String, TypedValue>> idents = new LinkedList<Map<String, TypedValue>>();
	
	public Environment() {
		addLayer();
	}
	
	public void addLayer() {
		idents.add(new HashMap<String, TypedValue>());
	}
	
	public void removeLayer() {
		idents.remove(idents.size()-1);
	}
	
	public void bind(String ident, TypedValue val) {
		int layer = findLayer(ident);
		if (layer == -1)
			layer = idents.size()-1;
		idents.get(layer).put(ident, val);
	}
	
	private int findLayer(String ident) {
		for (int i = idents.size()-1; i >= 0; i--)
			if (idents.get(i).get(ident) != null)
				return i;
		return -1;
	}
	
	
	public TypedValue resolve(String ident) {
		TypedValue obj = null;
		for (int i = idents.size()-1; i >= 0; i--) {
			obj = idents.get(i).get(ident);
			if (obj != null)
				return obj;
		}
		return null;
	}
}
