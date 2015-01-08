package mobi.chouette.common.chain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import mobi.chouette.common.Context;

@AllArgsConstructor
public class ChainImpl implements Chain {

	private List<Command> commands = new ArrayList<Command>();
	private boolean ignored = false;
	
	@Override
	public void add(Command command) {
		commands.add(command);
	}

	@Override
	public boolean execute(Context context) throws Exception {

		if (context == null) {
			throw new IllegalArgumentException();
		}

		boolean result = SUCCESS;
		for (Command command : commands) {
			try {
				result = command.execute(context);
				if (result == ERROR && ! ignored) {
					break;
				}
			} catch (Exception e) {
				if(! ignored){
					result = ERROR;
					throw e;
				}
			}
		}
		return result;
	}

}
