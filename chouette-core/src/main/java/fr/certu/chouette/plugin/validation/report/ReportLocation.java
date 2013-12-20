package fr.certu.chouette.plugin.validation.report;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import lombok.Getter;
import lombok.Setter;

public class ReportLocation 
{
	@Getter @Setter private String fileName = null;
	@Getter @Setter private String errorCode = null;
	@Getter @Setter private int lineNumber = -1;
	@Getter @Setter private int columnNumber = -1;
	@Getter @Setter private String attribute = null;
	@Getter @Setter private String value = null;
	@Getter @Setter private String xpath = null;
	@Getter @Setter private NeptuneIdentifiedObject object = null;
	@Getter @Setter private String message = null;

	public ReportLocation(String fileName, String errorCode, int lineNumber,
			int columnNumber,
			String message) 
	{
		super();
		this.fileName = fileName;
		this.errorCode = errorCode;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.message = message;
	}

	public ReportLocation(String fileName) 
	{
		super();
		this.fileName = fileName;
	}

	public ReportLocation(String fileName, String errorCode, int lineNumber,
			int columnNumber, String attribute, String value) 
	{
		super();
		this.fileName = fileName;
		this.errorCode = errorCode;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.attribute = attribute;
		this.value = value;
	}

	public ReportLocation(String fileName, int lineNumber,int columnNumber) 
	{
		super();
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	public ReportLocation(String fileName, int lineNumber,
			int columnNumber, String attribute, String value) 
	{
		super();
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.attribute = attribute;
		this.value = value;
	}

	public ReportLocation(NeptuneIdentifiedObject chouetteObject) 
	{
		super();
		this.object = chouetteObject;
	}

	public Object toJSON() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		if (fileName != null)
			builder.append("\"fileName\":\""+fileName+"\",");
		if (errorCode != null)
			builder.append("\"errorCode\":\""+errorCode+"\",");
		if (lineNumber != -1)
			builder.append("\"lineNumber\":\""+lineNumber+"\",");
		if (columnNumber != -1)
			builder.append("\"columnNumber\":\""+columnNumber+"\",");
		if (attribute != null)
			builder.append("\"attribute\":\""+attribute+"\",");
		if (value != null)
			builder.append("\"value\":\""+value+"\",");
		if (xpath != null)
			builder.append("\"xpath\":\""+xpath+"\",");
		// TODO : build Url for objects 
//		if (object != null)
//			builder.append("\"url\":\""+url+"\",");
		if (message != null)
			builder.append("\"message\":\""+message+"\",");
		return builder.subSequence(0, builder.length()-1)+"}";
	}

}
