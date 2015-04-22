package mobi.chouette.service;

import java.nio.file.Paths;
import java.util.Iterator;

import lombok.Data;
import lombok.experimental.Delegate;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;

@Data
public class JobService implements ServiceConstants {

	@Delegate(types = { Job.class }, excludes = { ExcludedJobMethods.class })
	private Job job;

	
	/**
	 * create a jobService on existing job
	 * 
	 * @param job
	 */
	public JobService(Job job) {
		this.job = job;
	}

	/**
	 * create a new jobService
	 * 
	 * @param referential : referential
	 * @param action : action
	 * @param type : type (may be null)
	 */
	public JobService(String referential, String action, String type) {
		job = new Job();
		setReferential(referential);
		setAction(action);
		setType(type);
	}

	/**
	 * return job file path <br/>
	 * build it if not set and job saved
	 * 
	 * @return path or null if job not saved
	 */
	public String getPath() {
		if (job.getPath() == null && job.getId() != null) {
			String path = Paths.get(System.getProperty("user.home"), ROOT_PATH, getReferential(), "data",
					getId().toString()).toString();
			job.setPath(path);
		}
		return job.getPath(); // TODO choisir si on retourne null ou une
								// exception
	}

	/**
	 * add a link if not already present
	 * 
	 * @param mediaType : mime type
	 * @param rel : link key 
	 */
	public void addLink(String mediaType, String rel) {
		if (!linkExists(rel)) {
			Link link = new Link(mediaType, rel, "", ""); // TODO réduire la
															// signature à 2
															// args
			job.getLinks().add(link);
		}
	}

	/**
	 * check link existence
	 * 
	 * @param rel link key
	 * @return
	 */
	public boolean linkExists(String rel) {
		for (Link link : job.getLinks()) {
			if (link.getRel().equals(rel))
				return true;
		}
		return false;
	}

	/**
	 * remove a link if exists <br/>
	 * does nothing if not
	 * 
	 * @param rel link key
	 */
	public void removeLink(String rel) {
		for (Iterator<Link> iterator = job.getLinks().iterator(); iterator.hasNext();) {
			Link link = iterator.next();
			if (link.getRel().equals(rel)) {
				iterator.remove();
				break;
			}
		}
	}

}
