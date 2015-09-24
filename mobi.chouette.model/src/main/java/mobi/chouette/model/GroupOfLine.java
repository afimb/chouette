package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette GroupOfLine : to associate lines with common purpose
 * <p/>
 * Neptune mapping : GroupOfLine <br/>
 * 
 */
@Entity
@Table(name = "group_of_lines")
@Cacheable
@NoArgsConstructor
@ToString(callSuper=true, exclude = {"lines" })
public class GroupOfLine extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 2900948915585746984L;

	@Getter
	@Setter
	@GenericGenerator(name = "group_of_lines_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "group_of_lines_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "group_of_lines_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name", nullable = false)
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
	 * registration number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "registration_number", unique = true)
	private String registrationNumber;

	/**
	 * set registration number <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setRegistrationNumber(String value) {
		registrationNumber = StringUtils.abbreviate(value, 255);

	}

	/**
	 * grouped Lines
	 * 
	 * @param lines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(mappedBy = "groupOfLines")
	private List<Line> lines = new ArrayList<Line>(0);

	/**
	 * add a line to list only if not already present <br/>
	 * do not affect lineIds list
	 * 
	 * @param line
	 *            line to add
	 */
	public void addLine(Line line) {
		if (!getLines().contains(line)) {
			getLines().add(line);
		}
		if (!line.getGroupOfLines().contains(line)) {
			line.getGroupOfLines().add(this);
		}
	}

	/**
	 * remove a line from the group
	 * 
	 * @param line
	 */
	public void removeLine(Line line) {
		getLines().remove(line);
		line.getGroupOfLines().remove(this);
	}

}
