package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chouette DestinationDisplay : direction and destination info displayed for
 * each stop at a vehicle
 *
 * @since 3.4.2
 */

@Entity
@Table(name = "destination_displays")
@NoArgsConstructor
public class DestinationDisplay extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 6790138295242844540L;

	@Getter
	@Setter
	@GenericGenerator(name = "destination_displays_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "destination_displays_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "destination_displays_id_seq")
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
	 * side text
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "side_text")
	private String sideText;

	/**
	 * set side text <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setSideText(String value) {
		sideText = StringUtils.abbreviate(value, 255);
	}

	/**
	 * front text
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "front_text")
	private String frontText;

	/**
	 * set front text <br/>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setFrontText(String value) {
		frontText = StringUtils.abbreviate(value, 255);
	}

	public String getFrontTextWithComputedVias() {
		if (vias.size() > 0 && frontText != null) {
			StringBuilder b = new StringBuilder();
			b.append(frontText);
			b.append(" via ");

			List<String> viaFrontTexts = new ArrayList<>();
			for(DestinationDisplay via : vias) {
				if(via.getFrontText() != null) {
					viaFrontTexts.add(via.getFrontText());
				}
			}

			b.append(StringUtils.join(viaFrontTexts,"/"));

			return b.toString();

		} else {
			return frontText;
		}
	}

	/**
	 * vias
	 *
	 * @param vias
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OrderColumn(name = "position")
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinTable(name = "destination_display_via", joinColumns = {
			@JoinColumn(name = "destination_display_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "via_id", nullable = false, updatable = false) })
	private List<DestinationDisplay> vias = new ArrayList<>(0);

}
