package mobi.chouette.exchange.validator;

import lombok.Data;
import mobi.chouette.common.JobData;

@Data
public class JobDataTest implements JobData {

	private Long id;

	private String inputFilename;

	private String outputFilename;

	private String action;
	
	private String type;
	
	private String referential;
	
	private String pathName;
}
