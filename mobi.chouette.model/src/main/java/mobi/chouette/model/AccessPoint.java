package mobi.chouette.model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.AccessPointTypeEnum;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette AccessPoint : relation between an AccessPoint and a StopArea
 * <p/>
 * Neptune mapping : PTAccessPoint <br/>
 * Gtfs mapping : none <br/>
 * 
 */

@Entity
@Table(name = "access_points")
@NoArgsConstructor
@ToString(exclude = { "containedIn", "accessLinks" })
public class AccessPoint extends NeptuneLocalizedObject {

	private static final long serialVersionUID = 7520070228185917225L;

	@Getter
	@Setter
//	@SequenceGenerator(name="access_points_id_seq", sequenceName="access_points_id_seq", allocationSize=1)
//    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="access_points_id_seq")
	@GenericGenerator(name = "access_points_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "access_points_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "access_points_id_seq")
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
	 * comment
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "comment")
	private String comment;

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
	 * access point opening time
	 * 
	 * @param openingTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "openning_time")
	private Time openingTime;

	/**
	 * access point closing time
	 * 
	 * @param closingTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "closing_time")
	private Time closingTime;

	/**
	 * access type
	 * 
	 * @param type
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "access_type")
	private AccessPointTypeEnum type;

	/**
	 * lift indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a lift is available on this access point</li>
	 * <li>false if no lift is available on this access point</li>
	 * </ul>
	 * 
	 * @param liftAvailable
	 *            New state for lift indicator
	 * @return The actual lift indicator
	 */
	@Getter
	@Setter
	@Column(name = "lift_availability")
	private Boolean liftAvailable = false;

	/**
	 * mobility restriction indicator (such as wheel chairs) <br/>
	 * 
	 * <ul>
	 * <li>true if wheel chairs can follow this access point</li>
	 * <li>false if wheel chairs can't follow this access point</li>
	 * </ul>
	 * 
	 * @param mobilityRestrictedSuitable
	 *            New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private Boolean mobilityRestrictedSuitable = false;

	/**
	 * stairs indicator <br/>
	 * 
	 * <ul>
	 * <li>true if a stairs are presents on this access point</li>
	 * <li>false if no stairs are presents on this access point</li>
	 * </ul>
	 * 
	 * @param stairsAvailable
	 *            New state for stairs indicator
	 * @return The actual stairs indicator
	 */
	@Getter
	@Setter
	@Column(name = "stairs_availability")
	private Boolean stairsAvailable = false;

	/**
	 * access point owner <br/>
	 * should be a logical stop area such as commercial stop point or stop place <br/>
	 * access links from or to this access should reach only this stop area or
	 * it's children
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stop_area_id")
	private StopArea containedIn;

	/**
	 * set access point owner
	 * 
	 * @param containedIn New value
	 */
	public void setContainedIn(StopArea containedIn) {
		if (this.containedIn != null) {
			this.containedIn.getAccessPoints().remove(this);
		}
		this.containedIn = containedIn;
		if (containedIn != null) {
			containedIn.getAccessPoints().add(this);
		}
	}

	/**
	 * access links reaching this access point <br/>
	 * 
	 * @param accessLinks
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "accessPoint",cascade = { CascadeType.PERSIST })
	private List<AccessLink> accessLinks = new ArrayList<AccessLink>(0);

}
