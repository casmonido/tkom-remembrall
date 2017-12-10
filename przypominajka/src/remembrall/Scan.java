package remembrall;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Scan {
	
	public static long MAX_INT = 2147483647;
	
	private Source src;
	private int curChar = -1;
	private HashMap<String, Atom> keywords = new HashMap<String, Atom>(25);
	private TextPos atomStart;

	private String lastStringConst;
	private String lastIndentifier;
	private int lastIntConst;
	private double lastDoubleConst;
	
	public String getIdentifier() {
		return lastIndentifier;
	}
	public String getStringConst() {
		return lastStringConst;
	}	
	public int getIntConst() {
		return lastIntConst;
	}
	public double getDoubleConst() {
		return lastDoubleConst;
	}
	
	public Scan(String filePath) {
		initKeywords();
		try {
			src = new Source(filePath);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		nextChar();
	}
	
	private void nextChar() {
		try {
			curChar = src.nextChar();
		} catch (IOException e) {
			System.err.println("Błąd odczytu pliku źródłowego."); 
			curChar = -1; // postaraj sie szybko skonczyc przetwarzanie
		} 
	}
	
	private void initKeywords() {
		keywords.put("int", Atom.typeInt);
		keywords.put("double", Atom.typeDouble);
		keywords.put("string", Atom.typeString);
		keywords.put("bool", Atom.typeBool);
		keywords.put("time", Atom.typeTime);
		keywords.put("datetime", Atom.typeDatetime);
		keywords.put("location", Atom.typeLocation);
		keywords.put("weather", Atom.typeWeather);
		keywords.put("netinfo", Atom.typeNetInfo);
		keywords.put("true", Atom.trueKw);
		keywords.put("false", Atom.falseKw);
		keywords.put("repeat", Atom.repeatKw);
		keywords.put("return", Atom.returnKw);
		keywords.put("repeatUntil", Atom.repeatUntilKw);
		keywords.put("if", Atom.ifKw);
		keywords.put("else", Atom.elseKw);
		keywords.put("and", Atom.andKw);
		keywords.put("or", Atom.orKw);
		keywords.put("When", Atom.whenKw);
		keywords.put("do", Atom.doKw);
		keywords.put("every", Atom.everyKw);
		keywords.put("starting", Atom.startKw);
		keywords.put("include", Atom.inclKw);
		keywords.put("null", Atom.nullKw);
		keywords.put("nonimportant", Atom.nonImportantKw);
	}
	
	public Atom nextAtom() { //curChar na 'kolejnym' znaku
		do {
			while (isWhitespace(curChar)) 
				nextChar();
			if (curChar == -1) // EOF
				return Atom.eof;
			else
				if (curChar == '/')
				{ 
					nextChar();
					if (curChar != '/') 
						return Atom.divOp;
					else
						do nextChar(); 
						while (curChar != '\n');
				}
		 } while (isWhitespace(curChar) || curChar == '/');

		atomStart = src.getPosition(); // wazne w przypadku 'dlugich' atomow
		if (isIdentifierStart(curChar))
			return scanIdentifierOrKeyword();
		if (isDigit(curChar))
			return scanNumber(1);
		switch(curChar) {
		case '"':
		case '\'':
			return scanStringConst();
		case '+':
			nextChar();
			if (curChar == '+') {
				nextChar();
				return Atom.doublePlus;
			}
			if (curChar == '=') {
				nextChar();
				return Atom.plusBecomes;
			}
			else return Atom.plusOp;
		case '-':
			nextChar();
			if (isDigit(curChar))
				return scanNumber(-1);
			if (curChar == '-') {
				nextChar();
				return Atom.doubleMinus;
			}
			if (curChar == '=') {
				nextChar();
				return Atom.minusBecomes;
			}
			else return Atom.minusOp;	
		case '=':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return Atom.equalsOp;
			}
			else return Atom.becomesOp;
		case '!':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return Atom.notEqual;
			}
			else return Atom.notOp;
		case '<':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return Atom.lessEquals;
			}
			else return Atom.lessThan;
		case '>':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return Atom.moreEquals;
			}
			else return Atom.moreThan;
		case '*': 
			nextChar();
			return Atom.multOp;
		case '/': 
			nextChar();
			return Atom.divOp;
		case '.': 
			nextChar();
			return Atom.dotOp;
		case ',': 
			nextChar();
			return Atom.commaOp;
		case '[': 
			nextChar();
			return Atom.lBracket;
		case ']': 
			nextChar();
			return Atom.rBracket;
		case '(': 
			nextChar();
			return Atom.lParent;
		case ')': 
			nextChar();
			return Atom.rParent;
		case '@':
			nextChar();
			return Atom.atOp;
		}
		if (curChar == -1) 
			return Atom.eof;
		src.scanError(atomStart, "Nierozpoznany znak: " + (char) curChar);
		nextChar();
		return Atom.unrecognizedSym; // np ^
	}
	
	private Atom scanStringConst() { // start na "
		String str = "";
		boolean escaped = false;
		nextChar();
		while (true) {
			if (curChar == -1) {
				src.scanError(atomStart, "Niezakończony string");
				return Atom.eof;
			}
			if (escaped) {
				if (curChar!='b' && curChar!='t' && curChar!='n' && curChar!='f' 
						&& curChar!='r' && curChar!='"' && curChar!='\'' && curChar!='\\') {
					src.scanError(atomStart, "Po escape (\\) mogą występować tylko: b, t, n, f, r, \", ', \\");
					return skipRestOfTheString();
				}
				escaped = false;
			}
			else {
				if (curChar == '"' || curChar == '\'') 
					break;
				if (curChar == '\\') 
					escaped = true;
			}
			str += (char)curChar;
			nextChar();
		}
		lastStringConst = str;
		nextChar();
		return Atom.stringConst;
	}
	
	
	private Atom skipRestOfTheString() {
		boolean escaped = false;
		while (true) {
			if (curChar == -1) {
				src.scanError(atomStart, "Niezakończony string");
				return Atom.eof;
			}
			if (escaped) 
				escaped = false;
			else {
				if (curChar == '"' || curChar == '\'') 
					break;
				if (curChar == '\\') 
					escaped = true;
			}
			nextChar();
		}
		nextChar();
		return Atom.stringConst;
	}
	
	private Atom scanIdentifierOrKeyword() {
		String identifier = ""; 
		while (isIdentifierChar(curChar)) {
			identifier += (char) curChar;
			nextChar();
		} 
		Atom at = keywords.get(identifier);
		if (at != null)
			return at;
		else {
			lastIndentifier = identifier; 
			return Atom.identifier;
		}
	}
	
	
	private boolean isWhitespace(int cChar) {
		if ((cChar > 0 && cChar < 33) || cChar == 127)
			return true;
		return false;
	}
	
	private boolean isIdentifierStart(int cChar) { //a-zA-Z_ lub znaki > 127
		if ((cChar >= 'a' && cChar <= 'z') || 
				(cChar >= 'A' && cChar <= 'Z') || cChar == '_' || cChar > 217)
			return true;
		return false;
	}
	
	private boolean isDigit(int cChar) {
		if ((cChar >= '0' && cChar <= '9'))
			return true;
		return false;
	}
	
	private boolean isIdentifierChar(int cChar) { 
		if (isIdentifierStart(cChar) || isDigit(cChar))
			return true;
		return false;
	}

	private Atom scanNumber(int multiplier) { //curChar na pierwszej cyfrze
		long number = 0;
		while (isDigit(curChar))
		{
			number = number * 10 + (curChar - '0');
			if (number > MAX_INT) {
				src.scanError(atomStart, "Zbyt długa stała liczbowa!");
				return skipRestOfTheNumber(); // symbolicznie zwracamy jakis typ
			}
			nextChar();
		}
		if (curChar == '.') 
			return scanDouble(number, multiplier);
		lastIntConst = multiplier*(int)number;
		return Atom.intConst;
	}
	
	private Atom skipRestOfTheNumber() {
		do nextChar(); 
		while (isDigit(curChar));
		if (curChar == '.') {
			do nextChar(); 
			while (isDigit(curChar));
			return Atom.doubleConst;
		}
		return Atom.intConst;
	}
	
	private Atom scanDouble(long number, int multiplier) { // curChar == '.'
		nextChar();
		if (!isDigit(curChar)) { // 90.
			src.scanError(atomStart, "Zły format stałej typu double");
			return Atom.doubleConst;
		}
		lastDoubleConst = number;
		int pos = 0;
		double part;
		while (isDigit(curChar)) { //
			pos++;
			part = curChar - '0';
			part /= 10*pos;
			lastDoubleConst += part;
			nextChar();
		}
		lastDoubleConst *= multiplier;
		return Atom.doubleConst;
	}
	
	public void endReport() {
		System.out.println("Znaleziono " + src.getErrorsNum() + " błędów.");
	}
	
}
