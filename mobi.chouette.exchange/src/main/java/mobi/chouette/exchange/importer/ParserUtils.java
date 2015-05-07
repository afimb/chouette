package mobi.chouette.exchange.importer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.log4j.Log4j;

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
	public static Time getSQLDuration(String value) {
		Time result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			try {
				Duration duration = factory.newDuration(value);
				result = new Time(duration.getHours(), duration.getMinutes(),
						duration.getSeconds());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public static Time getSQLTime(String value) throws ParseException {
		Time result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
			result = new Time(getDate(TIME_FORMAT, value).getTime());

		}
		return result;
	}

	public static Date getSQLDateTime(String value) throws ParseException {
		Date result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			XMLGregorianCalendar calendar = factory
					.newXMLGregorianCalendar(value);
			result = new Date(calendar.toGregorianCalendar().getTimeInMillis());
		}
		return result;
	}

	public static Date getSQLDate(String value) throws ParseException {
		Date result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
			long  time = getDate(SHORT_DATE_FORMAT, value).getTime();
			result = new Date(time);
		}
		return result;
	}

	public static java.util.Date getDate(DateFormat format, String value)
			throws ParseException {
		java.util.Date result = null;
		assert value != null : "[DSU] invalid value : " + value;

		if (value != null) {
			result = format.parse(value);
		}
		return result;
	}

	public static java.util.Date getDate(String value) throws ParseException {
		DateFormat DATE_FORMAT = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		return getDate(DATE_FORMAT, value);
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
