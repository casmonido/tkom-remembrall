package remembrall.nodes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import remembrall.Environment;
import remembrall.TypedValue;

public class VariableNode implements Node {
	public Node ident;
	public String attrib;
	public Node numVal;
	public Environment env;

	public VariableNode(Node i, Node v, String a, Environment e) {
		ident = i;
		attrib = a;
		numVal = v;
		env = e;
	}

	@Override
	public TypedValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException { 
		Object obj = ident.evalNode(env).v;
		if (numVal != null)
			obj = ((Object [])obj)[(int)numVal.evalNode(env).v];
		if (attrib != null) {
			Field f = null;
			try {
				if ("length".equals(attrib))
					return new TypedValue(new Long(Array.getLength(obj)));
				f = obj.getClass().getDeclaredField(attrib);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException("Atrybut nie występuje w obiekcie");
			} 
			f.setAccessible(true);
			try {
				obj = f.get(obj);
			} catch (IllegalAccessException e) {
				// nie powinno sie zdarzyc
				e.printStackTrace();
			}
		}
		return new TypedValue(obj);
	}
}
