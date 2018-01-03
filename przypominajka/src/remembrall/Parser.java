package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import remembrall.nodes.AdditionNode;
import remembrall.nodes.AssignExpNode;
import remembrall.nodes.BoolAndNode;
import remembrall.nodes.BoolNotNode;
import remembrall.nodes.BoolOrNode;
import remembrall.nodes.ConstrNode;
import remembrall.nodes.DivNode;
import remembrall.nodes.EqualsNode;
import remembrall.nodes.FuncCallName;
import remembrall.nodes.IfNode;
import remembrall.nodes.LessEqualsNode;
import remembrall.nodes.LessThanNode;
import remembrall.nodes.LiteralNode;
import remembrall.nodes.MoreEqualsNode;
import remembrall.nodes.MoreThanNode;
import remembrall.nodes.MultiplicationNode;
import remembrall.nodes.Node;
import remembrall.nodes.NotEqualNode;
import remembrall.nodes.RepeatNode;
import remembrall.nodes.ReturnNode;
import remembrall.nodes.SelfAdditionNode;
import remembrall.nodes.SelfSubstractionNode;
import remembrall.tokens.DoubleToken;
import remembrall.tokens.IntToken;
import remembrall.tokens.StringToken;
import remembrall.tokens.Token;
import remembrall.nodes.StartNode;
import remembrall.nodes.SubstractionNode;
import remembrall.nodes.VariableNode;


public class Parser {
	private Token currToken;
	private ScanInterface scan;
	private Node root;
	

	
	private Token accept(Atom atom) throws Exception { // po co to
		if (currToken.getAtom() == atom) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private boolean maybe(Atom atom) {
		if (currToken.getAtom() == atom) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return true;
		}
		return false;
	}

	public Parser(ScanInterface sc) {
		scan = sc;
		currToken = scan.nextToken();
	}
	
	//<collectionDecl> -> <identifier> (  ‘:’ <collectionType> ) 
	//					| ( ‘=’  ‘[‘ <lit> (‘,’ <lit>)* ‘]’  )
	void collectionDecl(Environment env) throws Exception {
		String var = variable(env);
		if (var == null || "".equals(var))
			return;
		accept(Atom.becomesOp);
		Node val = valExp(env);
		if (val.evalNode().v != null)
			env.bind(var, val.evalNode().v);
		else
			env.bind(var, val.evalNode().vArr);
	}
	
	//‘When’ <boolExp> ‘do’ <voidExp>+ ‘.’
	public void start() throws Exception {
		Environment env = new Environment();
		accept(Atom.whenKw);
		Node l = boolExp(env);
		accept(Atom.doKw);
		Node valEx = valExp(env);
		Node voidEx = voidExp(env);
		List<Node> list = new LinkedList<Node>();
		while (valEx != null || voidEx != null) {
			if (valEx != null)
				list.add(valEx);
			if (voidEx != null)
				list.add(voidEx);
			valEx = valExp(env);
			voidEx = voidExp(env);	
		}
		accept(Atom.dotOp);
		root = new StartNode(l, list.toArray(new Node [list.size()]), env);
	}
	
	//<voidExp> -> <ifExp> | <repExp> | <retExp> | <procedureCall>
	Node voidExp(Environment env) throws Exception {
		Node n = ifExp(env);
		if (n != null)
			return n;
		n = repExp(env);
		if (n != null)
			return n;
		n = retExp(env);
		if (n != null)
			return n;
		n = procedureCall(env);
		if (n != null)
			return n;
		return null;
	}
	
