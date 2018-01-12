package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class Main {

	public static void main (String [] args) {
		String filePath = "../przypominajka/testPrograms/realExample4";//";//
		Scan scan = null;
		ErrorTracker errTr = new ErrorTracker();
		try {
			scan = new Scan(new Source(filePath), errTr);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		Parser parser = new Parser(scan, errTr);
		try {
			parser.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Environment e = new Environment();
		parser.run(e);
	}

}