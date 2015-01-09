package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/**
 * Chouette GroupOfLine : to associate lines with common purpose
 * <p/>
 * Neptune mapping : GroupOfLine <br/>
 * 
 */
@Entity
@Table(name = "group_of_lines")
@NoArgsConstructor
@ToString
@Log4j
public class GroupOfLine extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 2900948915585746984L;

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
		name = dataBaseSizeProtectedValue(value, "name", log);
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
		comment = dataBaseSizeProtectedValue(value, "comment", log);
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
