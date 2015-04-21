package mobi.chouette.model.api;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Todo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@XmlAttribute(name = "name")
	private String name;
	
	private int toto;

	/**
	 * @return the toto
	 */
	public int getToto() {
		return toto;
	}

	/**
	 * @param toto the toto to set
	 */
	public void setToto(int toto) {
		this.toto = toto;
	}

}