	//<procedureCall> -> <procedureName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node procedureCall(Environment env) throws Exception {
		Token id = accept(Atom.identifier);
		if (id == null) return null;
		List<Node> args = new LinkedList<Node>();
		accept(Atom.lParent);
		Node n = valExp(env);
		while (n != null) {
			args.add(n);
			n = valExp(env);
		}
		accept(Atom.rParent);
		return new FuncCallName((String)id.getValue(), args, env);
	}
	
	//<retExp> -> ‘return’ <valExp>
	public Node retExp(Environment env) throws Exception {
		if (maybe(Atom.returnKw)) {
			Node n = valExp(env);
			return new ReturnNode(n, env);
		}
		return null;
	}
	
	//<repExp> -> ‘repeat’ ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	//			| 'repeatUntil' ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	public Node repExp(Environment env) throws Exception {
		Token rep = accept(Atom.repeatKw);
		if (rep == null)
			rep = accept(Atom.repeatUntilKw);
		if (rep != null) {
			accept(Atom.lParent);
			Node n = valExp(env);
			accept(Atom.rParent);
			accept(Atom.lParent);
			Node valEx = valExp(env);
			Node voidEx = voidExp(env);
			List<Node> list = new LinkedList<Node>();
			while (valEx != null && voidEx != null) {
				if (valEx != null)
					list.add(valEx);
				if (voidEx != null)
					list.add(voidEx);
				valEx = valExp(env);
				voidEx = voidExp(env);	
			}
			accept(Atom.rParent);
			return new RepeatNode(valEx, list, env);
		}
		return null;
	}
	
	//<ifExp> -> ‘if’ <valExp> ‘(’ (<valExp> | <voidExp>)* ‘)’ [ ‘else’ ‘(’ (<valExp> | <voidExp> )* ‘)’ ]
	Node ifExp(Environment env) throws Exception {
		if (maybe(Atom.ifKw)) {
			Node warunek = valExp(env);
			accept(Atom.lParent);
			List<Node> list = new LinkedList<Node>();
			Node valEx = valExp(env);
			Node voidEx = voidExp(env);
			List<Node> elseList = new LinkedList<Node>();
			while (valEx != null && voidEx != null) {
				if (valEx != null)
					list.add(valEx);
				if (voidEx != null)
					list.add(voidEx);
				valEx = valExp(env);
				voidEx = voidExp(env);	
			}
			accept(Atom.rParent);
			if (maybe(Atom.elseKw)) {
				//‘else’ ‘(’ (<valExp> | <voidExp> )* ‘)’
				accept(Atom.lParent);
				valEx = valExp(env);
				voidEx = voidExp(env);
				while (valEx != null && voidEx != null) {
					if (valEx != null)
						list.add(valEx);
					if (voidEx != null)
						elseList.add(voidEx);
					valEx = valExp(env);
					voidEx = voidExp(env);	
				}
				accept(Atom.rParent);
			}
			return new IfNode(warunek, list, elseList, env);
		}
		return null;
	}
	
	
	//<boolExp> -> ‘(‘ <valExp> [<boolOp> <valExp>] ‘)’ |   ‘!’ <valExp>
	Node boolExp(Environment env) throws Exception {
		if (maybe(Atom.lParent)) {
			Node l = valExp(env);
			Token t = boolOp();
			if (t != null) {
				Node r = valExp(env);
				accept(Atom.rParent);
				return t.getAtom()==Atom.andKw?new BoolAndNode(l, r, env):new BoolOrNode(l, r, env);
			}
			accept(Atom.rParent);
			return l;
		}
		accept(Atom.notOp);
		Node l = valExp(env);
		return new BoolNotNode(l, env);
	}
	
	

	//<assignExp> -> <variable> ‘=’ <valExp>
	void assignExp(Environment env) throws Exception {
		String var = variable(env);
		if (var == null || "".equals(var))
			return;
		accept(Atom.becomesOp);
		Node val = valExp(env);
		env.bind(var, val.evalNode().v);
	}
	

	//<variable> ::=   <identifier> <variable'>
	private String variable(Environment env) throws Exception {
		Token t = accept(Atom.identifier);
		if (t == null) return "";
		String s = (String) t.getValue();
		s += variablePrim(env);
		return s;
	}
			
	//	<variable'> ::=   ‘[‘ <valExp> ‘]’ <variable'>
	//					|  (‘.’ <variable> )+ <variable'>
	//					| ϵ
	private String variablePrim(Environment env) throws Exception {
		if (maybe(Atom.lBracket)) {
			Node v = valExp(env);
			accept(Atom.rBracket);
			String s = variablePrim(env);
			return "[" + v.evalNode().v + "]" + s;
		}
		String v = "";
		if (maybe(Atom.dotOp)) {
			v += "." + variable(env);
			while (maybe(Atom.dotOp)) {
				v += "." + variable(env);	
			}
			v += variablePrim(env);	
		}	
		return v;
	}
	
	//<attribute> ::= <variable> (‘.’ <variable> )+
	private String attribute(Environment env) throws Exception {
		String s = variable(env);
		if (s == null || "".equals(s))
			return "";
		while (maybe(Atom.dotOp)) {
			s += "." + variable(env);	
		}
		return s;
	}
	
	//<collectElem> ::= <variable> ‘[‘ <valExp> ‘]’
	private String collectElem(Environment env) throws Exception {
		String s = variable(env);
		accept(Atom.lBracket);
		Node v = valExp(env);
		accept(Atom.rBracket);
		return s + "[" + v.evalNode().v + "]";
	}
	
	
