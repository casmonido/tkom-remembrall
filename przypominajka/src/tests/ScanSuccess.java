package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import remembrall.Scan;
import remembrall.Source;

public class ScanSuccess {

	Scan scan;
	
	@Before
	public void setUp() throws Exception {
		scan = new Scan(
				new Source("../przypominajka/testPrograms/sourceTestLineCounting"));	
	}

	@Test
	public void testNextToken() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintEndReport() {
		fail("Not yet implemented");
	}

}
