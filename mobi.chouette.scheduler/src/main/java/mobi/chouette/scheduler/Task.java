package mobi.chouette.scheduler;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Log4j
@AllArgsConstructor
@ToString(of = { "job" })
public class Task implements Callable<Job.STATUS>, ManagedTask, Constant {

	@Getter
	private Job job;

	private Map<String, String> properties;

	private ManagedTaskListener listener;

	@Override
	public STATUS call() throws Exception {

		STATUS result = STATUS.TERMINATED;
		ContextHolder.setContext(job.getReferential());
		try {
			InitialContext initialContext = new InitialContext();
			Context context = new Context();
			context.put(INITIAL_CONTEXT, initialContext);
			context.put(JOB_ID, job.getId());
			Thread.sleep(1000);
			Command command = CommandFactory.create(initialContext,
					MainCommand.class.getName());
			command.execute(context);

			result = job.getStatus();
		} catch (Exception e) {
			log.error(e);
			result = STATUS.ABORTED;
		} finally {
			ContextHolder.setContext(null);
		}
		return result;
	}

	@Override
	public Map<String, String> getExecutionProperties() {
		return properties;
	}

	@Override
	public ManagedTaskListener getManagedTaskListener() {
		return listener;
	}

}
