/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

/**
 * basic class for every Chouette object
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@EqualsAndHashCode(of = { "id" })
public abstract class NeptuneObject implements Serializable {

	private static final long serialVersionUID = -1406542019260386319L;

	/**
	 * database id <br/>
	 * null if not saved
	 * 
	 * @param id
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * read annotation to get maximum size of database field
	 * 
	 * @param fieldName
	 *            field name
	 * @return size
	 * @throws NoSuchFieldException
	 */
	private int getfieldSize(String fieldName) throws NoSuchFieldException {
		Class<? extends Object> c = getClass();
		Field f = null;
		while (f == null) {
			try {
				f = c.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				if (c == Object.class)
					throw e;
				c = c.getSuperclass();

			}
		}
		return f.getAnnotation(Column.class).length();
	}

	/**
	 * truncate string value for database suitability
	 * 
	 * @param value
	 *            value to set
	 * @param fieldName
	 *            field name
	 * @param log
	 *            log if truncated
	 * @return truncated or entire value
	 */
	protected String dataBaseSizeProtectedValue(String value, String fieldName,
			Logger log) {
		if (value != null) {
			try {
				int size = getfieldSize(fieldName);
				int inLength = value.length();
				if (inLength > size) {
					log.warn(fieldName + " length > " + size + ", truncated "
							+ value);
					value = value.substring(0, size);
				}
			} catch (NoSuchFieldException ex) {
			} catch (SecurityException ex) {
			}
		}
		return value;

	}

	protected String truncate(String value, int size) {
		if (value.length() > size) {
			value = value.substring(0, size);
		}
		return value;
	}

}
