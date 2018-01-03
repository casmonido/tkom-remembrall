package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class A {
	int a = 5;
	Integer s;
	public A() {}
}

public class Main {

	public static void main (String [] args) {
		String filePath = "/home/kaja/Desktop/tkom-przyklady/f";
		Scan scan = null;
		try {
			scan = new Scan(new Source(filePath));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		Parser parser = new Parser(scan);
		Environment env = new Environment();
		try {
			parser.start();
			IdentValue t = env.resolve("i");
			if (t.v != null)
				System.out.println(t.v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}