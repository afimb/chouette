package mobi.chouette.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Chouette PTLink : a link between 2 successive StopPoints in a route <br/>
 * Note: this object is only used for Neptune import, export and validation
 * purpose
 * <p/>
 * <p/>
 * Neptune mapping : PtLink <br/>
 * Gtfs mapping : none <br/>
 */
@Entity
@Table(name = "pt_links")
@NoArgsConstructor
@ToString(exclude = { "route" })
public class PTLink extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -3089442100133439163L;

	/**
	 * name
	 * 
	 * @param name
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "name")
	private String name;

	/**
	 * comment
	 * 
	 * @param comment
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "comment")
	private String comment;

	/**
	 * link length in meters
	 * 
	 * @param linkDistance
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "link_distance")
	private BigDecimal linkDistance;

	/**
	 * start of link
	 * 
	 * @param startOfLink
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "start_of_link_id")
	private StopPoint startOfLink;

	/**
	 * end of link
	 * 
	 * @param endOfLink
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "end_of_link_id")
	private StopPoint endOfLink;

	/**
	 * route
	 * 
	 * @param route
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

}
