package remembrall;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Scan {
	
	private Source src;
	private int curChar;
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
		} catch (FileNotFoundException e) {
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
		keywords.put("@every", Atom.everyKw);
		keywords.put("@starting", Atom.startKw);
		keywords.put("include", Atom.inclKw);
		keywords.put("null", Atom.nullKw);
		keywords.put("nonimportant", Atom.nonImportantKw);
	}
	
	public Atom nextAtom() {
		do { 
			while (isWhitespace(curChar)) 
				nextChar();
			if (curChar == -1) // EOF
				return Atom.unrecognized;
			else
				if (curChar == '/')
				{ 
					nextChar();
					if (curChar == '/') 
						return Atom.divOp;
					else
						do nextChar(); //skip comment
						while (curChar != '\n');
				}
		 } while (isWhitespace(curChar) || curChar == '/');
		atomStart = src.getPosition();

		String identifier = ""; //identifier or keyword
		if (isIdentifierStart(curChar))
		{
			do {
				identifier += (char)curChar;
				nextChar();
			} while (isIdentifierChar(curChar));
			Atom at = keywords.get(identifier);
			if (at != null)
				return at;
			else {
				lastIndentifier = identifier; 
				return Atom.identifier;
			}
		} else {
			if (isDigit(curChar))
				return readInt();
			else {
				switch(curChar) {
					case '"':
					case '\'':
						String str = "'";
						do {
							nextChar();
							str += (char)curChar;
						} while (curChar != '\'' && curChar != '"');
						lastStringConst = str.substring(1, str.length()-1);
						nextChar();
						return Atom.stringConst;
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
				}
			}
		}
		
		return Atom.unrecognized;
	}
	
	private boolean isWhitespace(int cChar) {
		if ((cChar > 0 && cChar < 33) || cChar == 127)
			return true;
		return false;
	}
	
	private boolean isIdentifierStart(int cChar) { //a-zA-Z_
		if ((cChar >= 'a' && cChar <= 'z') || 
				(cChar >= 'A' && cChar <= 'Z') || cChar == '_')
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

	private Atom readInt() { // curChar na pierwszej cyfrze
		if (curChar == '0') {
			nextChar();
			if (curChar == '.') 
				return readDouble(0);
			if (isWhitespace(curChar)) {
				lastIntConst = 0;
				return Atom.intConst;
			}
			src.scanError("Zły format stałej typu int");
			return Atom.unrecognized;
		}
		else {
			int number = 0;
			while (isDigit(curChar))
			{
				number = number * 10 + (curChar - '0');
				nextChar();
			}
			if (curChar == '.') 
				return readDouble(number);
			lastIntConst = number;
			return Atom.intConst;
		}
	}
	
	private Atom readDouble(int intPart) { // curChar == '.'
		nextChar();
		if (!isDigit(curChar)) // 90.
			return Atom.unrecognized;
		lastDoubleConst = intPart;
		int pos = 0;
		double part;
		while (isDigit(curChar)) {
			pos++;
			part = curChar - '0';
			part /= 10*pos;
			lastDoubleConst += part;
			nextChar();
		}
		return Atom.doubleConst; // i sprawdzic czy nie za duzy
	}
	
	public void run() {
		while (true) {
			System.out.print(Character.toChars(curChar));
			nextChar();
			if (curChar == -1)
				return;
		}
	}	
}
