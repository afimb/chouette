package mobi.chouette.model.api;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@EqualsAndHashCode(of = { "id" })
@Log4j
@ToString()
@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "jobs")
@Data
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "jobs_seq", sequenceName = "jobs_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobs_seq")
	@Column(name = "id", nullable = false)
	@XmlAttribute(name = "id")
	private Long id;

	@XmlAttribute(name = "referential")
	@Column(name = "referential")
	private String referential;

	@XmlAttribute(name = "action")
	@Column(name = "action")
	private String action;

	@XmlAttribute(name = "type")
	@Column(name = "type")
	private String type;

	@XmlElement(name = "parameters")
	@Transient
	private Object parameters;
	
	
	@XmlAttribute(name = "path")
	@Column(name = "path")
	private String path;

	@XmlAttribute(name = "filename")
	@Column(name = "filename")
	private String filename;

	@XmlAttribute(name = "created")
	@Column(name = "created")
	private Date created;

	@XmlAttribute(name = "updated")
	@Column(name = "updated")
	private Date updated;

	@XmlAttribute(name = "status")
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private STATUS status;

	@XmlElement(name = "links")
	@ElementCollection(targetClass = Link.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "links", joinColumns = @JoinColumn(name = "job_id"))
	private List<Link> links = new ArrayList<Link>();

	public Job() {
		super();
		status = STATUS.CREATED;
		created = new Date();
		updated = new Date();
	}

	@XmlType
	@XmlEnum(String.class)
	public enum STATUS implements java.io.Serializable {
		CREATED, SCHEDULED, TERMINATED, CANCELED, ABORTED
	}
}
