package fr.certu.chouette.echange.comparator;

public class DataState 
{
        
    /**
     * the name of the data
     */
    private String name;
    
    /**
     * the state of the comparison
     */
    private boolean identical;
    
	/**
	 * @param name
	 * @param identical
	 */
	public DataState(String name, boolean identical) 
	{
		this.name = name;
		this.identical = identical;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIdentical() {
		return identical;
	}
	public void setIdentical(boolean identical) {
		this.identical = identical;
	}
    
    
}
