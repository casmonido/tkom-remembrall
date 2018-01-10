package remembrall;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import remembrall.exceptions.ParseException;
import remembrall.exceptions.RuntimeException;
import remembrall.nodes.AdditionNode;
import remembrall.nodes.ArrayNode;
import remembrall.nodes.AssignNode;
import remembrall.nodes.BoolAndNode;
import remembrall.nodes.BoolNotNode;
import remembrall.nodes.BoolOrNode;
import remembrall.nodes.CastNode;
import remembrall.nodes.ConstrNode;
import remembrall.nodes.DivNode;
import remembrall.nodes.EqualsNode;
import remembrall.nodes.FunctionCallNode;
import remembrall.nodes.FunctionDefNode;
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
import remembrall.nodes.RepeatUntilNode;
import remembrall.nodes.ReturnNode;
import remembrall.nodes.SelfAdditionNode;
import remembrall.nodes.SelfSubstractionNode;
import remembrall.tokens.Token;
import remembrall.types.Type;
import remembrall.nodes.StartNode;
import remembrall.nodes.SubstractionNode;
import remembrall.nodes.VariableNode;
import remembrall.nodes.builtin.GetBirthdaysToday;
import remembrall.nodes.builtin.GetCurrentDate;
import remembrall.nodes.builtin.GetCurrentTime;
import remembrall.nodes.builtin.GetDuration;
import remembrall.nodes.builtin.GetSunset;
import remembrall.nodes.builtin.GetWeatherForecast;


public class Parser {
	private Token currToken;
	private ScanInterface scan;
	public StartNode root;
	private ErrorTracker bin;
	private Map<String,Node> functions = new HashMap<String,Node>();
	
