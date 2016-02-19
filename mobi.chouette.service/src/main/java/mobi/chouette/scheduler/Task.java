package mobi.chouette.scheduler;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Job.STATUS;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;

@Log4j
@ToString(of = { "job" })
public class Task implements Callable<Job.STATUS>, ManagedTask, Constant {

	@Getter
	private JobService job;

	private Map<String, String> properties;

	private ManagedTaskListener listener;
	
	private Context context = new Context();

	public Task(JobService job, Map<String, String> properties, ManagedTaskListener listener) 
	{
		this.job = job;
		this.properties = properties;
		this.listener = listener;
		context.put(JOB_ID, job.getId());
		context.put(JOB_DATA, job);
		
	}

	@Override
	public STATUS call() throws Exception {

		STATUS result = STATUS.TERMINATED;
		if (context.containsKey(CANCEL_ASKED)) return STATUS.CANCELED;
		ContextHolder.setContext(job.getReferential());
		try {
			InitialContext initialContext = new InitialContext();
			context.put(INITIAL_CONTEXT, initialContext);
			// Thread.sleep(100);
			Command command = CommandFactory.create(initialContext,
					MainCommand.class.getName());
			command.execute(context);
			//result = job.getStatus();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result = STATUS.ABORTED;
		} finally {
			context.clear();
			ContextHolder.setContext(null);
		}
		return result;
	}
	
	public void cancel()
	{
		context.put(CANCEL_ASKED, Boolean.TRUE);
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
