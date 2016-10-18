package mobi.chouette.exchange.report;

import java.io.PrintStream;


/**
 * Dummy class used for AbstractReport method testing
 * @author gjamot
 *
 */
public class DummyReport extends AbstractReport{
	
	protected enum DUMMY_ENUM {
		NO_DATA_FOUND,
		NO_DATA_PROCEEDED,
		NO_DATA_ON_PERIOD
	};
	
	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "chaine", "dummy string", true));
		out.print(toJsonString(ret, level+1, "enum", DUMMY_ENUM.NO_DATA_FOUND, false));
		out.print(toJsonString(ret, level+1, "number", new Integer(1), false));
		out.print(toJsonString(ret, level+1, "boolean", true, false));
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));	
	}
}
