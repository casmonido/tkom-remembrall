package remembrall.nodes;

import java.util.List;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.AtomType;
import remembrall.types.Datetime;
import remembrall.types.Location;
import remembrall.types.Time;

public class ConstrNode implements Node {
	public Atom type;
	public List<Node> args;
	
	public ConstrNode(Atom t, List<Node> a) {
		type = t;
		args = a;
	}

	@Override
	public TypedValue evalNode(Environment env) throws RuntimeException { 
		switch (type) { 
		case typeDatetime:
//			datetime(int day, int month, int year)
//			datetime(int day, int month, int year, time t)
//			datetime(int day, int month, int year, int hour)
//			datetime(int day, int month, int year, int hour, int min)
			if (args.size() == 3)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).getValue(),
						(Long)args.get(1).evalNode(env).getValue(),
						(Long)args.get(2).evalNode(env).getValue()),
						new AtomType(type));
			if (args.size() == 4 && args.get(3) instanceof Time)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).getValue(),
						(Long)args.get(1).evalNode(env).getValue(),
						(Long)args.get(2).evalNode(env).getValue(),
						(Time)args.get(3).evalNode(env).getValue()),
						new AtomType(type));
			if (args.size() == 4)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).getValue(), 
						(Long)args.get(1).evalNode(env).getValue(), 
						(Long)args.get(2).evalNode(env).getValue(),
						(Long)args.get(3).evalNode(env).getValue()),
						new AtomType(type));
			if (args.size() == 5)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).getValue(), 
						(Long)args.get(1).evalNode(env).getValue(), 
						(Long)args.get(2).evalNode(env).getValue(),
						(Long)args.get(3).evalNode(env).getValue(),
						(Long)args.get(4).evalNode(env).getValue()),
						new AtomType(type));
		case typeLocation:
			return new TypedValue(new Location(
					(String)args.get(0).evalNode(env).getValue(), 
					(String)args.get(1).evalNode(env).getValue(), 
					(String)args.get(2).evalNode(env).getValue(),
					(String)args.get(3).evalNode(env).getValue(), 
					(String)args.get(4).evalNode(env).getValue()),
					new AtomType(type));
		case typeTime:
			if (args.size() == 1)
				return new TypedValue(new Time(
						(Long)args.get(0).evalNode(env).getValue()),
						new AtomType(type));
			if (args.size() == 2)
				return new TypedValue(new Time(
						(Long)args.get(0).evalNode(env).getValue(), 
						(Long)args.get(1).evalNode(env).getValue()),
						new AtomType(type));
			return new TypedValue(new Time(
					(Long)args.get(0).evalNode(env).getValue(), 
					(Long)args.get(1).evalNode(env).getValue(), 
					(Long)args.get(2).evalNode(env).getValue()),
					new AtomType(type));
		default:
			return null;
		}
	}
}
