package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Chouette DeadRun
 */

@Entity
@Table(name = "dead_runs")
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "journeyPattern", "timetables" })
@Log4j
public class DeadRun extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 304336286208135064L;

	@Getter
	@Setter
	@GenericGenerator(name = "dead_runs_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "dead_runs_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "dead_runs_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;


	/**
	 * journey pattern reference
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_pattern_id")
	private JourneyPattern journeyPattern;

	/**
	 * set journey pattern reference
	 * 
	 * @param journeyPattern
	 */
	public void setJourneyPattern(JourneyPattern journeyPattern) {
		if (this.journeyPattern != null) {
			this.journeyPattern.getDeadRuns().remove(this);
		}
		this.journeyPattern = journeyPattern;
		if (journeyPattern != null) {
			journeyPattern.getDeadRuns().add(this);
		}
	}

	/**
	 * Blocks referencing this DeadRun .
	 */

	@Getter
	@ManyToMany(mappedBy = "deadRuns")
	private List<Block> blocks = new ArrayList<>();


	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(name = "time_tables_dead_runs", joinColumns = { @JoinColumn(name = "dead_run_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "time_table_id", nullable = false, updatable = false) })
	private List<Timetable> timetables = new ArrayList<Timetable>(0);

	/**
	 * vehicle journey at stops : passing times
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "dead_run_id", updatable = false)
	private List<DeadRunAtStop> deadRunAtStops = new ArrayList<DeadRunAtStop>(0);

}
