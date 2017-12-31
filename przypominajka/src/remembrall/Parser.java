package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import remembrall.nodes.BoolAndNode;
import remembrall.nodes.BoolNotNode;
import remembrall.nodes.BoolOrNode;
import remembrall.nodes.LiteralNode;
import remembrall.nodes.Node;
import remembrall.tokens.DoubleToken;
import remembrall.tokens.IntToken;
import remembrall.tokens.StringToken;
import remembrall.tokens.Token;
import remembrall.nodes.StartNode;


public class Parser {
	private Token currToken;
	private Scan scan;
	private Node root;
	
	
	private Token accept(Atom atom) throws Exception {
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
		String var = variable();
		accept(Atom.becomesOp);
		Token val = valExp();
		env.bind(var, val.getValue());
	}
	
	//‘When’ <boolExp> ‘do’ <voidExp>+ ‘.’
	void start() throws Exception {
		Environment env = new Environment();
		accept(Atom.whenKw);
		Node l = boolExp(env);
		accept(Atom.doKw);
		List<Node> r = new LinkedList<Node>();
		r.add(voidExp(env));
			for (Node rr = voidExp(env); ; rr = voidExp(env))
				r.add(rr);
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
//	| <variable> <valExp'>
//	| <variable>  <sNumOp> 	<valExp'>				  <-------<sNumExp> 
//	| <variable> ‘=’ <valExp> <valExp'> 	          <-------<assignExp> 
//	| ‘(‘ <typeName> ‘)’ <valExp> <valExp'>		      <-------<cast> 
//	| ‘(‘ <valExp> <boolOp> <valExp> ‘)’ |   ‘!’ <valExp> <valExp'><--------<boolExp> 
//	| <typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’ <valExp'>	    <------<constrUse>  
//	|<funcCall> <valExp'>
	private Node valExp(Environment env) throws Exception {
		Token l = literal();
		if (l != null)
			return new LiteralNode(l.getValue(), l.getValue().getClass());
		String t = variable(env);
		if (!"".equals(t)) {
			Token sign;
			if ((sign = sNumOp()) != null)
				return new LiteralNode(l.getValue(), l.getValue().getClass());
		}
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
