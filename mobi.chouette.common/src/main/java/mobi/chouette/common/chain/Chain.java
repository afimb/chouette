package mobi.chouette.common.chain;



public interface Chain extends Command{

	void add(Command command);
	
	void clear();

}
