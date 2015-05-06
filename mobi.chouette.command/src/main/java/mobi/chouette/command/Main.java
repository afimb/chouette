package mobi.chouette.command;


public class Main {
	
	public static void main( String[] args ) {
		CommandManager manager = new CommandManager(args);
		manager.parseArgs();
		manager.process();
	}

}
