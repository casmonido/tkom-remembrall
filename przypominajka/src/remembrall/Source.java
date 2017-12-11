package remembrall;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

class TextPos {
	int lineNum = 1;
	int charNum = 0;
	
	public String toString() {
		return "In line:" + lineNum + ", symbol:" + charNum;
	}
}

public class Source {

	private BufferedReader bufferedReader; 
	private int totalErrors = 0;
	private TextPos currentPos = new TextPos();

	public Source (String srcFileName) throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader fileReader = new InputStreamReader(new FileInputStream(srcFileName), "UTF8");
		bufferedReader = new BufferedReader(fileReader);
	}

	public int nextChar() throws IOException {
		if (bufferedReader == null)
			return -1;
		int c = bufferedReader.read();
		if (c == -1) 
			bufferedReader.close();
		adjustCurrentPos(c);
		return c;
	}

	public TextPos getPosition() {
		return currentPos;
	}
	
	public void scanError(TextPos startPos, String errorMsg) {
		totalErrors++;
		System.out.println(startPos.toString() + ": " + errorMsg);
	}
	
	public int getErrorsNum() {
		return totalErrors;
	}
	
	
	private void adjustCurrentPos(int c) {
		if (c == '\n' || currentPos.lineNum == 0) {
			currentPos.lineNum++;
			currentPos.charNum = 0;
		}
		else 
			currentPos.charNum++;
	}
}