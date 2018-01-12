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
import remembrall.nodes.IdentNode;
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
import remembrall.tokens.BasicToken;
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
	private Token next = null;
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
	
	private void rejectCur(Token t) {
		next = currToken;
		currToken = t;
	}
	
	private void nextToken() {
		if (next != null) {
			currToken = next;
			next = null;
		} 
		else
			currToken = scan.nextToken();
	}
	
	private Token accept(Atom atom) throws ParseException {
		if (currToken.getAtom() == atom) {
			Token thisToken = currToken;
			nextToken();
			return thisToken;
		}
		throw new ParseException("Na pozycji " + currToken.getTextPos().toString() + 
				" spodziewany atom: " + atom +
				", znaleziono: " + currToken.getAtom());
	}
	
	private boolean maybe(Atom atom) {
		if (currToken.getAtom() == atom) {
			nextToken();
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
			nextToken();
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
			Node aNode = parseAssignExpression(env);
			while (aNode != null) {
				assignNodes.add(aNode);
				aNode = parseAssignExpression(env);
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
			condition = parseAssignExpression(env);
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
	
	private List<Token> parseLiteralList(Environment env) throws ParseException {
		List<Token> list = new LinkedList<Token>();
		Token expr = null;
		while ((expr = literal()) != null) {
			if (expr != null)
				list.add(expr);
		}
		return list;
	}
	
	private List<Node> parseInstructionList(Environment env) throws ParseException {
		List<Node> list = new LinkedList<Node>();
		Node expr = null;
		while ((expr = parseAssignExpression(env)) != null || (expr = voidExp(env)) != null) {
			if (expr != null)
				list.add(expr);
		}
		return list;
	}
	
	private List<String> parseArgsDef() throws ParseException {
		List<String> attrs = new LinkedList<String>();
		Type typ = parseType();
		while (typ != null) {
			String id = identifier();
			if (id == null)
				throw new ParseException("Lista argumentów w definicj funkcji...");
			attrs.add(id);
			if (!maybe(Atom.commaOp))
				break;
			typ = parseType();
		}
		return attrs;
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
			try {
				accept(Atom.stringConst);
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
			// arytm?
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
		if (maybe(Atom.atOp))
			if (maybe(Atom.everyKw))
				if (!everyAnno())
					throw new ParseException("Oczekiwano reszty everyAnno... ");
		Type retTyp = parseType();
		if (retTyp == null)
			return null;
		String name = identifier();
		accept(Atom.lParent);
		List<String> attrs = parseArgsDef();
		accept(Atom.rParent);
		accept(Atom.lParent);
		List<Node> list = parseInstructionList(env);
		if (!(list.get(list.size()-1) instanceof ReturnNode))
			throw new ParseException("Funkcja nic nie zwraca!");
		accept(Atom.rParent);
		return new FunctionDefNode(name, attrs, list);
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
		return null;
	}
	
	public List<Node> parseArgs(Environment env) throws ParseException {
		List<Node> args = new LinkedList<Node>();
		Node n = null;
		while ((n = parseAssignExpression(env)) != null) {
			args.add(n);
			if (!maybe(Atom.commaOp))
				break;
		}
		return args;
	}
	
	//<retExp> -> ‘return’ <valExp>
	public Node retExp(Environment env) throws ParseException {
		if (maybe(Atom.returnKw)) {
			Node ret = parseAssignExpression(env);
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
			Node cond = parseAssignExpression(env);
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
			Node cond = parseAssignExpression(env);
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
			nextToken();
		else throw new ParseException("Brak parametru w anotacji @every");
		accept(Atom.rParent);
		return true;
	}

//	<startAnno> ::= <startKw>‘(‘ <intLit>, <intLit>, <intLit>, <intLit> ‘)’	
	private boolean startAnno() throws ParseException {
		accept(Atom.lParent);
		for (int i = 0; i < 3; i++) {
			if (currToken.getAtom() == Atom.intConst) 
				nextToken();
			else throw new ParseException("Brak parametru w anotacji @start");
			accept(Atom.commaOp);
		}
		if (currToken.getAtom() == Atom.intConst) 
			nextToken();
		else throw new ParseException("Brak parametru w anotacji @start");
		accept(Atom.rParent);
		return true;
	}
	
	// [@annot] <ident> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’
	private Node parseFuncCall(Environment env) throws ParseException {
		boolean isFunc = false;
		if (annotation()) {
			isFunc = true;
			annotation();
		}
		String t = identifier();
		if (t == null && isFunc) 
			throw new ParseException("Po anotacji oczekiwane wywołanie funkcji");
		if (t == null)
			return null;
		if (maybe(Atom.lParent))
		{
			List<Node> args = parseArgs(env);
			accept(Atom.rParent);
			return new FunctionCallNode(t, functions.get(t), args);
		}
		return new IdentNode(t);
	}
	
//	  <lit>	
//	| ident albo function call
//	| [ a, b, c]
//	| <typeName> ‘(‘ <valExp> (‘,’ <valExp> )* ‘)’ <-konstruktor
//	| ('(' <Expr> ')') 
//	| <typeName> <Term>
	private Node parseTerm(Environment env) throws ParseException {
		Token t = literal();
		if (t != null)
			return new LiteralNode(t.getValue());
		Node n = parseFuncCall(env);
		if (n != null) 
			return n;
		n = arrayConstruct(env);
		if (n != null) 
			return n;
		n = constrUse(env);
		if (n != null) 
			return n;
		if (maybe(Atom.lParent)) {
			Token typ = typeName();
			if (typ != null)
				if (maybe(Atom.rParent)) {
					n = parseTermAddons(env);
					return new CastNode(typ, n, env);
				} else
					rejectCur(typ);
			Node expr = parseLogic(env);
			if (expr == null)
				throw new ParseException("W nawiasie oczekiwano wyrażenia");
			accept(Atom.rParent);
			return expr;
		}
		return null;
	}
	
	private Node parseVariable(Environment env) throws ParseException {
		Node term = parseTerm(env);
		if (term != null) {
			String tAttr = null;
			Node num = null;
			if (maybe(Atom.lBracket)) {
				num = parseArytm(env);
				if (num == null)
					throw new ParseException("W [] : oczekiwano wyrażenia o wartości arytmetycznej");
				accept(Atom.rBracket);	
			}
			Token dot;
			if ((dot = currToken).getAtom() == Atom.dotOp) {
				nextToken();
				tAttr = identifier();
				if (tAttr == null)
					rejectCur(dot);
			}
			return new VariableNode(term, num, tAttr, env);
		}
		return null;
	}

	private Node parseTermAddons(Environment env) throws ParseException {
		Node var = parseVariable(env);
		if (var != null) {
			Token op = unaryNumOp();
			if (op != null)
				switch (op.getAtom()) {
				case doublePlus:
					return new SelfAdditionNode((VariableNode)var, env);
				case doubleMinus:
					return new SelfSubstractionNode((VariableNode)var, env);
				default:
					break;
				}
			return var;
		}
		if (maybe(Atom.notOp)) {
			var = parseTermAddons(env);
			if (var == null)
				throw new ParseException("Brak wyrażenia po operatorze !");
			return new BoolNotNode(var, env);
		}
		return null;
	}
	
	private Node parseMult(Environment env) throws ParseException {
		Node left = parseTermAddons(env);
		Token op = null; 
		while ((op = doubleOp()) != null) 
		{
			Node right = parseLogic(env);
			if (right == null)
				throw new ParseException("Po operatorze spodziewane wyrażenie!");
			switch (op.getAtom()) {
			case divOp:
				left = new DivNode(left, right, env);
				break;
			case multOp:
				left = new MultiplicationNode(left, right, env);
				break;
			default:
				bin.parseError("Zły operator na pozycji " + op.getTextPos().toString());
			}
		}
		return left;
	}
	
	private Node parseArytm(Environment env) throws ParseException {
		Node left = parseMult(env);
		if (left == null)
			return null;
		Token op; 
		while ((op = arytmOp()) != null) {
			Node right = parseLogic(env);
			if (right == null)
				throw new ParseException("Po operatorze spodziewane wyrażenie!");
			switch (op.getAtom()) {
			case plusOp:
				left = new AdditionNode(left, right, env);
				break;
			case minusOp:
				left = new SubstractionNode(left, right, env);
				break;
			default:
				bin.parseError("Zły operator na pozycji " + op.getTextPos().toString());
			}
		}
		return left;
	}
	
	private Node parseRelat(Environment env) throws ParseException {
		Node left = parseArytm(env);
		if (left == null)
			return null;
		Token op; 
		while ((op = compOp()) != null) {
			Node right = parseLogic(env);
			if (right == null)
				throw new ParseException("Po operatorze spodziewane wyrażenie!");
			switch (op.getAtom()) {
			case equalsOp:
				left = new EqualsNode(left, right, env);
				break;
			case notEqual:
				left = new NotEqualNode(left, right, env);
				break;
			case lessEquals:
				left = new LessEqualsNode(left, right, env);
				break;
			case lessThan:
				left = new LessThanNode(left, right, env);
				break;
			case moreEquals:
				left = new MoreEqualsNode(left, right, env);
				break;
			case moreThan:
				left = new MoreThanNode(left, right, env);
				break;
			default:
				bin.parseError("Zły operator na pozycji " + op.getTextPos().toString());
			}
		}
		return left;
	}
	
	private Node parseLogic(Environment env) throws ParseException {
		Node right, left = parseRelat(env);
		if (left == null)
			return null;
		Token op;
		while ((op = boolOp()) != null) {
			if ((right = parseLogic(env)) == null)
				throw new ParseException("Po operatorze spodziewane wyrażenie!");
			switch (op.getAtom()) {
			case andKw:
				left = new BoolAndNode(left, right, env);
				break;
			case orKw:
				left = new BoolOrNode(left, right, env);
				break;
			default:
				bin.parseError("Zły operator na pozycji " + op.getTextPos().toString());
			}
		}
		return left;
	}

	private Node parseAssignExpression(Environment env) throws ParseException {
		Node left = parseLogic(env);
		Token op = assignOp();
		if (op != null) {
			if (left == null || !(left instanceof VariableNode)) 
				throw new ParseException("Przypisywać można tylko do identifiera");
			Node right = parseAssignExpression(env);
			if (right == null)
				throw new ParseException("Oczekiwano warości którą można przypisać");
			switch (op.getAtom()) {
			case becomesOp:
				left = new AssignNode((VariableNode)left, right, env);
				break;
			case plusBecomes:
				left = new PlusAssignNode((VariableNode)left, right, env);
				break;
			case minusBecomes:
				left = new MinusAssignNode((VariableNode)left, right, env);
				break;
			default:
				break;
			}
		}
		if (left != null && maybe(Atom.colonOp)) {
			Type typ = parseType();
			if (typ == null) throw new ParseException("Brak typename...");
			return new ArrayNode(new LinkedList<>());
		}
		return left;
	}

	private Node arrayConstruct(Environment env) throws ParseException {
		if (maybe(Atom.lBracket)) {
			List<Token> list = parseLiteralList(env);
			accept(Atom.rBracket);
			return new ArrayNode(list);
		}
		return null;
	}
	
	private Node constrUse(Environment env) throws ParseException {
		Token type = typeName();
		if (type != null) {
			accept(Atom.lParent);
			List<Node> args = parseArgs(env);
			accept(Atom.rParent);
			return new ConstrNode(type.getAtom(), args, env);
		}
		return null;
	}
	
	private String identifier() {
		if (currToken.getAtom() == Atom.identifier) {
			Token thisToken = currToken;
			nextToken();
			return (String)thisToken.getValue();
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
			nextToken();
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
			nextToken();
			return thisToken;
		}
		return null;
	}
	
	private Token boolOp() {
		if (currToken.getAtom() == Atom.andKw ||
			currToken.getAtom() == Atom.orKw) {
			Token thisToken = currToken;
			nextToken();
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
			nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘+’ | ‘-’ | ‘*’ | '/'
	private Token doubleOp() {
		if (currToken.getAtom() == Atom.multOp ||
			currToken.getAtom() == Atom.divOp) {
			Token thisToken = currToken;
			nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘+’ | ‘-’ | ‘*’ | '/'
	private Token arytmOp() {
		if (currToken.getAtom() == Atom.plusOp ||
			currToken.getAtom() == Atom.minusOp) {
			Token thisToken = currToken;
			nextToken();
			return thisToken;
		}
		return null;
	}
	
	//‘++’ | ‘--’
	private Token unaryNumOp() {
		if (currToken.getAtom() == Atom.doublePlus ||
			currToken.getAtom() == Atom.doubleMinus) {
			Token thisToken = currToken;
			nextToken();
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
			nextToken();
			return thisToken;
		}
		return null;	
	}
}
