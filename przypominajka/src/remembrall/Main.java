package remembrall;


public class Main {


	public static void main (String [] args) {
		Scan scan = new Scan("/home/kaja/Desktop/tkom-przyklady/f");

		Atom at;
		while ((at = scan.nextAtom()) != Atom.eof) {
			if (at == Atom.identifier)
				System.out.print(scan.getIdentifier() + " -- ");
			if (at == Atom.stringConst)
				System.out.print(scan.getStringConst() + " -- ");
			if (at == Atom.intConst)
				System.out.print(scan.getIntConst() + " -- ");
			if (at == Atom.doubleConst)
				System.out.print(scan.getDoubleConst() + " -- ");
			System.out.println(at);
		}
		scan.endReport();
	}	
}