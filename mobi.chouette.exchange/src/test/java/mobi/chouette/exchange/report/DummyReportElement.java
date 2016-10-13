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
