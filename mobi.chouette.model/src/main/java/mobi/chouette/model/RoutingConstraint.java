package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "routing_constraints")
@Cacheable
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "routingConstraintAreas", "routingConstraintLines" })
public class RoutingConstraint extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 4548672479038099248L;

	@Getter
	@Setter
	@GenericGenerator(name = "routing_constraints_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "routing_constraints_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "routing_constraints_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name")
	private String name;
	
	/**
	 * comment
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "comment")
	private String comment;

	/**
	 * set name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setName(String value) {
		name = StringUtils.abbreviate(value, 255);
	}
	
	/**
	 * set comment <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setComment(String value) {
		comment = StringUtils.abbreviate(value, 255);
	}

	/**
	 * lines concerned by routing constraints <br/>
	 * only for areaType = ITL
	 * 
	 * @param routingConstraintLines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST})
	@JoinTable(name = "routing_constraints_lines", joinColumns = { @JoinColumn(name = "routing_constraint_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "line_id", nullable = false, updatable = false) })
	private List<Line> routingConstraintLines = new ArrayList<Line>(0);

	/**
	 * stops grouped in a routing constraints <br/>
	 * only for areaType = ITL<br/>
	 * stops in this list can't be of ITL type
	 * 
	 * @param routingConstraintAreas
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST})
	@JoinTable(name = "routing_constraints_stop_areas", joinColumns = { @JoinColumn(name = "routing_constraint_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "stop_area_id", nullable = false, updatable = false) })
	private List<StopArea> routingConstraintAreas = new ArrayList<StopArea>(0);

	/**
	 * add a line if not already present
	 * <p>
	 * stop
	 * 
	 * @param area
	 */
	public void addRoutingConstraintStopArea(StopArea area) {
		if (!routingConstraintAreas.contains(area))
			routingConstraintAreas.add(area);
	}

	/**
	 * remove a line
	 * 
	 * @param area
	 */
	public void removeRoutingConstraintArea(StopArea area) {
		if (routingConstraintAreas.contains(area))
			routingConstraintAreas.remove(area);
	}

}
