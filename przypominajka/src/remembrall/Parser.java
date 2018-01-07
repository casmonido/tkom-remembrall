package remembrall;

import java.util.LinkedList;
import java.util.List;

import remembrall.exceptions.ParseException;
import remembrall.nodes.AdditionNode;
import remembrall.nodes.AssignExpNode;
import remembrall.nodes.BoolAndNode;
import remembrall.nodes.BoolNotNode;
import remembrall.nodes.BoolOrNode;
import remembrall.nodes.CastNode;
import remembrall.nodes.ConstrNode;
import remembrall.nodes.DivNode;
import remembrall.nodes.EqualsNode;
import remembrall.nodes.FuncCallName;
import remembrall.nodes.FunctionCallNode;
import remembrall.nodes.IfNode;
import remembrall.nodes.LessEqualsNode;
import remembrall.nodes.LessThanNode;
import remembrall.nodes.LiteralNode;
import remembrall.nodes.MinusAssignNode;
import remembrall.nodes.MoreEqualsNode;
import remembrall.nodes.MoreThanNode;
import remembrall.nodes.MultiplicationNode;
import remembrall.nodes.Node;
import remembrall.nodes.NotEqualNode;
import remembrall.nodes.PlusAssignNode;
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
	private ErrorTracker bin;
	private FunctionArray functions;
	
	public Parser(ScanInterface sc, ErrorTracker et) {
		scan = sc;
		currToken = scan.nextToken();
		bin = et;
	}
	
	private Token accept(Atom atom) throws ParseException {
		if (currToken.getAtom() == atom) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		throw new ParseException("Na pozycji " + currToken.getTextPos().toString() + 
				" spodziewany atom: " + atom +
				", znaleziono: " + currToken.getAtom());
	}
	
	private boolean maybe(Atom atom) {
		if (currToken.getAtom() == atom) {
			currToken = scan.nextToken();
			return true;
		}
		return false;
	}
	
	private void skipTo(Atom [] atoms) {
		boolean end = false;
		while (currToken.getAtom() != Atom.eof) {
			for (Atom at : atoms)
				if (currToken.getAtom() == at)
					end = true;
			if (end) break;
			currToken = scan.nextToken();
		}
			
	}
	
	//[<include>] [<funcDef>*] [<assignExp>*] ‘When’ <boolExp> ‘do’ <voidExp>+ ‘.’
	public void start() {
		Environment env = new Environment();
		functions = include();
		//[<funcDef>*] 
		
		List<Node> assignNodes = new LinkedList<Node>();
		try {
			Node aNode = assignExp(env);
			while (aNode != null) {
				assignNodes.add(aNode);
				aNode = assignExp(env);
			}
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.whenKw});
			bin.parseError(pe.getMessage());
		}
		try {
			accept(Atom.whenKw);
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.lParent});
			bin.parseError(pe.getMessage());
		}
		Node condition;
		//try {
			condition = boolExp(env);
		//} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.lParent});
			bin.parseError(pe.getMessage());
		//}
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
	
	//'include' <fileName>
	public FunctionArray include() {
		FunctionArray fArr = new FunctionArray();
		if (maybe(Atom.inclKw)) {
			Token fileName;
			try {
				fileName = accept(Atom.stringConst);
			}
			catch (ParseException pe) {
				skipTo(new Atom [] {Atom.atOp, Atom.whenKw});
				bin.parseError(pe.getMessage());
			}
		}
		return fArr;
	}
	
	
	//<collectionDecl> -> <identifier> (  ‘:’ <collectionType> ) 
	//					| ( ‘=’  ‘[‘ <lit> (‘,’ <lit>)* ‘]’  )
	void collectionDecl(Environment env) throws ParseException {
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
	

	//<voidExp> -> <ifExp> | <repExp> | <retExp> | <procedureCall>
	Node voidExp(Environment env) throws ParseException {
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
	private Node procedureCall(Environment env) throws ParseException {
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
	public Node retExp(Environment env) throws ParseException {
		if (maybe(Atom.returnKw)) {
			Node n = valExp(env);
			return new ReturnNode(n, env);
		}
		return null;
	}
	
	//<repExp> -> ‘repeat’ ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	//			| 'repeatUntil' ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	public Node repExp(Environment env) throws ParseException {
		Token rep = accept(Atom.repeatKw);
		if (rep == null)
			rep = accept(Atom.repeatUntilKw);
		if (rep != null) {
			accept(Atom.lParent);
			Node cond = valExp(env);
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
			return new RepeatNode(cond, list, env);
		}
		return null;
	}
	
	//<ifExp> -> ‘if’ <valExp> ‘(’ (<valExp> | <voidExp>)* ‘)’ [ ‘else’ ‘(’ (<valExp> | <voidExp> )* ‘)’ ]
	Node ifExp(Environment env) throws ParseException {
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
	
	
	//<boolExp> -> ‘(‘ <valExp> [<boolOp> <valExp>] ‘)’ | [‘!’] <valExp>
	Node boolExp(Environment env) throws ParseException {
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
		return new (l, env);
	}
	
	

	//<assignExp> -> <variable> <assignOp> <valExp>
	Node assignExp(Environment env) throws ParseException {
		Node var = variable(env);
		if (var == null || "".equals(var))
			return;
		Token op = assignOp();
		Node val = valExp(env);
		//env.bind(var, val.evalNode().v);
	}
	

	//<identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>]
	private String variable(Environment env) throws ParseException {
		Token t = accept(Atom.identifier);
		if (t == null) return "";
		String s = (String) t.getValue();
		s += variablePrim(env);
		return s;
	}
			
	everyAnno();
	private void startAnno() {
		if (!maybe(Atom.atOp))
			return;
		if (!)
	}
	
//	[<everyAnno>][<startAnno>] <identifier> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node funcCall() {
		everyAnno();
		startAnno();
		Environment env = new Environment();
		Token lit = literal();
		accept(Atom.lParent);
		List<Node> args = new LinkedList<Node>();
		Node n = valExp(env);
		while (n != null) {
			args.add(n);
			if (maybe(Atom.commaOp))
				break;
			n = valExp(env);
		}
		accept(Atom.rParent);
		return new FunctionCallNode(functions.resolve((String)lit.getValue()), args);
	}

	private Node valExp(Environment env) throws ParseException {
//	    <literal> 
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>] <selfNumOp> 
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>] <assignOp> <valExp>
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>] 
		Token t = literal();
		if (t != null)
			return new LiteralNode(t.getValue());
		t = identifier(); //poszukaj t.getValue() w liście funkcji
