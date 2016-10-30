package mobi.chouette.model.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.util.DateAdapter;

@NoArgsConstructor
@XmlRootElement(name = "lineStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "startDate", "validityCategories", "publicLines" })
@Getter
@Setter
public class LineStatistics {

	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date startDate;
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date endDate;
	private List<ValidityCategory> validityCategories = new ArrayList<>();
	private List<PublicLine> publicLines = new ArrayList<>();

}