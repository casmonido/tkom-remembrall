package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import remembrall.nodes.AdditionNode;
import remembrall.nodes.BoolAndNode;
import remembrall.nodes.BoolNotNode;
import remembrall.nodes.BoolOrNode;
import remembrall.nodes.DivNode;
import remembrall.nodes.EqualsNode;
import remembrall.nodes.LessEqualsNode;
import remembrall.nodes.LessThanNode;
import remembrall.nodes.LiteralNode;
import remembrall.nodes.MoreEqualsNode;
import remembrall.nodes.MoreThanNode;
import remembrall.nodes.MultiplicationNode;
import remembrall.nodes.Node;
import remembrall.nodes.NotEqualNode;
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
	private Scan scan;
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

	public Parser(String filePath) {
		try {
			scan = new Scan(new Source(filePath));
			currToken = scan.nextToken();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
	}
	
	//<collectionDecl> -> <identifier> (  ‘:’ <collectionType> ) 
	//					| ( ‘=’  ‘[‘ <lit> (‘,’ <lit>)* ‘]’  )
	void collectionDecl(Environment env) throws Exception {
		String var = variable(env);
		accept(Atom.becomesOp);
		Node val = valExp(env);
		if (val.evalNode().v != null)
			env.bind(var, val.evalNode().v);
		else
			env.bind(var, val.evalNode().vArr);
	}
	
	//‘When’ <boolExp> ‘do’ <voidExp>+ ‘.’
	void start() throws Exception {
		Environment env = new Environment();
		accept(Atom.whenKw);
		Node l = boolExp(env);
		accept(Atom.doKw);
		List<Node> r = new LinkedList<Node>();
		//r.add(voidExp(env));
			//for (Node rr = voidExp(env); ; rr = voidExp(env))
			//	r.add(rr);
		root = new StartNode(l, r.toArray(new Node [r.size()]), env);
		//assignExp(env);
	}
	
	
	//<boolExp> -> ‘(‘ <valExp> <boolOp> <valExp> ‘)’ |   ‘!’ <valExp>
	Node boolExp(Environment env) throws Exception {
		if (maybe(Atom.lParent)) {
			Node l = valExp(env);
			Token t = boolOp();
			Node r = valExp(env);
			accept(Atom.rParent);
			return t.getAtom()==Atom.andKw?new BoolAndNode(l, r, env):new BoolOrNode(l, r, env);
		}
		accept(Atom.notOp);
		Node l = valExp(env);
		return new BoolNotNode(l, env);
	}
	
	

	//<assignExp> -> <variable> ‘=’ <valExp>
	void assignExp(Environment env) throws Exception {
		String var = variable(env);
		accept(Atom.becomesOp);
		Node val = valExp(env);
		env.bind(var, val.evalNode().v);
	}
	

	//<variable> ::=   <identifier> <variable'>
	private String variable(Environment env) throws Exception {
		String s = (String)accept(Atom.identifier).getValue();
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
		Token l = literal();
		if (l != null) {
			Node n = new LiteralNode(l.getValue(), l.getValue().getClass());
			return valExpPrim(n, env);
		}
		String t = variable(env);
		if (!"".equals(t)) {
			Token op;
			if ((op = sNumOp()) != null) {
				Node cur = null;
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
//		| <typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’ <valExp'>	    <------<constrUse>  
//		| <funcCall> <valExp'>
//		| ‘!’ <valExp> <valExp'> <--------<boolExp> 
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
			currToken instanceof StringToken ||
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
