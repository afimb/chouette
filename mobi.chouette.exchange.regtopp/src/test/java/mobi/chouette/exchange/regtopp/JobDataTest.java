package mobi.chouette.exchange.regtopp;

import lombok.Data;
import mobi.chouette.common.JobData;

@Data
public class JobDataTest implements JobData {

	private Long id;

	private String filename;

	private String action;
	
	private String type;
	
	private String referential;
	
	private String pathName;
}
