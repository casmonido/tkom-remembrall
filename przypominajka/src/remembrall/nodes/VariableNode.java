package remembrall.nodes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import remembrall.Environment;
import remembrall.IdentValue;

public class VariableNode implements Node {
	public String ident;
	public String attrib;
	public Node numVal;
	public Environment env;

	public VariableNode(String i, Node v, String a, Environment e) {
		ident = i;
		attrib = a;
		numVal = v;
		env = e;
	}


	@Override
	public IdentValue evalNode(Environment env) throws remembrall.exceptions.RuntimeException { 
		IdentValue objVal = env.resolve(ident);
		Object obj = null;
		if (numVal != null)
			obj = objVal.vArr[(int)numVal.evalNode(env).v];
		else
			obj = (objVal.v==null)?objVal.vArr:objVal.v;
		if (attrib != null) {
			Field f = null;
			try {
				if ("length".equals(attrib))
					return new IdentValue(new Long(Array.getLength(obj)));
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
		return new IdentValue(obj);
	}
}
