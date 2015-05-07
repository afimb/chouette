package mobi.chouette.command;

import java.util.Locale;


public class Main {
	
	public static void main( String[] args ) {
		Locale.setDefault(Locale.ENGLISH);
		CommandManager manager = new CommandManager(args);
		manager.parseArgs();
		try {
			manager.process();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		try {
			manager.saveReports();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