//		| [<everyAnno>][<startAnno>] <identifier> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
		if (t != null) {
			Node n = null;
			Token tAttr = null;
			if (maybe(Atom.lBracket)) {
				n = valExp(env);
				accept(Atom.rBracket);	
			}
			if (maybe(Atom.dotOp))
				tAttr = identifier();
			Token op = selfNumOp();
			if (op != null)
				switch (op.getAtom()) {
				case doublePlus:
					return new SelfAdditionNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
				case doubleMinus:
					return new SelfSubstractionNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
				default:
					break;
				}
			op = assignOp();
			if (op != null) {
				Node value = valExp(env);
				switch (op.getAtom()) {
				case becomesOp:
					return new AssignExpNode((String)t.getValue(), n, (String)tAttr.getValue(), value, env);
				case plusBecomes:
					return new PlusAssignNode((String)t.getValue(), n, (String)tAttr.getValue(), value, env);
				case minusBecomes:
					return new MinusAssignNode((String)t.getValue(), n, (String)tAttr.getValue(), value, env);
				default:
					break;
				}
			}
			return new VariableNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
		}
//		| ‘(‘ <typeName> ‘)’ <valExp> 
//		| '(' <valExp> <numOp> <valExp> ')'
//		| ‘(‘ <valExp> [<boolOp> <valExp>] ‘)’ 
//		| '(' <valExp> <compOp> <valExp> ')'
		if (maybe(Atom.lParent)) {
			t = typeName();
			if (t != null) {
				accept(Atom.rParent);
				Node valNode = valExp(env);
				return new CastNode(t.getAtom(), valNode, env);
			}
			Node left = valExp(env);
			Node right = null;
			Token op;
			if ((op = numOp()) == null) 
				if ((op = boolOp()) == null)
					op = compOp();
			if (op == null) {
				accept(Atom.rParent);
				return left;
			}
			right = valExp(env);
			accept(Atom.rParent);
			switch (op.getAtom()) {
			case andKw:
				return new BoolAndNode(left, right, env);
			case orKw:
				return new BoolOrNode(left, right, env);
			case plusOp:
				return new AdditionNode(left, right, env);
			case minusOp:
				return new SubstractionNode(left, right, env);
			case divOp:
				return new DivNode(left, right, env);
			case multOp:
				return new MultiplicationNode(left, right, env);
			case equalsOp:
				return new EqualsNode(left, right, env);
			case notEqual:
				return new NotEqualNode(left, right, env);
			case lessEquals:
				return new LessEqualsNode(left, right, env);
			case lessThan:
				return new LessThanNode(left, right, env);
			case moreEquals:
				return new MoreEqualsNode(left, right, env);
			case moreThan:
				return new MoreThanNode(left, right, env);
			default:
				bin.parseError("Zły operator na pozycji " + op.getTextPos().toString());
			}
		}
