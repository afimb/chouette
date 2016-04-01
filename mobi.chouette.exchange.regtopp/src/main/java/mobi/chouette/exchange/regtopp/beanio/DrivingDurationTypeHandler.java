package mobi.chouette.exchange.regtopp.beanio;

import org.apache.commons.lang.StringUtils;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.joda.time.Duration;

public class DrivingDurationTypeHandler implements TypeHandler {

	@Override
	public String format(Object minutes) {
		Duration d = (Duration) minutes;
		if(d == null) {
			return "999";
		} else {
			return StringUtils.leftPad(""+d.toStandardMinutes(),3, '0');
		}
	}

	@Override
	public Class<Duration> getType() {
		return Duration.class;
	}

	@Override
	public Object parse(String minutes) throws TypeConversionException {
		if("999".equals(minutes)) {
			return null;
		} else {
			// Minutes to ms
			return new Duration(Long.parseLong(minutes)*1000*60);
		}
	}

}