//<valExp> -> <literal> <valExp'>
	
//	| <variable>  <sNumOp> 	<valExp'>				  <-------<sNumExp> 
//	| <variable> ‘=’ <valExp> <valExp'> 	          <-------<assignExp> 
//	| <variable> <valExp'>
	
//	| ‘(‘ <typeName> ‘)’ <valExp> <valExp'>		      <-------<cast> 
//	| ‘(‘ <valExp> <boolOp> <valExp> ‘)’ |   ‘!’ <valExp> <valExp'> <--------<boolExp> 
//	| <typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’ <valExp'>	    <------<constrUse>  
//	| <funcCall> <valExp'>
	private Node valExp(Environment env) throws Exception {
		Node xxx = procedureCall(env);
		if (xxx != null)
			return valExpPrim(xxx, env);
		Token l = literal();
		if (l != null) {
			Node n = new LiteralNode(l.getValue(), l.getValue().getClass());
			return valExpPrim(n, env);
		}
		String t = variable(env);
//		| <variable>  <sNumOp> 	<valExp'>				  <-------<sNumExp> 
//		| <variable> ‘=’ <valExp> <valExp'> 	          <-------<assignExp> 
//		| <variable> <valExp'>
//		| <funcCall> <valExp'>
		if (t != null && !"".equals(t)) {
			Token op;
			Node cur = null;
			if ((op = sNumOp()) != null) {
				switch (op.getAtom()) {
				case doublePlus:
					cur = new SelfAdditionNode(t, env);
					break;
				case doubleMinus:
					cur = new SelfSubstractionNode(t, env);
					break;
				}
				return valExpPrim(cur, env);
			}
			if (maybe(Atom.becomesOp)) {
				Node ll = valExp(env);
				Node r = valExpPrim(ll, env);
				return new AssignExpNode(new VariableNode(t, env), r, env);
			}
			return valExpPrim(new VariableNode(t, env), env);
		}
//		| ‘(‘ <typeName> ‘)’ <valExp> <valExp'>		      <-------<cast> 
//		| ‘(‘ <valExp> <boolOp> <valExp> ‘)’ <valExp'> 	  <-----<boolExp> 
		if (maybe(Atom.lParent)) {
			Atom type = typeName();
			if (type != null) {
				accept(Atom.rParent);
				Node nr = valExp(env);
				return valExpPrim(nr, env);
			}
			Node nl = valExp(env);
			Token op = boolOp();
			Node nr = valExp(env);
			accept(Atom.rParent);
			return op.getAtom()==Atom.andKw?new BoolAndNode(nl, nr, env):new BoolOrNode(nl, nr, env);
		}
		Node fc = procedureCall(env);
		if (fc != null)
			return valExpPrim(fc, env);
		if (maybe(Atom.notOp)) {
			accept(Atom.notOp);
			fc = valExp(env);
			Node xx = new BoolNotNode(fc, env);
			return valExpPrim(xx, env);
		}
		if ((fc = constrUse(env)) != null)
			return valExpPrim(fc, env);
//		| <typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’ <valExp'>	    <------<constrUse>  
//		| ‘!’ <valExp> <valExp'> <--------<boolExp> 
		return null;
	}
	
	//<typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node constrUse(Environment env) throws Exception {
		Atom t = typeName();
		if (t != null) {
			List<Node> args = new LinkedList<Node>();
			accept(Atom.lParent);
			Node n = valExp(env);
			while (n != null) {
				args.add(n);
				n = valExp(env);
			}
			accept(Atom.rParent);
			return new ConstrNode(t, args, env);
		}
		return null;
	}
	
	
