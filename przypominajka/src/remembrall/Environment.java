package remembrall;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import remembrall.nodes.Node;


public class Environment {

	Map<String, Object> identTable = new HashMap<String, Object>(5);
	Map<String, Object []> arrTable = new HashMap<String, Object []>(5);
	
	public Environment() {}
	
	public Environment(Environment env) {
		identTable.putAll(env.identTable);
		arrTable.putAll(env.arrTable);
	}
	
	public void bind(String s, Object v) {
		identTable.put(s, v);
	}
	
	public void bindArr(String s, Object [] t) {
		arrTable.put(s, t);
	}
	
	// a
	// a.i
	// a[2]
	// a[2].e
	public IdentValue resolve(String s, Node num, String attr) {
		String mainIdent = s;
		int arrPos = -1;
		Pattern ident_attr = Pattern.compile("(.*)\\.(.*)"); 
		Matcher m = ident_attr.matcher(s);
		if (m.matches()) {
			mainIdent = m.group(1);
			attr = m.group(2);
		}
		Pattern ident_array = Pattern.compile("(.*)[(.*)]"); 
		Matcher arr = ident_array.matcher(mainIdent);
		if (arr.matches()) {
			mainIdent = m.group(1);
			arrPos = new Integer(m.group(2));
		}
		boolean isArr = true;
		Object [] tArr = arrTable.get(mainIdent);
		Object tok = null;
		if (tArr == null) {
			isArr = false;
			tok = identTable.get(mainIdent);
		}
		if (arrPos != -1 && isArr) {
			tok = tArr[arrPos];
			isArr = false;
		}
		if ("".equals(attr))
			return isArr?new IdentValue(tArr):new IdentValue(tok);
		//jesli jest [3] to wyjmij z tego
		try {
			Field f = tok.getClass().getDeclaredField(attr);
			f.setAccessible(true);
			return new IdentValue(f.get(tok));
		} catch (NoSuchFieldException | IllegalAccessError | IllegalArgumentException | IllegalAccessException e) {
			System.err.println("What !!!!");
		} 
		return new IdentValue("");
	}
}
