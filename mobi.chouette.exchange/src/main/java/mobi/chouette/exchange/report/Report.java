package mobi.chouette.exchange.report;

import java.io.PrintStream;
import java.util.Date;

public interface Report {

	boolean isEmpty();

	void print(PrintStream stream);
	
	void setDate(Date date);
	Date getDate();
	
}
