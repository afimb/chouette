package mobi.chouette.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.MediaType;

import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;

import org.apache.commons.lang.StringUtils;



public class JobServiceManager implements ServiceConstants {
	
	@EJB
	JobDAO jobDAO;

	@EJB
	SchemaDAO schemaDAO;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobService upload(String referential,String action, String type, Map<String,InputStream> parts) throws Exception
	{
		if (type != null && type.startsWith("/")) {
			type = type.substring(1);
		}
	
	if (!schemaDAO.getSchemaListing().contains(referential))
	{
		throw new Exception(UNKNOWN_REFERENTIAL);
	}
		if (!commandExists(action, type))
		{
			throw new Exception(UNKNOWN_ACTION);
		}
		
		
		JobService jobService = null;
		
		try
		{
			jobService = new JobService(referential,action,type);
			jobDAO.create(jobService.getJob());
		
		for (Entry<String, InputStream> entry : parts.entrySet()) {
			String name = entry.getKey();
			InputStream stream = entry.getValue();
			addPart(jobService,name,stream);
		}
		
		}
		catch(Exception ex)
		{
			if (jobService != null && jobService.getId() != null)
			{
				jobDAO.delete(jobService.getJob());
			}
			// remove path if exists
			
			throw ex;
		}
		
		return jobService;
		
		
	}
	
	private void addPart(JobService jobService, String name, InputStream stream) throws Exception
	{
		if (name.equals(PARAMETERS_FILE))
		{
			addParameterPart(jobService,stream);
		}
		else
		{
			addDataPart(jobService,stream);
		}
	}
	
	
	
	private void addDataPart(JobService jobService, InputStream stream) throws Exception {
		if (jobService.linkExists(DATA_REL))
		{
			throw new Exception(DUPPLICATE_DATA);
			
		}
		
		// save file
		
		
		// add link
		jobService.addLink(MediaType.APPLICATION_OCTET_STREAM,DATA_REL);
		
	}

	private void addParameterPart(JobService jobService, InputStream stream) throws Exception {
		if (jobService.linkExists(PARAMETERS_REL))
		{
			throw new Exception(DUPPLICATE_PARAMETERS);
		}
		// save file
		
		
		// add link
		jobService.addLink(MediaType.APPLICATION_JSON,PARAMETERS_REL);
		
	}

	
	public void download()
	{
		
	}
	
	public void jobs()
	{
		
	}
	
	public void scheduledJob()
	{
		
	}
	
	public void cancel()
	{
		
	}
	
	public void terminatedJob()
	{
		
	}
	
	public void remove()
	{
		
	}
	
	public void drop()
	{
		
	}
	
	public boolean commandExists(String action, String type)
	{
		try {
			Class.forName(getCommandName(action, type));
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * find next waiting job on referential <br/>
	 * return null if a job is STARTED or if no job is SCHEDULED
	 * 
	 * @param referential
	 * @return
	 */
	public JobService getNextJob(String referential)
	{
		Job job = jobDAO.getNextJob(referential);
		if (job == null) return null;
		return new JobService(job);
	}
	
	
	public void start(JobService jobService)
	{
		
	}
	
	public void terminate(JobService jobService)
	{
		
	}

	public void abort(JobService jobService)
	{
		
	}

	
	public static String getCommandName(String action, String type)
	{
		type = type == null ? "" : type;

		return "mobi.chouette.exchange."
				+ (type.isEmpty() ? "" : type + ".") + action + "."
				+ StringUtils.capitalize(type)
				+ StringUtils.capitalize(action) + "Command";
	}

	
}
