package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import remembrall.Source;

public class SourceSuccess {
	
	Source src;

	@Before
	public void setUp() throws Exception {
		src = new Source("../przypominajka/testPrograms/sourceTestLineCounting");
	}
	
//	@After
//	public void tearDown() throws Exception {}

	@Test
	public void testGetPosition() {
		try {
			int c = src.nextChar();
			while (c != -1) {
				c = src.nextChar();
			}
		} catch (IOException e) {
			fail("IOException" + e.getMessage());
		}
	}

}
