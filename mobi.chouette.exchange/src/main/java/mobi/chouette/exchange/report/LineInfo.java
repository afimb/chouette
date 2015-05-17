package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NonNull;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name","status","stats","errors"})
@Data
public class LineInfo {

	@XmlType(name="lineState")
	@XmlEnum
	public enum LINE_STATE 
	{
		OK,
		WARNING,
		ERROR
	};
	@XmlElement(name = "name",required=true)
	@NonNull
	private String name;

	@XmlElement(name = "status",required=true)
	private LINE_STATE status = LINE_STATE.OK;
	
	@XmlElement(name = "stats",required=true)
	private DataStats stats = new DataStats();

	@XmlElement(name="errors")
	private List<LineError> errors = new ArrayList<>();
	
	/**
	 * add an error; status will be set to ERROR
	 * 
	 * @param error
	 */
	public void addError(LineError error)
	{
		status = LINE_STATE.ERROR;
		errors.add(error);
	}


}
