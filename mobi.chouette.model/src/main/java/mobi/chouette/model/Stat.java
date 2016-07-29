package mobi.chouette.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.StatActionEnum;
import mobi.chouette.model.type.StatFormatEnum;


/**
 * Chouette Stat : Registers chouette actions by date and author (import, export, validation)
 * @author gjamot
 *
 */
@Entity
@Table(name = "stats")
@NoArgsConstructor
@ToString
public class Stat implements Serializable {
	private static final long serialVersionUID = -1406542013260384319L;
	

	@Getter
	@Setter
	@Id @GeneratedValue
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * action
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "action")
	private StatActionEnum action;
	
	/**
	 * format
	 * 
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "format")
	private StatFormatEnum format;
	
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
