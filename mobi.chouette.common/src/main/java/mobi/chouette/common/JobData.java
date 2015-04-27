package mobi.chouette.common;

public interface JobData {
	
	Long getId();
	String getFilename();
	void setFilename(String filename); 
	String getReferential();
	String getAction();
	String getType();
	String getPath();

}