	private void initFunctions() {
		functions.put("getCurrentTime", new GetCurrentTime());
		functions.put("getWeatherForecast", new GetWeatherForecast());
		functions.put("switchOnAlarm", new GetCurrentTime());
		functions.put("sleep", new GetWeatherForecast());
		functions.put("getBirthdaysToday", new GetBirthdaysToday());
		functions.put("sendSMS", new GetWeatherForecast());
		functions.put("getCurrentDate", new GetCurrentDate());
		
		functions.put("getDuration", new GetDuration());
		functions.put("getCurrentDayTime", new GetCurrentDate());
		functions.put("getSunset", new GetSunset());
		functions.put("getEndDate", new GetWeatherForecast());
	}
	
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
		//[<funcDef>*] 
		include();
		initFunctions();
		FunctionDefNode func;
		try {
			while ((func = funcDef()) != null)
				functions.put(func.getName(), func);
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.whenKw});
			bin.parseError(pe.getMessage());
		}
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
		Node condition = null;
		try {
			condition = boolExp(env);
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.lParent});
			bin.parseError(pe.getMessage());
		}
		try {
			accept(Atom.doKw);
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.dotOp});
			bin.parseError(pe.getMessage());
		}
		List<Node> list = null;
		try {
			list = parseInstructionList(env);
		} catch (ParseException pe) {
			skipTo(new Atom [] {Atom.dotOp});
			bin.parseError(pe.getMessage());
		}
		try {
			accept(Atom.dotOp);
		} catch (ParseException pe) {
			bin.parseError(pe.getMessage());
		}
		root = new StartNode(assignNodes, condition, list, env);
	}
	
	private List<Node> parseInstructionList(Environment env) throws ParseException {
		List<Node> list = new LinkedList<Node>();
		Node expr = null;
		while ((expr = valExp(env)) != null || (expr = voidExp(env)) != null) {
			if (expr != null)
				list.add(expr);
		}
		return list;
	}

	
	public void run(Environment env) {
		try {
			root.evalNode(env);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//'include' <fileName>
	public void include() {
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
		return;
	}
	
	private Type parseType() throws ParseException {
		Token typ = typeName();
		boolean arr = false;
		if (typ == null)
			return null;
		if (maybe(Atom.lBracket)) {
			accept(Atom.rBracket);
			arr = true;
		}
		return new Type(typ.getAtom(), arr);
	}
	
//	<everyAnno> <typeName> | [ ‘[‘ ‘]’ ]  <funcName> 
//	‘(‘ [<typeName> | <collectionType> <identifier> 
//			(‘,’ <typeName> | <collectionType> <identifier>)*] ‘)’ 
//	‘(‘ (<valExp> | <voidExp>)*  <retExp> ‘)’
	private FunctionDefNode funcDef() throws ParseException {
		Environment env = new Environment();
		Node root;
		if (maybe(Atom.atOp))
			if (maybe(Atom.everyKw))
				if (!everyAnno())
					throw new ParseException("Oczekiwano reszty everyAnno... ");
		Type retTyp = parseType();
		if (retTyp == null)
			return null;
		List<String> attrs = new LinkedList<String>();
		Token id, name = identifier();
		accept(Atom.lParent);
		Type typ = parseType();
		while (typ != null) {
			id = identifier();
			attrs.add((String)id.getValue());
			if (!maybe(Atom.commaOp))
				break;
			typ = parseType();
		}
		accept(Atom.rParent);
		accept(Atom.lParent);
		List<Node> list = parseInstructionList(env);
		if (!(list.get(list.size()-1) instanceof ReturnNode))
			throw new ParseException("Funkcja nic nie zwraca!");
		accept(Atom.rParent);
		return new FunctionDefNode((String)name.getValue(), attrs, list);
	}

	//<voidExp> -> <ifExp> | <repExp> | <retExp> | <procedureCall>
	private Node voidExp(Environment env) throws ParseException {
		Node n = ifExp(env);
		if (n != null)
			return n;
		n = repExp(env);
		if (n != null)
			return n;
		n = retExp(env); // tylko w funkcjach 
		if (n != null)
			return n;
//		n = procedureCall(env); // to moze byc parsowane jako functioncall
//		if (n != null)
//			return n;
		return null;
	}
	
	//<procedureCall> -> <identifier> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node procedureCall(Environment env) throws ParseException {
		Token id = identifier();
		if (id != null) {
			accept(Atom.lParent);
			List<Node> args = new LinkedList<Node>();
			Node n = null;
			n = valExp(env);
			while (n != null) {
				args.add(n);
				if (!maybe(Atom.commaOp))
					break;
				n = valExp(env);
			}
			accept(Atom.rParent);
			return new FunctionCallNode((String)id.getValue(), functions.get((String)id.getValue()), args);
		}
		return null;
	}
	
	//<retExp> -> ‘return’ <valExp>
	public Node retExp(Environment env) throws ParseException {
		if (maybe(Atom.returnKw)) {
			Node ret = valExp(env);
			if (ret == null)
				throw new ParseException("valExp puste");
			return new ReturnNode(ret, env);
		}
		return null;
	}
	
	//<repExp> -> ‘repeat’ ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	//			| 'repeatUntil' ‘(‘ <valExp> ‘)’ ‘(‘ (<valExp> | <voidExp>)* ‘)’
	private Node repExp(Environment env) throws ParseException {
		Atom repeat = null;
		if (maybe(Atom.repeatKw)) 
			repeat = Atom.repeatKw;
		else 
			if (maybe(Atom.repeatUntilKw)) 
				repeat = Atom.repeatUntilKw;
		if (repeat != null) {
			accept(Atom.lParent);
			Node cond = valExp(env);
			if (cond == null)
				throw new ParseException("valExp puste");
			accept(Atom.rParent);
			accept(Atom.lParent);
			List<Node> list = parseInstructionList(env);
			accept(Atom.rParent);
			return (repeat==Atom.repeatUntilKw)?new RepeatUntilNode(cond, list, env):new RepeatNode(cond, list, env);
		}
		return null;
	}
	
	//‘if’ <valExp> ‘(’ (<valExp> | <voidExp>)* ‘)’ [ ‘else’ ‘(’ (<valExp> | <voidExp> )* ‘)’ ]
	private Node ifExp(Environment env) throws ParseException {
		if (maybe(Atom.ifKw)) {
			Node cond = valExp(env);
			if (cond == null)
				throw new ParseException("valExp puste");
			accept(Atom.lParent);
			List<Node> elseList = null, ifList = parseInstructionList(env);
			accept(Atom.rParent);
			if (maybe(Atom.elseKw)) {
				//‘else’ ‘(’ (<valExp> | <voidExp> )* ‘)’
				accept(Atom.lParent);
				elseList = parseInstructionList(env);
				accept(Atom.rParent);
			}
			return elseList==null? new IfNode(cond, ifList, new LinkedList<Node>(), env) : new IfNode(cond, ifList, elseList, env);
		}
		return null;
	}
	
	// ‘(‘ <valExp> [<boolOp> <valExp>] ‘)’ | [‘!’] <valExp>
	private Node boolExp(Environment env) throws ParseException {
		if (maybe(Atom.lParent)) {
			Node l = valExp(env);
			if (l == null)
				throw new ParseException("valExp puste");
			Token op = boolOp();
			if (op != null) {
				Node r = valExp(env);
				if (r == null)
					throw new ParseException("valExp puste");
				accept(Atom.rParent);
				return op.getAtom()==Atom.andKw?new BoolAndNode(l, r, env):new BoolOrNode(l, r, env);
			}
			accept(Atom.rParent);
			return l;
		}
		accept(Atom.notOp);
		Node l = valExp(env);
		if (l == null)
			throw new ParseException("valExp puste");
		return new BoolNotNode(l, env);
	}

	//<assignExp> ::=  <variable> (<assignOp> <valExp>) | (‘:’ <typeName> ‘[‘ [<intLit>] ‘]’)
	private Node assignExp(Environment env) throws ParseException {
		VariableNode varNode = variable(env);
		if (varNode == null)
			return null;
		Token op = assignOp();
		if (op != null) {
			Node val = valExp(env);
			if (val == null)
				throw new ParseException("valExp puste");
			switch (op.getAtom()) {
			case becomesOp:
				return new AssignNode(varNode, val, env);
			case plusBecomes:
				return new PlusAssignNode(varNode, val, env);
			case minusBecomes:
				return new MinusAssignNode(varNode, val, env);
			default:
				break;
			}
		}
		if (maybe(Atom.colonOp)) {
			Token t = typeName();
			if (t == null) throw new ParseException("Brak typename...");
			accept(Atom.lBracket);
			Token intNode = literal();
			accept(Atom.rBracket);
			return new ArrayNode((int)intNode.getValue());
		}
		return null;
	}
	
	//<identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>]
	private VariableNode variable(Environment env) throws ParseException {
		if (currToken.getAtom() == Atom.identifier) {
			Token tAttr = null, ident = currToken;
			currToken = scan.nextToken();
			Node vNode = null;
			if (maybe(Atom.lBracket)) {
				vNode = valExp(env);
				if (vNode == null)
					throw new ParseException("Oczekiwano ");
				accept(Atom.rBracket);	
			}
			if (maybe(Atom.dotOp)) {
				tAttr = identifier();
				if (tAttr == null)
					throw new ParseException("Brak atrybutu");
			}
			return tAttr==null?new VariableNode((String)ident.getValue(), vNode, null, env):
					new VariableNode((String)ident.getValue(), vNode, (String)tAttr.getValue(), env);
		}
		return null;
	}
	
	private boolean annotation() throws ParseException {
		if (!maybe(Atom.atOp))
			return false;
		if (maybe(Atom.everyKw))
			return everyAnno();
		else {
			accept(Atom.startKw);
			return startAnno();
		}	
	}
	
//	<everyAnno> ::= <everyKw> ‘(‘ <intLit> ‘)’
	private boolean everyAnno() throws ParseException {
		accept(Atom.lParent);
		if (currToken.getAtom() == Atom.intConst) 
			currToken = scan.nextToken();
		else throw new ParseException("Brak parametru w anotacji @every");
		accept(Atom.rParent);
		return true;
	}

//	<startAnno> ::= <startKw>‘(‘ <intLit>, <intLit>, <intLit>, <intLit> ‘)’	
	private boolean startAnno() throws ParseException {
		accept(Atom.lParent);
		for (int i = 0; i < 3; i++) {
			if (currToken.getAtom() == Atom.intConst) 
				currToken = scan.nextToken();
			else throw new ParseException("Brak parametru w anotacji @start");
			accept(Atom.commaOp);
		}
		if (currToken.getAtom() == Atom.intConst) 
			currToken = scan.nextToken();
		else throw new ParseException("Brak parametru w anotacji @start");
		accept(Atom.rParent);
		return true;
	}

	private Node valExp(Environment env) throws ParseException {
//	      <literal> 
		Node n = null;
		Token t = literal();
		if (t != null)
			return new LiteralNode(t.getValue());
		t = identifier();
		if (t != null) {
//		| <identifier> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
			if (maybe(Atom.lParent))
			{
				List<Node> args = new LinkedList<Node>();
				n = valExp(env);
				while (n != null) {
					args.add(n);
					if (!maybe(Atom.commaOp))
						break;
					n = valExp(env);
				}
				accept(Atom.rParent);
				return new FunctionCallNode((String)t.getValue(), functions.get((String)t.getValue()), args);
				//return new FunctionCallNode(functions.resolve((String)t.getValue()), args);
			}
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>] <selfNumOp> 
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>] <assignOp> <valExp>
//		| <identifier> [‘[‘ <valExp> ‘]’] [‘.’ <identifier>]
			Token tAttr = null;
			if (maybe(Atom.lBracket)) {
				n = valExp(env);
				if (n == null)
					throw new ParseException("valExp puste");
				accept(Atom.rBracket);	
			}
			if (maybe(Atom.dotOp))
				tAttr = identifier();
			Token op = selfNumOp();
			VariableNode var = tAttr==null? new VariableNode((String)t.getValue(), n, null, env):
					new VariableNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
			if (op != null)
				switch (op.getAtom()) {
				case doublePlus:
					return new SelfAdditionNode(var, env);
				case doubleMinus:
					return new SelfSubstractionNode(var, env);
				default:
					break;
				}
			op = assignOp();
			if (op != null) {
				Node value = valExp(env);
				if (value == null)
					throw new ParseException("valExp puste");
				VariableNode varNode = tAttr==null?
						new VariableNode((String)t.getValue(), n, null, env)
						:new VariableNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
				switch (op.getAtom()) {
				case becomesOp:
					return new AssignNode(varNode, value, env);
				case plusBecomes:
					return new PlusAssignNode(varNode, value, env);
				case minusBecomes:
					return new MinusAssignNode(varNode, value, env);
				default:
					break;
				}
			}
			return tAttr==null?new VariableNode((String)t.getValue(), n, null, env):
				new VariableNode((String)t.getValue(), n, (String)tAttr.getValue(), env);
		}
