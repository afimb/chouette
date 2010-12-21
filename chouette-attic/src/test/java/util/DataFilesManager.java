package util;

import java.io.File;
import java.util.Hashtable;
import fr.certu.chouette.manager.SingletonManager;

public class DataFilesManager 
{

	private String INPUT_FOLDER;
	private String OUTPUT_FOLDER;
	private int filesCounter = 0;
	
	private final static Hashtable<String, String> INPUT_FILES = new Hashtable<String, String>();
	static
	{
		INPUT_FILES.put("goodAFNORFile", "goodAFNORLineFile.xml");
		INPUT_FILES.put("badEnumValueAFNORFile", "badAFNORFile0.xml");
	}
	
	private final static Hashtable<String, String> OUTPUT_FILES = new Hashtable<String, String>();
	static
	{
		OUTPUT_FILES.put("beforeAFNORImport", "beforeAFNORImport.xml");
		OUTPUT_FILES.put("afterAFNORImport", "afterAFNORImport.xml");
	}
	
	/**
	 * @throws Exception
	 */
	public DataFilesManager() throws Exception
	{
		OUTPUT_FOLDER = SingletonManager.getSpringProperty("dir.temp") + "/";
		INPUT_FOLDER = SingletonManager.getSpringProperty("test.inputData.dir") + "/";
	}
	
	/**
	 * @param filename, the key of the input or output data file test 
	 * @return the full path of data file
	 */
	public String getInputFileName(String filename)
	{
		return INPUT_FOLDER + INPUT_FILES.get(filename);
	}
	
	/**
	 * @param filename, the key of the input or output data file test 
	 * @return the full path of data file
	 */
	private String getOutputFileName(String filename)
	{
		String fullname = OUTPUT_FOLDER + OUTPUT_FILES.get(filename) + filesCounter; 
		filesCounter += 1;
		return fullname;		
	}
	
	
	public File getOutputFile(String filename) throws Exception
	{
		
		String fullName = getOutputFileName(filename);
		File file = new File(fullName);
		file.deleteOnExit();
		if (null == file || "null" == file.getName())
		{
			throw new Exception("Invalid path file : " + fullName);
		}
		return file;
	}
}
