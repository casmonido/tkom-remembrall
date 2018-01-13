package remembrall.nodes;

import java.util.List;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.TypedValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.Datetime;
import remembrall.types.Location;
import remembrall.types.Time;

public class ConstrNode implements Node {
	public Atom type;
	public List<Node> args;
	protected Environment env;
	
	public ConstrNode(Atom t, List<Node> a, Environment e) {
		type = t;
		args = a;
		env = e;
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
						(Long)args.get(0).evalNode(env).v,
						(Long)args.get(1).evalNode(env).v,
						(Long)args.get(2).evalNode(env).v));
			if (args.size() == 4 && args.get(3) instanceof Time)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).v,
						(Long)args.get(1).evalNode(env).v,
						(Long)args.get(2).evalNode(env).v,
						(Time)args.get(3).evalNode(env).v));
			if (args.size() == 4)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).v, 
						(Long)args.get(1).evalNode(env).v, 
						(Long)args.get(2).evalNode(env).v,
						(Long)args.get(3).evalNode(env).v));
			if (args.size() == 5)
				return new TypedValue(new Datetime(
						(Long)args.get(0).evalNode(env).v, 
						(Long)args.get(1).evalNode(env).v, 
						(Long)args.get(2).evalNode(env).v,
						(Long)args.get(3).evalNode(env).v,
						(Long)args.get(4).evalNode(env).v));
		case typeLocation:
			return new TypedValue(new Location(
					(String)args.get(0).evalNode(env).v, 
					(String)args.get(1).evalNode(env).v, 
					(String)args.get(2).evalNode(env).v,
					(String)args.get(3).evalNode(env).v, 
					(String)args.get(4).evalNode(env).v));
		case typeTime:
			if (args.size() == 1)
				return new TypedValue(new Time(
						(Long)args.get(0).evalNode(env).v));
			if (args.size() == 2)
				return new TypedValue(new Time(
						(Long)args.get(0).evalNode(env).v, 
						(Long)args.get(1).evalNode(env).v));
			return new TypedValue(new Time(
					(Long)args.get(0).evalNode(env).v, 
					(Long)args.get(1).evalNode(env).v, 
					(Long)args.get(2).evalNode(env).v));
		default:
			return null;
		}
	}
}
