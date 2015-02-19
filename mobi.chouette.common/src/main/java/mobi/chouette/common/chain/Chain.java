package mobi.chouette.common.chain;

import javax.ejb.Local;

@Local
public interface Chain extends Command {

	void add(Command command);
	
	void clear();

}
