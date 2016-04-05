package mobi.chouette.exchange.regtopp.beanio;

import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalDateTypeHandler implements TypeHandler {

	@Override
	public String format(Object localDate) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyMMdd");
		return fmt.print((ReadableInstant) localDate);
	}

	@Override
	public Class<LocalDate> getType() {
		return LocalDate.class;
	}

	@Override
	public Object parse(String localDate) throws TypeConversionException {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyMMdd");
		
		DateTime dt =  fmt.parseDateTime(localDate);
		return dt.toLocalDate();
	}

}
