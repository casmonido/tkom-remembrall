package remembrall;

import java.io.IOException;
import java.util.HashMap;
import remembrall.tokens.*;

public class Scan implements ScanInterface {
	
	private Source src;
	private int curChar = -1;
	private HashMap<String, Atom> keywords = new HashMap<String, Atom>(25);
	private TextPos atomStart;
	private ErrorTracker errTracker;

	
	public Scan(Source src, ErrorTracker errTr) {
		initKeywords();
		this.src = src;
		errTracker = errTr;
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
	
	public Token nextToken() { //curChar na 'kolejnym' znaku
		do {
			while (isWhitespace(curChar)) 
				nextChar();
			if (curChar == -1) // EOF
				return new BasicToken(Atom.eof, src.getPosition());
			else
				if (curChar == '/')
				{
					atomStart = src.getPosition();
					nextChar();
					if (curChar != '/') 
						return new BasicToken(Atom.divOp, atomStart);
					else
						do nextChar(); 
						while (curChar != '\n');
				}
		 } while (isWhitespace(curChar) || curChar == '/');

		atomStart = src.getPosition(); // mozliwy start 'dlugich' atomow
		if (isIdentifierStart(curChar))
			return scanIdentifierOrKeyword();
		if (isDigit(curChar))
			return scanNumber("");
		switch(curChar) {
		case '"':
		case '\'':
			return scanStringConst(curChar);
		case '+':
			nextChar();
			if (curChar == '+') {
				nextChar();
				return new BasicToken(Atom.doublePlus, atomStart);
			}
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.plusBecomes, atomStart);
			}
			else 
				return new BasicToken(Atom.plusOp, atomStart);
		case '-':
			nextChar();
			if (isDigit(curChar))
				return scanNumber("-");
			if (curChar == '-') {
				nextChar();
				return new BasicToken(Atom.doubleMinus, atomStart);
			}
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.minusBecomes, atomStart);
			}
			else 
				return new BasicToken(Atom.minusOp, atomStart);	
		case '=':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.equalsOp, atomStart);
			}
			else 
				return new BasicToken(Atom.becomesOp, atomStart);
		case '!':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.notEqual, atomStart);
			}
			else 
				return new BasicToken(Atom.notOp, atomStart);
		case '<':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.lessEquals, atomStart);
			}
			else 
				return new BasicToken(Atom.lessThan, atomStart);
		case '>':
			nextChar();
			if (curChar == '=') {
				nextChar();
				return new BasicToken(Atom.moreEquals, atomStart);
			}
			else 
				return new BasicToken(Atom.moreThan, atomStart);
		case '*': 
			nextChar();
			return new BasicToken(Atom.multOp, atomStart);
		case '/': 
			nextChar();
			return new BasicToken(Atom.divOp, atomStart);
		case '.': 
			nextChar();
			return new BasicToken(Atom.dotOp, atomStart);
		case ',': 
			nextChar();
			return new BasicToken(Atom.commaOp, atomStart);
		case '[': 
			nextChar();
			return new BasicToken(Atom.lBracket, atomStart);
		case ']': 
			nextChar();
			return new BasicToken(Atom.rBracket, atomStart);
		case '(': 
			nextChar();
			return new BasicToken(Atom.lParent, atomStart);
		case ')': 
			nextChar();
			return new BasicToken(Atom.rParent, atomStart);
		case '@':
			nextChar();
			return new BasicToken(Atom.atOp, atomStart);
		case ':':
			nextChar();
			return new BasicToken(Atom.colonOp, atomStart);
		}
		errTracker.scanError(atomStart, "Nierozpoznany znak: " + (char) curChar);
		nextChar(); 
		return new BasicToken(Atom.unrecognizedSym, atomStart); // np ^
	}
	
	private Token scanStringConst(int stringStart) { // start na "
		String str = "";
		nextChar();
		while (curChar != stringStart) {
			if (curChar == -1) {
				errTracker.scanError(atomStart, "Niezakończony string");
				return new BasicToken(Atom.eof, atomStart);
			}
			if (curChar == '\\') { 
				nextChar();
				if (curChar!=stringStart && curChar!='\\') { // -1 również wywoła ten error
					errTracker.scanError(atomStart, "Po escape (\\) mogą występować tylko: \" lub ', \\"); //b, t, n, f, r,
					return skipRestOfTheString(stringStart); 
				}
			}
			str += (char)curChar; 
			nextChar();
		}
		nextChar();
		return new StringToken(Atom.stringConst, atomStart, str);
	}
	
	
	private Token skipRestOfTheString(int stringStart) {
		while (curChar != stringStart) {
			if (curChar == -1) {
				errTracker.scanError(atomStart, "Niezakończony string");
				return new BasicToken(Atom.eof, atomStart);
			}
			if (curChar == '\\') { 
				nextChar();
			}
			nextChar();
		}
		nextChar();
		return new BasicToken(Atom.unrecognizedSym, atomStart);
	}
	
	private Token scanIdentifierOrKeyword() {
		String identifier = ""; 
		while (isIdentifierChar(curChar)) {
			identifier += (char) curChar;
			nextChar();
		} 
		Atom at = keywords.get(identifier);
		if (at != null) 
			 //m.in. nullKw, nonImportantKw
			switch (at) {
			case falseKw:
				return new BasicToken(at, atomStart, new Boolean(false));
			case trueKw:
				return new BasicToken(at, atomStart, new Boolean(true));
			default:
				return new BasicToken(at, atomStart);
			}
		else 
			return new StringToken(Atom.identifier, atomStart, identifier);
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
	
	private Token scanNumberThatStartsWithZero(String scannedPart) {//curChar jest 0
		scannedPart += (char)curChar;
		nextChar();
		if (curChar == '.')
			return scanDouble(scannedPart);
		else
			if (isDigit(curChar)) {
				errTracker.scanError(atomStart, "Błędny format stałej liczbowej - zera wiodące");
				discardErroneousNum();
				return new BasicToken(Atom.unrecognizedSym, atomStart);
			}
		return new IntToken(Atom.intConst, atomStart, 0);	
	}
	
	private void discardErroneousNum() {
		while (isDigit(curChar))
			nextChar();
		if (curChar == '.')
			nextChar();
		while (isDigit(curChar))
			nextChar();
	}

	private Token scanNumber(String scannedPart) { //curChar na pierwszej cyfrze
		if (curChar == '0') 
			return scanNumberThatStartsWithZero(scannedPart);
		do
		{
			scannedPart += (char)curChar;
			nextChar();
		} while (isDigit(curChar));
		if (curChar == '.') 
			return scanDouble(scannedPart);
		else {
			long i = 0;
			try {
				i = Long.parseLong(scannedPart);
			} catch (NumberFormatException ne) { // za długi na long
				return new DoubleToken(Atom.doubleConst, atomStart, Double.parseDouble(scannedPart));
			}
			return new IntToken(Atom.intConst, atomStart, i);
		}
	}
	
	private Token scanDouble(String scannedPart) { //curChar na .
		scannedPart += (char)curChar;
		nextChar();
		if (!isDigit(curChar)) {
			errTracker.scanError(atomStart, "Nieprawidłowy format stałej double - brak cyfr po kropce!");
			return new BasicToken(Atom.unrecognizedSym, atomStart);
		}
		do
		{
			scannedPart += (char)curChar;
			nextChar();
		} while (isDigit(curChar));
		return new DoubleToken(Atom.doubleConst, atomStart, Double.parseDouble(scannedPart));
	}
	
	
	public void printEndReport() {
		System.out.println("Znaleziono " + errTracker.getErrorsNum() + " błędów.");
	}
	
}
