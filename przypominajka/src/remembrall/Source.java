package remembrall;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class TextPos {
	int lineNum = 0;
	int charNum = 0;
}

public class Source {

	private BufferedReader bufferedReader; //trzeba zrobic .close()
	private int totalErrors = 0;
	private int errorsInLine = 0;
	private TextPos currentPos = new TextPos();

	public Source (String srcFileName) throws FileNotFoundException {
		FileReader fileReader = new FileReader(srcFileName);
		bufferedReader = new BufferedReader(fileReader);
	}

	public int nextChar() throws IOException {
		int c = bufferedReader.read();
		if (c == -1)
			bufferedReader.close();
		currentPos.charNum++;
		return c;
	}

	public TextPos getPosition() {
		return currentPos;
	}
	
	public void scanError(String errorMsg) {
		System.out.println(errorMsg); //i przewin? do nowej linii? przez wszystkie inty? what?
	}
}