package mobi.chouette.model.iev;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Chouette Stat : Registers chouette actions by date and author 
 * @author gjamot
 *
 */
@Entity
@Table(name = "stats")
@NoArgsConstructor
@ToString
public class Stat implements Serializable {
	private static final long serialVersionUID = -1406542013260384319L;
	
	/**
	 * id
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "stats_seq", sequenceName = "stats_seq", allocationSize = 20)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stats_seq")
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * referential
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "referential")
	private String referential;
	
	/**
	 * action
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "action")
	private String action;
	
	/**
	 * format
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "format")
	private String format;
	
	/**
	 * date
	 * 
	 * @param date
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "date")
	private Date date;
}
