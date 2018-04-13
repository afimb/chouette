package mobi.chouette.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Reusable Key-value structure.
 *
 */
@ToString(callSuper=true)
@Embeddable
@Table(name = "key_values")
@NoArgsConstructor
@Cacheable
public class KeyValue {

	/**
	 * key
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "key")
	private String key;


	/**
	 * value
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "value")
	private String value;


	/**
	 * Type of key.
	 *
	 *
	 * @param typeOfKey
	 *            new typeOfKey
	 * @return The actual type of key
	 */
	@Getter
	@Setter
	@Column(name = "type_of_key")
	private String typeOfKey;
}
