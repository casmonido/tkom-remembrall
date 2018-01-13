package remembrall;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class Source {

	private BufferedReader bufferedReader; 
	private TextPos currentPos = new TextPos();

	public Source (String srcFileName) throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader fileReader = new InputStreamReader(new FileInputStream(srcFileName), "UTF8");
		bufferedReader = new BufferedReader(fileReader);
	}

	public int nextChar() throws IOException {
		if (bufferedReader == null)
			return -1;
		int c = bufferedReader.read();
		if (c == -1) {
			bufferedReader.close();
			bufferedReader = null;
		}
		adjustCurrentPos(c); 
		return c;
	}

	public TextPos getPosition() {
		return new TextPos(currentPos);
	}
	
	private void adjustCurrentPos(int c) {
		if (c == -1)
			return;
		if (c == '\n') {
			currentPos.lineNum++;
			currentPos.charNum = 0;
		}
		else 
			currentPos.charNum++;
	}
}