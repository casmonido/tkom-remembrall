package remembrall;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



public class Environment {

	List<Map<String, Object>> idents = new LinkedList<Map<String, Object>>();
	//List<Map<String, Object[]>> arrIdents = new LinkedList<Map<String, Object[]>>();
	
	public Environment() {
		addLayer();
	}
	
	public void addLayer() {
		idents.add(new HashMap<String, Object>());
		//arrIdents.add(new HashMap<String, Object []>());
	}
	
	public void removeLayer() {
		idents.remove(idents.size()-1);
		//arrIdents.remove(arrIdents.size()-1);
	}
	
	public void bind(String ident, Object val) {
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
	
	
	public IdentValue resolve(String ident) {
		Object obj = null;
		for (int i = idents.size()-1; i >= 0; i--) {
			obj = idents.get(i).get(ident);
			if (obj != null)
				return new IdentValue(obj);
		}
		return null;
	}
}