//<valExp'> -> 
//	|  <doubOp> <valExp> <valExp'>					  <-------<arytmExp> 
//	|  <compOp> <valExp> <valExp'>					  <-------<compExp>
//	|  ϵ
	private Node valExpPrim(Node left, Environment env) throws Exception {
		Token op = doubOp();
		if (op != null) {
			Node r = valExp(env);
			Node cur = null;
			switch (op.getAtom()) {
			case plusOp:
				cur = new AdditionNode(left, r, env);
				break;
			case minusOp:
				cur = new SubstractionNode(left, r, env);
				break;
			case divOp:
				cur = new DivNode(left, r, env);
				break;
			case multOp:
				cur = new MultiplicationNode(left, r, env);
				break;
			}
			return valExpPrim(cur, env);
		}
		op = compOp();
		if (op != null) {
			Node r = valExp(env);
			Node cur = null;
			switch (op.getAtom()) {
			case equalsOp:
				cur = new EqualsNode(left, r, env);
				break;
			case notEqual:
				cur = new NotEqualNode(left, r, env);
				break;
			case lessEquals:
				cur = new LessEqualsNode(left, r, env);
				break;
			case lessThan:
				cur = new LessThanNode(left, r, env);
				break;
			case moreEquals:
				cur = new MoreEqualsNode(left, r, env);
				break;
			case moreThan:
				cur = new MoreThanNode(left, r, env);
				break;
			}
			return valExpPrim(cur, env);
		}
		return left;
	}
	
	private Token literal() throws Exception {
		if (currToken instanceof DoubleToken ||
			(currToken instanceof StringToken &&
			currToken.getAtom() != Atom.identifier) ||
			currToken instanceof IntToken) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token sNumOp() throws Exception {
		if (currToken.getAtom() == Atom.doublePlus ||
			currToken.getAtom() == Atom.doubleMinus) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token boolOp() throws Exception {
		if (currToken.getAtom() == Atom.andKw ||
			currToken.getAtom() == Atom.orKw) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	//<compOp> -> ‘==’ | ‘>=’ | ‘>’ | ‘<’ | ‘<=’ | ‘!=’
	private Token compOp() throws Exception {
		if (currToken.getAtom() == Atom.equalsOp ||
			currToken.getAtom() == Atom.lessEquals ||
			currToken.getAtom() == Atom.lessThan ||
			currToken.getAtom() == Atom.moreEquals ||
			currToken.getAtom() == Atom.moreThan ||
			currToken.getAtom() == Atom.notEqual) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘+’ | ‘-’ | ‘*’ | ‘+=’ | '-=' | '/'
	private Token doubOp() throws Exception {
		if (currToken.getAtom() == Atom.plusOp ||
			currToken.getAtom() == Atom.minusOp ||
			currToken.getAtom() == Atom.multOp ||
			currToken.getAtom() == Atom.divOp ||
			currToken.getAtom() == Atom.plusBecomes ||
			currToken.getAtom() == Atom.minusBecomes) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	Atom typeName() {
		if (currToken.getAtom() == Atom.typeInt ||
				currToken.getAtom() == Atom.typeDouble ||
				currToken.getAtom() == Atom.typeString ||
				currToken.getAtom() == Atom.typeBool ||
				currToken.getAtom() == Atom.typeTime ||
				currToken.getAtom() == Atom.typeDatetime ||
				currToken.getAtom() == Atom.typeLocation ||
				currToken.getAtom() == Atom.typeWeather ||
				currToken.getAtom() == Atom.typeNetInfo) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken.getAtom();
		}
		return null;	
	}

//	//<annotation> -> '@' (<startAnno> | <everyAnno>)
//	private Token annotation() throws Exception {
//		accept(Atom.atOp);
//		if (currToken.getAtom() == Atom.startKw)
//			startAnno();
//		else 
//			everyAnno();
//		return null;
//	}
}
