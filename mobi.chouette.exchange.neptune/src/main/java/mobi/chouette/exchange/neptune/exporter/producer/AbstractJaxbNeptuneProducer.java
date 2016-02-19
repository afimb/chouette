package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Route;

import org.apache.log4j.Logger;
import org.trident.schema.trident.ObjectFactory;
import org.trident.schema.trident.RegistrationType;
import org.trident.schema.trident.TridentObjectType;

public abstract class AbstractJaxbNeptuneProducer<T extends TridentObjectType, U extends NeptuneIdentifiedObject>
// implements IJaxbNeptuneProducer<T, U>
{
	public static DatatypeFactory typeFactory = null;
	public static ObjectFactory tridentFactory = null;
	public static uk.org.siri.siri.ObjectFactory siriFactory = null;

	static {
		try {
			typeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		tridentFactory = new ObjectFactory();
		siriFactory = new uk.org.siri.siri.ObjectFactory();

	}

	public void populateFromModel(T target, U source) {
		// ObjectId : maybe null but not empty
		// TODO : Mandatory ?
		target.setObjectId(source.getObjectId());

		// ObjectVersion
		if (source.getObjectVersion() > 0)
			target.setObjectVersion(BigInteger.valueOf(source.getObjectVersion()));

		// CreationTime : maybe null
		if (source.getCreationTime() != null)
			target.setCreationTime(toCalendar(source.getCreationTime()));

		// CreatorId : maybe null but not empty
		if (source.getCreatorId() != null)
			target.setCreatorId(source.getCreatorId());

	}

	protected RegistrationType getRegistration(String registrationNumber) {
		if (registrationNumber == null)
			return null;
		if (registrationNumber.trim().isEmpty())
			return null;
		RegistrationType registration = tridentFactory.createRegistrationType();
		registration.setRegistrationNumber(registrationNumber);
		return registration;
	}

	protected String getNonEmptyObjectId(NeptuneIdentifiedObject object) {
		if (object == null)
			return null;
		return object.getObjectId();
	}

	// public abstract T produce(U o, boolean addExtension);

	protected String getNotEmptyString(String value) {
		if (value == null)
			return null;
		if (value.trim().isEmpty())
			return null;
		return value;
	}

	protected boolean isEmpty(String value) {
		return (value == null || value.trim().isEmpty());
	}

	protected boolean isEmpty(Collection<?> value) {
		return (value == null || value.isEmpty());
	}

	protected Duration toDuration(java.sql.Time time) {
		if (time == null)
			return null;
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(time.getTime());
		long h = c.get(Calendar.HOUR_OF_DAY);
		long m = c.get(Calendar.MINUTE);
		long s = c.get(Calendar.SECOND);
		long millis = (h * 3600 + m * 60 + s) * 1000;
		Duration duration = typeFactory.newDuration(millis);
		return duration;
	}

	protected XMLGregorianCalendar toCalendar(Date date) {
		if (date == null)
			return null;
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar c = typeFactory.newXMLGregorianCalendar(cal);
		c.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		c.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
		return c;
	}

	@SuppressWarnings("unused")
	protected boolean hasOppositeRoute(Route route, Logger log) {
		// protect tests from opposite_id invalid foreign key
		try {
			Route wayBack = route.getOppositeRoute();
			if (wayBack != null) {
				String o = wayBack.getObjectId();
				return true;
			}
		} catch (Exception ex) {
			log.error("problem with oppositeRoute foreign key ");
			// route.unsetOppositeRoute();
		}
		return false;
	}

}
