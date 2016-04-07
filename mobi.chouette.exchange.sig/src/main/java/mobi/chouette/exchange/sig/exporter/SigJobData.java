package mobi.chouette.exchange.sig.exporter;

import lombok.Data;
import lombok.NoArgsConstructor;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.parameters.AbstractParameter;

@Data
@NoArgsConstructor
public class SigJobData implements JobData {

	private Long id;
	private String inputFilename;
	private String outputFilename;
	private String referential;
	private String action;
	private String type;
	private String pathName;
	private AbstractParameter configuration;
	

}
