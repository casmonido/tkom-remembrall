package remembrall;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Main {


	public static void main (String [] args) {
		String filePath = "/home/kaja/Desktop/tkom-przyklady/e";
		Scan scan;
		try {
			scan = new Scan(new Source(filePath));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		

		Atom at;
		
		while ((at = scan.nextAtom()) != Atom.eof) {
			
			if (at == Atom.identifier)
				System.out.print(" -- " + scan.getIdentifier()+" -- "  );
			if (at == Atom.stringConst)
				System.out.print(" -- " + scan.getStringConst()+" -- " );
			if (at == Atom.intConst)
				System.out.print(" -- " + scan.getIntConst()+" -- " );
			if (at == Atom.doubleConst)
				System.out.print(" -- " + scan.getDoubleConst() + " -- " );
			
			System.out.println(at);

		}
		scan.printEndReport();
	}	
}