package remembrall;

public class TextPos {
	int lineNum = 1;
	int charNum = 1;
	
	public TextPos() {}
	public TextPos(TextPos t) {
		lineNum = t.lineNum;
		charNum = t.charNum;
	}
	
	public String toString() {
		return "line:" + lineNum + ", symbol:" + charNum;
	}
}