//		| ‘(‘ <typeName> [ '[' ']' ] ‘)’ <valExp> // uwaga constrUse
//		| '(' <valExp> <numOp> <valExp> ')'
//		| ‘(‘ <valExp> [<boolOp> <valExp>] ‘)’ 
//		| '(' <valExp> <compOp> <valExp> ')'
		if (maybe(Atom.lParent)) {
			t = typeName();
			boolean array = false;
			if (t != null) {
				if (maybe(Atom.lBracket)) {
					accept(Atom.rBracket);
					array = true;
				}
				if (maybe(Atom.rParent)) {
					Node valNode = valExp(env);
					if (valNode == null)
						throw new ParseException("valExp puste");
					return new CastNode(t.getAtom(), array, valNode, env);
				}
				accept(Atom.lParent);
				List<Node> args = new LinkedList<Node>();
				n = valExp(env);
				while (n != null) {
					args.add(n);
					if (!maybe(Atom.commaOp))
						break;
					n = valExp(env);
				}
				accept(Atom.rParent);
				Node left = new ConstrNode(t.getAtom(), args, env);
				Token op;
				if ((op = numOp()) == null) 
					if ((op = boolOp()) == null)
						op = compOp();
				if (op == null) {
					accept(Atom.rParent);
					return left;
				}
				Node right = valExp(env);
				if (right == null)
					throw new ParseException("valExp puste");
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
			Node left = valExp(env);
			if (left == null)
				throw new ParseException("valExp puste");
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
			if (right == null)
				throw new ParseException("valExp puste");
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
		n = constrUse(env);
		if (n != null)
			return n;
		if (maybe(Atom.notOp)) {
			n = valExp(env);
			return new BoolNotNode(n, env);
		}
//		| <everyAnno> [<startAnno>] <identifier> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
		if (annotation()) {
			annotation(); //kolejna
			Environment e = new Environment();
			t = identifier();
			accept(Atom.lParent);
			List<Node> args = new LinkedList<Node>();
			try {n = valExp(e);} catch (ParseException pe) {n = null;}
			while (n != null) {
				args.add(n);
				if (!maybe(Atom.commaOp))
					break;
				try {n = valExp(e);} catch (ParseException pe) {n = null;}
			}
			accept(Atom.rParent);
			return new FunctionCallNode((String)t.getValue(), functions.get((String)t.getValue()), args);
			//return new FunctionCallNode(functions.resolve((String)t.getValue()), args);
		}
//		| ‘[‘ <literal> (‘,’ <literal>)* ‘]’
		if (maybe(Atom.lBracket)) {
			List<Object> elements = new LinkedList<Object>();
			t = literal();
			while (t != null) {
				elements.add(t.getValue());
				if (!maybe(Atom.commaOp))
					break;
				t = literal();
			}
			accept(Atom.rBracket);
			return new ArrayNode(elements.toArray());
		}
		//throw new ParseException("Oczekiwano valExp");
		return null;
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
	private Token assignOp() {
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
