package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "action_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"progression","result","zip","files","lines","stats","failure"})
@NoArgsConstructor
@Data
public class ActionReport {

	@XmlElement(name = "progression", required = true)
	private Progression progression = new Progression();

	@XmlElement(name = "result", required = true)
	private String result = ReportConstant.STATUS_OK;

	@XmlElement(name = "zip_file")
	private FileInfo zip;

	@XmlElement(name = "files")
	private List<FileInfo> files = new ArrayList<>();

	@XmlElement(name = "lines")
	private List<LineInfo> lines = new ArrayList<>();

	@XmlElement(name = "stats", required = true)
	private LineStats stats = new LineStats();

	@XmlElement(name = "failure")
	private ActionError failure;

	/**
	 * set or unset error ; will set result to ERROR if error != null 
	 * @param error
	 */
	public void setFailure(ActionError error)
	{
		if (error == null)
		{
			result = ReportConstant.STATUS_OK;
			failure = null;
		}
		else
		{
			result = ReportConstant.STATUS_ERROR;
			failure = error;
		}
	}

	public FileInfo findFileInfo(String name)
	{
		for (FileInfo fileInfo : files) 
		{
			if (fileInfo.getName().equals(name)) return fileInfo;
		}
		return null;
	}

}
