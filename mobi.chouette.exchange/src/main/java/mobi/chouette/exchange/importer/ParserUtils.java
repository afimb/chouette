package mobi.chouette.exchange.importer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.log4j.Log4j;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Log4j
public class ParserUtils {

	private static DatatypeFactory factory = null;

	static {
		try {
			factory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {

		}
	}

	public static String getText(String value) {
		String result = null;
		if (value != null) {
			result = value.trim();
			result = (result.length() == 0 ? null : result);
		}
		return result;
	}

	public static Integer getInt(String value) {
		Integer result = null;
		if (value != null) {
			result = Integer.valueOf(value);
		}
		return result;
	}

	public static Long getLong(String value) {
		Long result = null;
		if (value != null) {
			result = Long.valueOf(value);
		}
		return result;
	}

	public static Boolean getBoolean(String value) {
		Boolean result = null;
		if (value != null) {
			result = Boolean.valueOf(value);
		}
		return result;
	}

	public static <T extends Enum<T>> T getEnum(Class<T> type, String value) {
		T result = null;
		if (value != null) {
			try {
				result = Enum.valueOf(type, value);
			} catch (Exception ignored) {
			}
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static org.joda.time.Duration getDuration(String value) {
		org.joda.time.Duration result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			try {
				result = new org.joda.time.Duration(factory.newDuration(value).getTimeInMillis(new java.util.Date(0)));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}


	public static Duration getDurationFromTime(String value) throws ParseException {
		Duration result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			LocalTime time = getLocalTime(value);
			result = Duration.standardSeconds(Seconds.secondsBetween(new LocalTime(0), time).getSeconds());
		}
		return result;
	}


	public static LocalTime getLocalTime(String value) throws ParseException {
        LocalTime result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
            result = LocalTime.parse(value, DateTimeFormat.forPattern("HH:mm:ss"));

		}
		return result;
	}

	public static LocalDate getLocalDate(String value) throws ParseException {
		LocalDate result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			result = LocalDate.parse(value, DateTimeFormat.forPattern("yyyy-MM-dd"));
		}
		return result;

	}

	public static LocalDate getDate(DateTimeFormatter format, String value)
			throws ParseException {
		LocalDate result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			result = format.parseLocalDate(value);
		}
		return result;
	}

	public static LocalDate getDate(String value) throws ParseException {
		DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		return getDate(DATE_FORMAT, value);
	}

	public static LocalDateTime getLocalDateTime(String value) throws ParseException {
		LocalDateTime result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			XMLGregorianCalendar calendar = factory
					.newXMLGregorianCalendar(value);
			result = new LocalDateTime(calendar.toGregorianCalendar().getTime());
		}
		return result;
	}

	public static BigDecimal getBigDecimal(String value) {
		BigDecimal result = null;
		if (value != null) {
			try {
				result = BigDecimal.valueOf(Double.valueOf(value));
			} catch (Exception ignored) {
			}
		}
		return result;
	}

	public static BigDecimal getBigDecimal(String value, String pattern) {
		BigDecimal result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			Matcher m = Pattern.compile(pattern).matcher(value.trim());
			if (m.matches()) {
				result = getBigDecimal(m.group(1));

			}
		}
		return result;
	}

	public static BigDecimal getX(String value) {
		return ParserUtils.getBigDecimal(value, "([\\d\\.]+) [\\d\\.]+");
	}

	public static BigDecimal getY(String value) {
		return ParserUtils.getBigDecimal(value, "[\\d\\.]+ ([\\d\\.]+)");
	}

	public static String objectIdPrefix(String objectId) {
		if (objectIdArray(objectId).length > 2) {
			return objectIdArray(objectId)[0].trim();
		} else
			return "";
	}

	public static String objectIdSuffix(String objectId) {
		if (objectIdArray(objectId).length > 2)
			return objectIdArray(objectId)[2].trim();
		else
			return "";
	}

	private static String[] objectIdArray(String objectId) {
		return objectId.split(":");
	}

}
