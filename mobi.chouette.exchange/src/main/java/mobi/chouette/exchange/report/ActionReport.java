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

	/**
	 * indicate progression informations, will disappear when terminated
	 */
	@XmlElement(name = "progression")
	private Progression progression;

	@XmlElement(name = "result")
	private String result;
	
	@XmlElement(name = "zip_file")
	private FileInfo zip;
	
	@XmlElement(name = "files")
	private List<FileInfo> files = new ArrayList<>();
	
	@XmlElement(name = "lines")
	private List<LineInfo> lines = new ArrayList<>();
	
	@XmlElement(name = "stats")
	private LineStats stats;

	@XmlElement(name = "failure")
	private String failure;
		
	public FileInfo findFileInfo(String name)
	{
		for (FileInfo fileInfo : files) 
		{
			if (fileInfo.getName().equals(name)) return fileInfo;
		}
		return null;
	}
	
}
