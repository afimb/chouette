package mobi.chouette.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@EqualsAndHashCode(of = { "id" })
@Log4j
@ToString(exclude = { "parameters", "validation" })
@XmlRootElement(name = "job")
@Entity
@Table(name = "jobs")
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "jobs_seq", sequenceName = "jobs_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobs_seq")
	@Column(name = "id", nullable = false)
	@XmlAttribute(name = "id")
	private Long id;

	@XmlAttribute(name = "referential")
	@Column(name = "referential")
	@Getter
	@Setter
	private String referential;

	@XmlAttribute(name = "action")
	@Column(name = "action")
	@Getter
	@Setter
	private String action;

	@XmlAttribute(name = "type")
	@Column(name = "type")
	@Getter
	@Setter
	private String type;

	@XmlAttribute(name = "parameters")
	@Column(name = "parameters", columnDefinition = "TEXT")
	@Getter
	@Setter
	private String parameters;

	@XmlAttribute(name = "validation")
	@Column(name = "validation", columnDefinition = "TEXT")
	@Getter
	@Setter
	private String validation;

	@XmlAttribute(name = "filename")
	@Column(name = "filename")
	@Getter
	@Setter
	private String filename;

	@XmlAttribute(name = "date")
	@Column(name = "date")
	@Getter
	@Setter
	private Date date;

	@XmlAttribute(name = "status")
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@Getter
	@Setter
	private STATUS status;

	@XmlElement(name = "links")
	@ElementCollection(targetClass = Link.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "links", joinColumns = @JoinColumn(name = "job_id"))
	@Getter
	@Setter
	private List<Link> links = new ArrayList<Link>();

	public Job() {
		super();
		status = STATUS.CREATED;
		date = new Date();
	}

	@XmlType
	@XmlEnum(String.class)
	public enum STATUS implements java.io.Serializable {
		CREATED, SCHEDULED, TERMINATED, CANCELED, ABORTED
	}
}
