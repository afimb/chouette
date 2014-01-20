package fr.certu.chouette.plugin.validation.report;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class ReportLocation 
{
	@Getter @Setter private String fileName = null;
	// @Getter @Setter private String errorCode = null;
	@Getter @Setter private Integer lineNumber = -1;
	@Getter @Setter private Integer columnNumber = -1;
	// @Getter @Setter private String attribute = null;
	// @Getter @Setter private String value = null;
	// @Getter @Setter private String xpath = null;
	@Getter @Setter private NeptuneIdentifiedObject object = null;
	// @Getter @Setter private String message = null;

//	public ReportLocation(String fileName, String errorCode, int lineNumber,
//			int columnNumber,
//			String message) 
//	{
//		super();
//		this.fileName = fileName;
//		this.errorCode = errorCode;
//		this.lineNumber = lineNumber;
//		this.columnNumber = columnNumber;
//		this.message = message;
//	}

	public ReportLocation(String fileName) 
	{
		super();
		this.fileName = fileName;
	}

//	public ReportLocation(String fileName, String errorCode, int lineNumber,
//			int columnNumber, String attribute, String value) 
//	{
//		super();
//		this.fileName = fileName;
//		this.errorCode = errorCode;
//		this.lineNumber = lineNumber;
//		this.columnNumber = columnNumber;
//		this.attribute = attribute;
//		this.value = value;
//	}

	public ReportLocation(String fileName, int lineNumber,int columnNumber) 
	{
		super();
		this.fileName = fileName;
		this.lineNumber = Integer.valueOf(lineNumber);
		this.columnNumber = Integer.valueOf(columnNumber);
	}

//	public ReportLocation(String fileName, int lineNumber,
//			int columnNumber, String attribute, String value) 
//	{
//		super();
//		this.fileName = fileName;
//		this.lineNumber = lineNumber;
//		this.columnNumber = columnNumber;
//		this.attribute = attribute;
//		this.value = value;
//	}

	public ReportLocation(NeptuneIdentifiedObject chouetteObject) 
	{
		super();
		this.object = chouetteObject;
	}

	public JSONObject toJSON() 
	{
		JSONObject json = new JSONObject();
		
//		StringBuilder builder = new StringBuilder();
//		builder.append("{");
		if (fileName != null)
			json.put("filename", fileName);
//			builder.append("\"fileName\":\""+fileName+"\",");
//		if (errorCode != null)
//			builder.append("\"errorCode\":\""+errorCode+"\",");
		if (lineNumber != -1)
			json.put("lineNumber", lineNumber);
//			builder.append("\"lineNumber\":\""+lineNumber+"\",");
		if (columnNumber != -1)
			json.put("columnNumber", columnNumber);
//			builder.append("\"columnNumber\":\""+columnNumber+"\",");
//		if (attribute != null)
//			builder.append("\"attribute\":\""+attribute+"\",");
//		if (value != null)
//			builder.append("\"value\":\""+value+"\",");
//		if (xpath != null)
//			builder.append("\"xpath\":\""+xpath+"\",");
		if (object != null)
			json.put("url", getObject().toURL());
// 			builder.append("\"url\":\""+getObject().toURL()+"\",");
//		if (message != null)
//			builder.append("\"message\":\""+message+"\",");
		return json;
	}

}
