package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@XmlRootElement(name = "validityCategories")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "numDaysAtLeastValid", "lineNumbers" })
@Getter
public class ValidityCategory {
	private String name;
	private Integer numDaysAtLeastValid;
	private List<String> lineNumbers = new ArrayList<String>();
}
