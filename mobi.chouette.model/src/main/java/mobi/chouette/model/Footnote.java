package mobi.chouette.model;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Chouette Footnote : a note for vehicle journeys
 * <p/>
 * Neptune mapping : non (extension in comments <br/>
 * Gtfs mapping : none <br/>
 * Hub mapping : 
 * 
 * @since 2.5.3
 */

@ToString(callSuper=true)
@Entity
@Table(name = "footnotes")
@NoArgsConstructor
@Cacheable
public class Footnote extends NeptuneIdentifiedObject {
	/**
    * 
    */
	private static final long serialVersionUID = -6223882293500225313L;

	@Getter
	@Setter
	@GenericGenerator(name = "footnotes_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "footnotes_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "footnotes_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * label
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "label")
	private String label;

	/**
	 * set label <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setLabel(String value) {
		label = StringUtils.abbreviate(value, 255);
	}

	/**
	 * code
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "code")
	private String code;

	/**
	 * set code <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setCode(String value) {
		code = StringUtils.abbreviate(value, 255);
	}

	/**
	 * relative key for import/export
	 * 
	 * should be unique for each line
	 * 
	 * @param key
	 *            new key
	 * @return The actual key
	 */
	@Getter
	@Setter
	@Transient
	private String key;

	@Getter
	@Setter
	@OneToMany(mappedBy = "footnote", cascade = { CascadeType.PERSIST})
	private List<FootNoteAlternativeText> alternativeTexts = new ArrayList<>();

}
