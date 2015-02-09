package mobi.chouette.exchange.validation.report;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Detail {

	public enum STATE {
		UNCHECK, OK, WARNING, ERROR, FATAL
	};

	@XmlElement(name = "location")
	private Location location;

	@XmlAttribute(name = "object_id")
	private String objectId;
	
	@XmlAttribute(name = "state")
	private STATE state;

	@XmlElement(name = "message_args")
	private Map<String, String> messageArgs = new HashMap<String, String>();

	@XmlAttribute(name = "message_key")
	private String messageKey;

	
	   public Detail(String key, String objectId, STATE state,
		         Location location, Map<String, Object> args)
		   {

		      setMessageKey("detail_" + key.replaceAll("-", "_").toLowerCase());
		      this.state = state;
		      this.location = location;
		      this.objectId = objectId;
		      setArgs(args);

		   }

		   public Detail(String key, String objectId, STATE state,
		         Location location)
		   {

		      setMessageKey("detail_" + key.replaceAll("-", "_").toLowerCase());
		      this.state = state;
		      this.location = location;
		      this.objectId = objectId;
		      this.messageArgs.clear();

		   }

		   public Detail(String key, STATE state, Location location,
		         Map<String, Object> args)
		   {
		      setMessageKey("detail_" + key.replaceAll("-", "_").toLowerCase());
		      this.state = state;
		      this.location = location;
		      this.objectId = null;
		      setArgs(args);

		   }
		   
		   private void setArgs(Map<String, Object> args)
		   {
			   for (String key : args.keySet()) 
			   {
				   messageArgs.put(key, args.get(key).toString());
			}
		   }

}
