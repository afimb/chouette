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
@XmlType(propOrder = { "startDate", "days", "validityCategories", "publicLines" })
@Getter
public class LineStatistics {

	// Use sql date for reliable serialization of date only (Should have been java.time/joda LocalDate)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private java.sql.Date startDate;
	private int days;
	private List<ValidityCategory> validityCategories = new ArrayList<>();
	private List<PublicLine> publicLines = new ArrayList<>();

	public LineStatistics(Date startDate, int days, List<PublicLine> publicLines) {
		this.startDate = startDate == null ? null : new java.sql.Date(startDate.getTime());
		this.days = days;
		this.publicLines = publicLines;
	}
}