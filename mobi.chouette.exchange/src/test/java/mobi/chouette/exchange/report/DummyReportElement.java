package mobi.chouette.exchange.report;

import java.io.PrintStream;


public class DummyReportElement extends AbstractReport{
	private int id;
	private String chaine;
	private boolean ok;
	
	
	protected DummyReportElement(int id, String chaine, boolean ok) {
		this.id = id;
		this.chaine = chaine;
		this.ok = ok;
	}


	@Override
	public void print(PrintStream out, int level, boolean first) {
		StringBuilder ret = new StringBuilder();	
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "id", this.id, true));
		out.print(toJsonString(ret, level+1, "chaine", this.chaine, false));
		out.print(toJsonString(ret, level+1, "ok", this.ok, false));
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));	
	}
	
	protected int getId() {
		return this.id;
	}
	
	protected String getChaine() {
		return this.chaine;
	}
	
	protected boolean getOk () {
		return this.ok;
	}
}
