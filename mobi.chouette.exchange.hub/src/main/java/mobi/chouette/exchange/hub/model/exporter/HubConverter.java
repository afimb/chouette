package mobi.chouette.exchange.hub.model.exporter;

import java.sql.Date;
import java.text.SimpleDateFormat;

import mobi.chouette.exchange.hub.model.HubException;


public interface HubConverter {

	public static DefaultFieldConverter<String> STRING_CONVERTER = new DefaultFieldConverter<String>() {

		@Override
		protected String convertTo(String input) throws Exception {
			return (input != null) ? input.toString() : "";
		}

	};


	public static DefaultFieldConverter<Number> NUMBER_CONVERTER = new DefaultFieldConverter<Number>() {

		@Override
		protected String convertTo(Number input) throws Exception {
			return (input != null) ? input.toString() : "";
		}

	};
	
	@SuppressWarnings("rawtypes")
	public static DefaultFieldConverter<Enum> ENUM_CONVERTER = new DefaultFieldConverter<Enum>() {

		@Override
		protected String convertTo(Enum input) throws Exception {
			return (input != null) ? input.name() : "";
		}

	};
	
	public static DefaultFieldConverter<Boolean> BOOLEAN_CONVERTER = new DefaultFieldConverter<Boolean>() {

		@Override
		protected String convertTo(Boolean input) throws Exception {
			return (input != null) ? (input.booleanValue() ? "1" : "0" ) : "";
		}

	};


	public static DefaultFieldConverter<Date> DATE_CONVERTER = new DefaultFieldConverter<Date>() {

		private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		@Override
		protected String convertTo(Date input) throws Exception {
			return (input != null) ? formater.format(input) : "";
		}

	};

	public abstract class DefaultFieldConverter<T> extends
	FieldConverter<String, T> {

		@SuppressWarnings("rawtypes")
		@Override
		public synchronized String to(HubContext hubContext, Enum field, T input, boolean required) {
			String result = "";
			if (input != null) {
				try {
					result = convertTo(input);
				} catch (Exception e) {
					hubContext.put(HubContext.FIELD, field.name());
					hubContext.put(HubContext.ERROR,
							HubException.ERROR.INVALID_FORMAT);
					hubContext.put(HubContext.CODE, "TODO");
					hubContext.put(HubContext.VALUE, input);
					throw new HubException(hubContext, e);
				}
			} else if (required) {
				hubContext.put(HubContext.FIELD, field.name());
				hubContext.put(HubContext.ERROR, HubException.ERROR.MISSING_FIELD);
				hubContext.put(HubContext.CODE, "TODO");
				throw new HubException(hubContext);
			}
			return result;
		}


		protected abstract String convertTo(T input) throws Exception;
	}
	public abstract class FieldConverter<F, T> {

		@SuppressWarnings("rawtypes")
		public abstract F to(HubContext hubContext, Enum field, T input,
				boolean required);
	}

	public abstract class Converter<F, T> {


		public abstract F to(HubContext hubContext, T input);

	}

}