//		| <constrUse> 
//		| ‘!’ <valExp> 
//		| <funcCall>
		Node n = constrUse(env);
		if (n != null)
			return n;
		if (maybe(Atom.notOp)) {
			n = valExp(env);
			return new BoolNotNode(n, env);
		}
		n = funcCall();
		if (n != null)
			return n;
		throw new ParseException("Oczekiwano valExp");
	}
	
	//<typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node constrUse(Environment env) throws ParseException {
		Token type = typeName();
		if (type != null) {
			accept(Atom.lParent);
			List<Node> args = new LinkedList<Node>();
			Node n = valExp(env);
			while (n != null) {
				args.add(n);
				if (!maybe(Atom.commaOp))
					break;
				n = valExp(env);
			}
			accept(Atom.rParent);
			return new ConstrNode(type.getAtom(), args, env);
		}
		return null;
	}
	
	private Token identifier() {
		if (currToken.getAtom() == Atom.identifier) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token literal() {
		if (currToken.getAtom() == Atom.doubleConst ||
			currToken.getAtom() == Atom.stringConst ||
			currToken.getAtom() == Atom.intConst ||
			currToken.getAtom() == Atom.trueKw ||
			currToken.getAtom() == Atom.falseKw ||
			currToken.getAtom() == Atom.nullKw ||
			currToken.getAtom() == Atom.nonImportantKw) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	//'=' | ‘+=’ | '-='
	private Token assignOp() throws ParseException {
		if (currToken.getAtom() == Atom.becomesOp ||
			currToken.getAtom() == Atom.plusBecomes ||
			currToken.getAtom() == Atom.minusBecomes) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token boolOp() {
		if (currToken.getAtom() == Atom.andKw ||
			currToken.getAtom() == Atom.orKw) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘==’ | ‘>=’ | ‘>’ | ‘<’ | ‘<=’ | ‘!=’
	private Token compOp() {
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
	
	//‘+’ | ‘-’ | ‘*’ | '/'
	private Token numOp() {
		if (currToken.getAtom() == Atom.plusOp ||
			currToken.getAtom() == Atom.minusOp ||
			currToken.getAtom() == Atom.multOp ||
			currToken.getAtom() == Atom.divOp) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘++’ | ‘--’
	private Token selfNumOp() {
		if (currToken.getAtom() == Atom.doublePlus ||
			currToken.getAtom() == Atom.doubleMinus) {
			Token thisToken = currToken;
			currToken = scan.nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token typeName() {
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
			return thisToken;
		}
		return null;	
	}
}
