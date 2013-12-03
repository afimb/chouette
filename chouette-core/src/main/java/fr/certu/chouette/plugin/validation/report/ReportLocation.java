package fr.certu.chouette.plugin.validation.report;

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
	@Getter @Setter private String url = null;
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

}
