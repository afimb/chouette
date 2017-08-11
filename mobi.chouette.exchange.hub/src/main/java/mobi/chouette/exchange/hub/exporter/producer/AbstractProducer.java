package mobi.chouette.exchange.hub.exporter.producer;

import java.util.Collection;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.hub.model.HubObject;
import mobi.chouette.exchange.hub.model.exporter.HubExporterInterface;
import mobi.chouette.model.NeptuneIdentifiedObject;

import org.joda.time.LocalTime;

@Log4j
public abstract class AbstractProducer {

	@Getter
	private HubExporterInterface exporter;

	public AbstractProducer(HubExporterInterface exporter) {
		if (exporter == null)
		{
			log.error("exporter cannot be null");
			throw new IllegalArgumentException("exporter cannot be null");
		}
		this.exporter = exporter;
	}

	static protected String toHubId(NeptuneIdentifiedObject neptuneObject) {
		if (neptuneObject == null || neptuneObject.getObjectId() == null)
			return null;
		String[] tokens = neptuneObject.getObjectId().split(":");
		return tokens[2];
	}

	static protected boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	static protected boolean isTrue(Boolean b) {
		return b != null && b;
	}

	static protected boolean isFalse(Boolean b) {
		return b != null && !b;
	}


	static protected boolean isEmpty(Collection<? extends Object> s) {
		return s == null || s.isEmpty();
	}

	static protected String getValue(String s) {
		if (isEmpty(s))
			return null;
		else
			return s;

	}

	static protected Integer toHubTime(LocalTime time)
	{
		if (time == null) return null;
		long seconds = ((time.getHourOfDay() * 60) + time.getMinuteOfHour()) * 60 + time.getSecondOfMinute();
		return Integer.valueOf((int) seconds);
	}

	static protected Integer toHubDelay(LocalTime startTime, LocalTime endTime)
	{
		if (startTime == null || endTime == null) return null;
		long startSeconds =  toHubTime(startTime);
		long endSeconds = toHubTime(endTime);
		
		long seconds = endSeconds - startSeconds;
		return Integer.valueOf((int) seconds);
	}
	
	static protected Integer toInt(String value)
	{
		if (value == null) return null;
		try
		{
			return Integer.decode(value);
		}
		catch (NumberFormatException ex)
		{
			return null;
		}
	}
	
	static protected int toSens(String wayBack)
	{
		return "R".equals(wayBack)? HubObject.SENS_RETOUR : HubObject.SENS_ALLER ;
	}

}
