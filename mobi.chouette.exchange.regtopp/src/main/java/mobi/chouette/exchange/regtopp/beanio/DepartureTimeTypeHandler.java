package mobi.chouette.exchange.regtopp.beanio;

import org.apache.commons.lang.StringUtils;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.joda.time.Duration;

public class DepartureTimeTypeHandler implements TypeHandler {

	@Override
	public String format(Object localTime) {
		Duration d = (Duration) localTime;
		long standardDays = d.getStandardDays();
		long standardHours = d.getStandardHours();
		long standardMinutes = d.getStandardMinutes();

		if (standardDays > 0) {
			standardHours += 24;
		}

		return StringUtils.leftPad("" + standardHours, 2, '0') + StringUtils.leftPad("" + standardMinutes, 2, '0');

	}

	@Override
	public Class<Duration> getType() {
		return Duration.class;
	}

	@Override
	public Object parse(String localTime) throws TypeConversionException {
		String hoursString = localTime.substring(0,2);
		String minutesString = localTime.substring(2, 4);
		
		long hours = Long.parseLong(hoursString);
		long minutes = Long.parseLong(minutesString);
		
		return new Duration(hours*60*60*1000 + minutes*60*1000);
	}

}
