package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Report {

	/**
	 * indicate progression informations, will disapear when terminated
	 */
	@XmlElement(name = "progression")
	private Progression progression = new Progression();

	@XmlElement(name = "result")
	private String result;
	
	@XmlElement(name = "zip_file")
	private ZipInfo zip;
	
	@XmlElement(name = "files")
	private List<FileInfo> files = new ArrayList<>();
	
	@XmlElement(name = "lines")
	private List<LineInfo> lines = new ArrayList<>();
	
	@XmlElement(name = "stats")
	private LineStats stats;

	@XmlElement(name = "failure")
	private String failure;
	
	public FileInfo findFileFileInfo(String name)
	{
		for (FileInfo fileInfo : files) 
		{
			if (fileInfo.getName().equals(name)) return fileInfo;
		}
		return null;
	}
	
}
