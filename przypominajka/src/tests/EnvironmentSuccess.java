package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import remembrall.Environment;
import remembrall.ErrorTracker;
import remembrall.TypedValue;
import remembrall.Parser;
import remembrall.Scan;
import remembrall.Source;

public class EnvironmentSuccess {

	Environment e;
	
	@Before
	public void setUp() throws Exception {

	}


	@Test
	public void testResolve() {
		String filePath = "../przypominajka/testPrograms/short2";//realExample3
		Scan scan = null;
		ErrorTracker errTr = new ErrorTracker();
		try {
			scan = new Scan(new Source(filePath), errTr);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		Parser parser = new Parser(scan, errTr);
		e = new Environment();
	//	parser.run(e);
		
		
	//	assertTrue(true);
	//	IdentValue a = e.resolve("i");
	//	assertEquals(a.v, new Long (3));
	}

}
