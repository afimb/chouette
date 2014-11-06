package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate.ExceptionType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute.RouteType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.LocationType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer.TransferType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.BikesAllowedType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.DirectionType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.WheelchairAccessibleType;

public interface GtfsConverter
{

   public static SimpleDateFormat BASIC_ISO_DATE = new SimpleDateFormat(
         "yyyyMMdd");

   public static DefaultFieldConverter<String> STRING_CONVERTER = new DefaultFieldConverter<String>()
   {

      @Override
      protected String convertFrom(String input) throws Exception
      {
         return input.trim();
      }

      @Override
      protected String convertTo(String input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Integer> INTEGER_CONVERTER = new DefaultFieldConverter<Integer>()
   {

      @Override
      protected Integer convertFrom(String input) throws Exception
      {
         return Integer.parseInt(input, 10);
      }

      @Override
      protected String convertTo(Integer input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Integer> POSITIVE_INTEGER_CONVERTER = new DefaultFieldConverter<Integer>()
   {

      @Override
      protected Integer convertFrom(String input) throws Exception
      {
         int result = Integer.parseInt(input, 10);
         if (result < 0)
         {
            throw new NumberFormatException();
         }
         return result;
      }

      @Override
      protected String convertTo(Integer input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Boolean> BOOLEAN_CONVERTER = new DefaultFieldConverter<Boolean>()
   {

      @Override
      protected Boolean convertFrom(String input) throws Exception
      {
         boolean value = input.equals("0");
         if (value)
         {
            return false;
         } else
         {
            value = input.equals("1");
            if (value)
            {
               return true;
            } else
            {

               throw new IllegalArgumentException();
            }
         }
      }

      @Override
      protected String convertTo(Boolean input) throws Exception
      {
         return (input != null) ? (input) ? "1" : "0" : "0";
      }

   };

   public static DefaultFieldConverter<Float> FLOAT_CONVERTER = new DefaultFieldConverter<Float>()
   {

      @Override
      protected Float convertFrom(String input) throws Exception
      {
         return Float.parseFloat(input);
      }

      @Override
      protected String convertTo(Float input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static DefaultFieldConverter<Double> DOUBLE_CONVERTER = new DefaultFieldConverter<Double>()
   {

      @Override
      protected Double convertFrom(String input) throws Exception
      {
         return Double.parseDouble(input);
      }

      @Override
      protected String convertTo(Double input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static DefaultFieldConverter<Time> TIME_CONVERTER = new DefaultFieldConverter<Time>()
   {

      @Override
      protected Time convertFrom(String input) throws Exception
      {
         return Time.valueOf(input);
      }

      @Override
      protected String convertTo(Time input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Date> DATE_CONVERTER = new DefaultFieldConverter<Date>()
   {

      @Override
      protected Date convertFrom(String input) throws Exception
      {
         return new Date(BASIC_ISO_DATE.parse(input).getTime());
      }

      @Override
      protected String convertTo(Date input) throws Exception
      {
         return (input != null) ? BASIC_ISO_DATE.format(input) : "";
      }

   };

   public static DefaultFieldConverter<URL> URL_CONVERTER = new DefaultFieldConverter<URL>()
   {

      @Override
      protected URL convertFrom(String input) throws Exception
      {
         URL result = new URL(input);
         String protocol = result.getProtocol();
         if (!(protocol.equals("http") || protocol.equals("https")))
         {
            throw new MalformedURLException();
         }
         return result;
      }

      @Override
      protected String convertTo(URL input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static DefaultFieldConverter<TimeZone> TIMEZONE_CONVERTER = new DefaultFieldConverter<TimeZone>()
   {

      @Override
      protected TimeZone convertFrom(String input) throws Exception
      {
         return TimeZone.getTimeZone(input);
      }

      @Override
      protected String convertTo(TimeZone input) throws Exception
      {
         return (input != null) ? input.getID() : "";
      }
   };

   public static DefaultFieldConverter<Color> COLOR_CONVERTER = new DefaultFieldConverter<Color>()
   {

      @Override
      protected Color convertFrom(String input) throws Exception
      {
         return new Color(Integer.parseInt(input, 16));
      }

      @Override
      protected String convertTo(Color input) throws Exception
      {
         return (input != null) ? Integer.toHexString(input.getRGB())
               .substring(2) : "";
      }
   };

   public static DefaultFieldConverter<GtfsTime> GTFSTIME_CONVERTER = new DefaultFieldConverter<GtfsTime>()
   {

      @Override
      protected GtfsTime convertFrom(String input) throws Exception
      {
         GtfsTime result = new GtfsTime();
         int day;
         int hour;
         int minute;
         int second;
         int firstColon;
         int secondColon;

         if (input == null)
            throw new java.lang.IllegalArgumentException();

         firstColon = input.indexOf(':');
         secondColon = input.indexOf(':', firstColon + 1);
         if ((firstColon > 0) & (secondColon > 0)
               & (secondColon < input.length() - 1))
         {
            hour = Integer.parseInt(input.substring(0, firstColon));
            day = hour / 24;
            hour %= 24;
            minute = Integer.parseInt(input.substring(firstColon + 1,
                  secondColon));
            second = Integer.parseInt(input.substring(secondColon + 1));
         } else
         {
            throw new java.lang.IllegalArgumentException();
         }

         result.setTime(new Time(hour, minute, second));
         result.setDay(day);

         return result;
      }

      @Override
      protected String convertTo(GtfsTime input) throws Exception
      {
         String result = "";
         if (input != null && input.getTime() != null)
         {

            Time value = input.getTime();

            int hour = value.getHours() + input.getDay();
            int minute = value.getMinutes();
            int second = value.getSeconds();
            String hourString;
            String minuteString;
            String secondString;

            if (hour < 10)
            {
               hourString = "0" + hour;
            } else
            {
               hourString = Integer.toString(hour);
            }
            if (minute < 10)
            {
               minuteString = "0" + minute;
            } else
            {
               minuteString = Integer.toString(minute);
            }
            if (second < 10)
            {
               secondString = "0" + second;
            } else
            {
               secondString = Integer.toString(second);
            }
            result = (hourString + ":" + minuteString + ":" + secondString);

         }
         return result;
      }
   };

   public static DefaultFieldConverter<PickupType> PICKUP_CONVERTER = new DefaultFieldConverter<PickupType>()
   {

      @Override
      protected PickupType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return PickupType.values()[ordinal];
      }

      @Override
      protected String convertTo(PickupType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<DropOffType> DROPOFFTYPE_CONVERTER = new DefaultFieldConverter<DropOffType>()
   {

      @Override
      protected DropOffType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return DropOffType.values()[ordinal];
      }

      @Override
      protected String convertTo(DropOffType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<ExceptionType> EXCEPTIONTYPE_CONVERTER = new DefaultFieldConverter<ExceptionType>()
   {

      @Override
      protected ExceptionType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return ExceptionType.values()[ordinal];
      }

      @Override
      protected String convertTo(ExceptionType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<RouteType> ROUTETYPE_CONVERTER = new DefaultFieldConverter<RouteType>()
   {

      @Override
      protected RouteType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return RouteType.values()[ordinal];
      }

      @Override
      protected String convertTo(RouteType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<LocationType> LOCATIONTYPE_CONVERTER = new DefaultFieldConverter<LocationType>()
   {

      @Override
      protected LocationType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return LocationType.values()[ordinal];
      }

      @Override
      protected String convertTo(LocationType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<WheelchairBoardingType> WHEELCHAIRBOARDINGTYPE_CONVERTER = new DefaultFieldConverter<WheelchairBoardingType>()
   {

      @Override
      protected WheelchairBoardingType convertFrom(String input)
            throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return WheelchairBoardingType.values()[ordinal];
      }

      @Override
      protected String convertTo(WheelchairBoardingType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<DirectionType> DIRECTIONTYPE_CONVERTER = new DefaultFieldConverter<DirectionType>()
   {

      @Override
      protected DirectionType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return DirectionType.values()[ordinal];
      }

      @Override
      protected String convertTo(DirectionType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<WheelchairAccessibleType> WHEELCHAIRACCESSIBLETYPE_CONVERTER = new DefaultFieldConverter<WheelchairAccessibleType>()
   {

      @Override
      protected WheelchairAccessibleType convertFrom(String input)
            throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return WheelchairAccessibleType.values()[ordinal];
      }

      @Override
      protected String convertTo(WheelchairAccessibleType input)
            throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<BikesAllowedType> BIKESALLOWEDTYPE_CONVERTER = new DefaultFieldConverter<BikesAllowedType>()
   {
      @Override
      protected BikesAllowedType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return BikesAllowedType.values()[ordinal];
      }

      @Override
      protected String convertTo(BikesAllowedType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<TransferType> TRANSFERTYPE_CONVERTER = new DefaultFieldConverter<TransferType>()
   {
      @Override
      protected TransferType convertFrom(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return TransferType.values()[ordinal];
      }

      @Override
      protected String convertTo(TransferType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public abstract class DefaultFieldConverter<T> extends
         FieldConverter<String, T>
   {
      @Override
      public T from(Context context, Enum field, String input, T value,
            boolean required)
      {
         T result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = convertFrom(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.ERROR, GtfsException.ERROR.INVALID_FORMAT);
               context.put(Context.CODE, "TODO");
               context.put(Context.VALUE, input);
               throw new GtfsException(context, e);
            }

         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.ERROR, GtfsException.ERROR.MISSING_FIELD);
            context.put(Context.CODE, "TODO");
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Enum field, T input, boolean required)
      {
         String result = "";
         if (input != null)
         {
            try
            {
               result = convertTo(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.ERROR, GtfsException.ERROR.INVALID_FORMAT);
               context.put(Context.CODE, "TODO");
               context.put(Context.VALUE, input);
               throw new GtfsException(context, e);
            }
         } else if (required)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.ERROR, GtfsException.ERROR.MISSING_FIELD);
            context.put(Context.CODE, "TODO");
            throw new GtfsException(context);
         }
         return result;
      }

      protected abstract T convertFrom(String input) throws Exception;

      protected abstract String convertTo(T input) throws Exception;
   }

   public abstract class FieldConverter<F, T>
   {

      public T from(Context context, Enum field, F input, boolean required)
      {
         return from(context, field, input, null, required);
      }

      public abstract T from(Context context, Enum field, F input, T value,
            boolean required);

      public abstract F to(Context context, Enum field, T input,
            boolean required);
   }

   public abstract class Converter<F, T>
   {

      public abstract T from(Context context ,F input);

      public abstract F to(Context context , T input);

   }
}
