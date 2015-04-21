package mobi.chouette.service;

import lombok.Data;
import lombok.experimental.Delegate;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;

@Data
public class JobService implements ServiceConstants{
	

	@Delegate(types = {Job.class}, excludes = {ExcludedJobMethods.class})
	private Job job;

	
	public JobService(Job job)
	{
		this.job = job;
	}
	
	public JobService()
	{
		job = new Job();
	}

	public JobService(String referential, String action, String type) 
	{
		job = new Job();
		setReferential(referential);
		setAction(action);
		setType(type);
	}
	
	public void getPath()
	{
		// todo return job's path or build it if null and id set
	}
	
	public void addLink(String mediaType, String rel)
	{
		if (!linkExists(rel))
		{
			Link link = new Link(mediaType, rel, "",""); // TODO réduire la signature à 2 args
			job.getLinks().add(link);
		}
	}
	

	public boolean linkExists(String rel)
	{
		for (Link link : job.getLinks()) 
		{
			if (link.getRel().equals(rel)) return true;
		}
		return false;
	}
	
	public void removeLink(String rel)
	{
		
	}
	

}
