package remembrall.nodes;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.types.AtomType;
import remembrall.types.Datetime;
import remembrall.types.Location;
import remembrall.types.NetInfo;
import remembrall.types.Time;
import remembrall.types.Type;
import remembrall.types.VoidType;
import remembrall.types.Weather;

public class LiteralNode implements Node {

	public Object value;
	
	public LiteralNode(Object v) {
		value = v;
	}

	@Override
	public TypedValue evalNode(Environment env) {
		Atom at = getAtomType();
		Type t = at==null ? VoidType.type : new AtomType(at);
		return new TypedValue(value, t);
	}
	
	
	private Atom getAtomType() {
		if (value instanceof Long) 
			return Atom.typeInt;
		if (value instanceof Integer) 
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
