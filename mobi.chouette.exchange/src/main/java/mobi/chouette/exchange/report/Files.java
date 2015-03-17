package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@Data
public class Files {

	@XmlElement(name = "file_info")
	private List<FileInfo> fileInfos = new ArrayList<>();
	
	public FileInfo findFileFileInfo(String name)
	{
		for (FileInfo fileInfo : fileInfos) 
		{
			if (fileInfo.getName().equals(name)) return fileInfo;
		}
		return null;
	}
	

}
