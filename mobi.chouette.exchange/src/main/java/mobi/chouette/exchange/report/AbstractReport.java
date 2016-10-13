package mobi.chouette.exchange.report;

import java.io.PrintStream;
import java.util.Date;

public abstract class AbstractReport implements Report {
	public static final int maxErrors = 15;

	public final void print(PrintStream out, StringBuilder ret, int level, boolean first){
		throw new UnsupportedOperationException("Use JAXB serialization instead");
	}

	public final void print(PrintStream stream){
		throw new UnsupportedOperationException("Use JAXB serialization instead");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDate(Date date){
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(){
		throw new UnsupportedOperationException();
	}

}
