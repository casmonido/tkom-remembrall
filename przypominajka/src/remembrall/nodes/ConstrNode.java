package remembrall.nodes;

import java.util.List;

import remembrall.Atom;
import remembrall.Environment;
import remembrall.IdentValue;
import remembrall.exceptions.RuntimeException;
import remembrall.types.Time;

public class ConstrNode implements Node {
	protected Atom type;
	protected List<Node> args;
	protected Environment env;
	
	public ConstrNode(Atom t, List<Node> a, Environment e) {
		type = t;
		args = a;
		env = e;
	}

	@Override
	public IdentValue evalNode() throws RuntimeException { 
		switch (type) { 
//		typeDatetime,
//		typeLocation,
//		typeWeather,
//		typeNetInfo,
		case typeTime:
			return new IdentValue(new Time((Integer)args.get(0).evalNode().v, 
					(Integer)args.get(1).evalNode().v, 
					(Integer)args.get(2).evalNode().v));
		default:
			return null;
		}
	}
}
