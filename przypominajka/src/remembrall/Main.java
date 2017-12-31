package remembrall;

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
		Parser parser = new Parser(filePath);
		Environment env = new Environment();
		try {
			parser.assignExp(env);
			IdentValue t = env.resolve("i");
			if (t.v != null)
				System.out.println(t.v);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> identTable = new HashMap<String, Object>(5);
		identTable.put("s", new A());
		System.out.println(((A)identTable.get("s")).a + " " + ((A)identTable.get("s")).s );
		((A)identTable.get("s")).s = 6;
		System.out.println(((A)identTable.get("s")).a + " " + ((A)identTable.get("s")).s );
		
	}

}