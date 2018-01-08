package tests;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import remembrall.Atom;
import remembrall.ErrorTracker;
import remembrall.Parser;
import remembrall.Scan;
import remembrall.Source;
import remembrall.nodes.AssignNode;
import remembrall.nodes.BoolAndNode;
import remembrall.nodes.ConstrNode;
import remembrall.nodes.EqualsNode;
import remembrall.nodes.FunctionCallNode;
import remembrall.nodes.LessEqualsNode;
import remembrall.nodes.LiteralNode;
import remembrall.nodes.MultiplicationNode;
import remembrall.nodes.Node;
import remembrall.nodes.RepeatNode;
import remembrall.nodes.SelfAdditionNode;
import remembrall.nodes.StartNode;
import remembrall.nodes.VariableNode;

public class ParserSuccess extends TestCase {

	Parser parser;
	
	@Before
	public void setUp() throws Exception {
		String filePath = "../przypominajka/testPrograms/short1";
		Scan scan = null;
		ErrorTracker errTr = new ErrorTracker();
		try {
			scan = new Scan(new Source(filePath), errTr);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.println("Błąd: nie udało się otworzeć pliku źródłowego.");
			return;
		}
		parser = new Parser(scan, errTr);
	}
	
	@Test
	public void testStart() {
		try {
			parser.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		AssignNode n = (AssignNode) parser.root.earlyAssign[0];
		assertEquals(n.var.ident, "alarmTime");
		assertTrue(n.val instanceof ConstrNode);
		ConstrNode nv = (ConstrNode)n.val;
		assertEquals(nv.type, Atom.typeTime);
		assertTrue(nv.args.get(0) instanceof LiteralNode);
		assertEquals(((LiteralNode)nv.args.get(0)).value, new Long (5));
		assertEquals(((LiteralNode)nv.args.get(1)).value, new Long (45));

		assertTrue(parser.root.left instanceof BoolAndNode);
		BoolAndNode and = (BoolAndNode) parser.root.left;
		assertTrue(and.left instanceof EqualsNode);	
		assertTrue(((EqualsNode)and.left).left instanceof FunctionCallNode);	
		FunctionCallNode a = (FunctionCallNode)((EqualsNode)and.left).left;
		assertEquals(a.funcStr, "getCurrentTime");
		assertTrue(((EqualsNode)and.left).right instanceof VariableNode);	
		VariableNode b = (VariableNode)((EqualsNode)and.left).right;
		assertEquals(b.ident, "alarmTime");
		
		assertTrue(and.right instanceof LessEqualsNode);	
		assertTrue(((LessEqualsNode)and.right).left instanceof FunctionCallNode);	
		a = (FunctionCallNode)((LessEqualsNode)and.right).left;
		assertEquals(a.funcStr, "averagePrecipitationDuringDay");
		assertEquals(((LiteralNode)a.args.get(0)).value, "Łódź");
		assertEquals(((LiteralNode)a.args.get(1)).value, "12-115");
		assertEquals(((LiteralNode)a.args.get(2)).value, "Polska");
		assertTrue(((LessEqualsNode)and.right).right instanceof LiteralNode);	
		LiteralNode c = (LiteralNode)((LessEqualsNode)and.right).right;
		assertEquals(c.value, new Double(0.1));
		
		assertTrue(parser.root.right[0] instanceof AssignNode);
		AssignNode d = (AssignNode)parser.root.right[0];
		assertEquals(d.var.ident, "i");
		assertEquals(((LiteralNode)d.val).value, new Long(4));
		assertTrue(parser.root.right[1] instanceof RepeatNode);
		
		RepeatNode r = (RepeatNode)parser.root.right[1];
		assertEquals(((LiteralNode)r.left).value, new Long(3));
		a = (FunctionCallNode)r.right.get(0);
		assertEquals(a.funcStr, "switchOnAlarm");
		assertEquals(((LiteralNode)a.args.get(0)).value, "jigsaw-falling-into-place");
		assertEquals(((VariableNode)a.args.get(1)).ident, "i");
		
		SelfAdditionNode e = (SelfAdditionNode)r.right.get(1);
		assertEquals(e.var.ident, "i");

		a = (FunctionCallNode)r.right.get(2);
		assertEquals(a.funcStr, "sleep");
		assertTrue(a.args.get(0) instanceof MultiplicationNode);
		assertEquals(((LiteralNode)((MultiplicationNode)a.args.get(0)).left).value, 
				new Long(5));
		assertEquals(((LiteralNode)((MultiplicationNode)a.args.get(0)).right).value, 
				new Long(60));
	}



}
