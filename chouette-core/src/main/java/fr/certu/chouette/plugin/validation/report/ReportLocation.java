package fr.certu.chouette.plugin.validation.report;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class ReportLocation 
{
	@Getter private String fileName = null;
	@Getter @Setter private Integer lineNumber = -1;
	@Getter @Setter private Integer columnNumber = -1;
	@Getter @Setter private NeptuneIdentifiedObject object = null;
	
	public void setFileName(String name)
	{
		File file = new File(name);
		fileName = file.getName();
		
	}

	public ReportLocation(String fileName) 
	{
		super();
		setFileName(fileName);
	}


	public ReportLocation(String fileName, int lineNumber,int columnNumber) 
	{
		super();
		setFileName(fileName);
		this.lineNumber = Integer.valueOf(lineNumber);
		this.columnNumber = Integer.valueOf(columnNumber);
	}


	public ReportLocation(NeptuneIdentifiedObject chouetteObject) 
	{
		super();
		this.object = chouetteObject;
	}

	public JSONObject toJSON() 
	{
		JSONObject json = new JSONObject();
		
		if (fileName != null)
			json.put("filename", fileName);
		if (lineNumber != -1)
			json.put("lineNumber", lineNumber);
		if (columnNumber != -1)
			json.put("columnNumber", columnNumber);
		if (object != null)
			json.put("url", getObject().toURL());
		return json;
	}
	
	public String toString()
	{
		String message = "";
		if (object != null)
		{
			return "objet = "+getObject().toURL();
		}
		if (fileName != null)
			message = fileName;
		if (lineNumber != -1)
			message += " : l="+lineNumber;
		if (columnNumber != -1)
			message += " ,c="+columnNumber;
		return message;
		
	}

}
