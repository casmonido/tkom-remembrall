package remembrall.nodes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.types.ArrayType;
import remembrall.types.AtomType;
import remembrall.types.Datetime;
import remembrall.types.Location;
import remembrall.types.NetInfo;
import remembrall.types.Time;
import remembrall.types.Type;
import remembrall.types.VoidType;
import remembrall.types.Weather;
import remembrall.exceptions.RuntimeException;

public class VariableNode implements Node {
	public Node ident;
	public String attrib;
	public Node numVal;
	

	public VariableNode(Node i, Node v, String a) {
		ident = i;
		attrib = a;
		numVal = v;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException { 
		TypedValue tv = ident.evalNode(env);
		if (tv == null)
			throw new RuntimeException("Zmienna " + ((IdentNode)ident).ident + " nie została zainicjalizowana!");
		Object obj = tv.getValue();
		Type typ = tv.getType();
		if (numVal != null && !obj.getClass().isArray()) 
			throw new RuntimeException("Próba użycia operatora wyłuskania [] na obiekcie który nie jest tablicą!");
		if (numVal != null) {
			obj = ((Node [])obj)[(int)numVal.evalNode(env).getValue()];
			typ = ((Node) obj).evalNode(env).getType();
		}
		if (attrib != null) {
			Field f = null;
			try {
				if (obj.getClass().isArray() && "length".equals(attrib))
					return new TypedValue(new Long(Array.getLength(obj)), new AtomType(Atom.typeInt));
				f = obj.getClass().getDeclaredField(attrib);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException("Atrybut nie występuje w obiekcie");
			} 
			f.setAccessible(true);
			try {
				obj = f.get(obj);
				Atom at = getAtomType(obj);
				typ = ((at==null) ? VoidType.type : new AtomType(at));
			} catch (IllegalAccessException e) {
				// nie powinno sie zdarzyc
				e.printStackTrace();
			}
		}
		return new TypedValue(obj, typ);
	}
	
	
	private Atom getAtomType(Object value) {
		if (value instanceof Long) 
			return Atom.typeInt;
		if (value instanceof Double) 
			return Atom.typeDouble;
		if (value instanceof String) 
			return Atom.typeString;
		if (value instanceof Boolean) 
			return Atom.typeBool;
		if (value instanceof Time) 
			return Atom.typeTime;
		if (value instanceof Datetime) 
			return Atom.typeDatetime;
		if (value instanceof Location) 
			return Atom.typeLocation;
		if (value instanceof Weather) 
			return Atom.typeWeather;
		if (value instanceof NetInfo) 
			return Atom.typeNetInfo;
		return null;
	}
}
