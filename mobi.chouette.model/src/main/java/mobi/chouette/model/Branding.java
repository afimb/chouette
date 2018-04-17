package mobi.chouette.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette Branding : marketing classification.
 * <p/>
 *
 * @since 2.5.3
 */

@ToString(callSuper = true)
@Entity
@Table(name = "brandings")
@NoArgsConstructor
@Cacheable
public class Branding extends NeptuneIdentifiedObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -6223882293500225313L;

	@Getter
	@Setter
	@GenericGenerator(name = "brandings_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "brandings_id_seq"),
			@Parameter(name = "increment_size", value = "10")})
	@GeneratedValue(generator = "brandings_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * name
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "name")
	private String name;

	/**
	 * description
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "description")
	private String description;

	/**
	 * url
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "url")
	private String url;

	/**
	 * image - url to an image
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "image")
	private String image;
}
