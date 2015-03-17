package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "action_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@NoArgsConstructor
public class ActionReport {

	/**
	 * indicate progression informations, will disappear when terminated
	 */
	@XmlElement(name = "progression")
	@Getter @Setter
	private Progression progression;

	@XmlElement(name = "result")
	@Getter @Setter
	private String result;
	
	@XmlElement(name = "zip_file")
	@Getter @Setter
	private ZipInfo zip;
	
	@XmlElement(name = "files")
	@Getter @Setter
	private List<FileInfo> files = new ArrayList<>();
	
	@XmlElement(name = "lines")
	@Getter @Setter
	private List<LineInfo> lines = new ArrayList<>();
	
	@XmlElement(name = "stats")
	@Getter @Setter
	private LineStats stats;

	@XmlElement(name = "failure")
	@Getter @Setter
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
