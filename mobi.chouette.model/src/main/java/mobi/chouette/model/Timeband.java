package mobi.chouette.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Models the time band for journies in timesheet category.
 * 
 * @author zbouziane
 * @since 3.2.0
 * 
 */
@Entity
@Table(name = "timebands")
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "journeyFrequencies" })
public class Timeband extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 3366941607519552650L;

	@Getter
	@Setter
	@GenericGenerator(name = "timebands_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "timebands_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "timebands_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * Timeband name
	 * 
	 * @return The name of this time band
	 */
	@Getter
	@Column(name = "name")
	private String name;

	/**
	 * set name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param name
	 *            The new name of this time band
	 */
	public void setName(String name) {
		name = StringUtils.abbreviate(name, 255);
	}

	/**
	 * start time
	 * 
	 * @param startTime
	 *            The new start time of this time band
	 * @return The start time of this time band
	 */
	@Getter
	@Setter
	@Column(name = "start_time", nullable = false)
	private Time startTime;

	/**
	 * end time
	 * 
	 * @param endTime
	 *            The new end time of this time band
	 * @return The end time of this time band
	 */
	@Getter
	@Setter
	@Column(name = "end_time", nullable = false)
	private Time endTime;

	@Getter
	@Setter
	@OneToMany(mappedBy = "timeband", cascade = { CascadeType.PERSIST })
	// @JoinColumn(name = "timeband_id", updatable = false)
	private List<JourneyFrequency> journeyFrequencies = new ArrayList<JourneyFrequency>(0);
	// @Getter
	// @Setter
	// @OneToMany(mappedBy = "route", cascade = { CascadeType.PERSIST })
	// private List<JourneyPattern> journeyPatterns = new
	// ArrayList<JourneyPattern>(0);
}
