package mobi.chouette.common;

public interface JobData {
	
	Long getId();
	String getInputFilename();
	void setInputFilename(String filename); 
	String getOutputFilename();
	void setOutputFilename(String filename); 
	String getReferential();
	String getAction();
	String getType();
	String getPathName();

}
