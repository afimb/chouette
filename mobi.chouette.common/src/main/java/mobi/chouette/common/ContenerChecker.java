package mobi.chouette.common;

public interface ContenerChecker {
	public static final String NAME = "ContenerChecker";
	
	 boolean validateContener(String contenerName);
	 
	 String getContext();
}
