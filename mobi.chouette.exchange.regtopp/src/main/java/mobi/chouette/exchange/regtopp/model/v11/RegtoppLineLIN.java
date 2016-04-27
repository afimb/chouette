package mobi.chouette.exchange.regtopp.model.v11;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppLineLIN extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "LIN";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0, length = 3)
	private String adminCode;

	@Getter
	@Setter
	@Field(at = 3, length = 1)
	private String counter;

	@Getter
	@Setter
	@Field(at = 4, length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(at = 8, length = 30)
	private String name;

	// Hack to work around some different implmenentations
	@Setter
	@Field(at = 38, length = 1, minLength = 0, maxOccurs = 10)
	private char[] fareCodeHack;

	public String getFareCode() {
		return new String(fareCodeHack).trim();
	}
	
	@Override
	public String getIndexingKey() {
		return adminCode + counter + lineId;
	}

}
