package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.awt.Color;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.IllegalFormatException;
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
      protected String from(String input) throws Exception
      {
         return input.trim();
      }

      @Override
      protected String to(String input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Integer> INTEGER_CONVERTER = new DefaultFieldConverter<Integer>()
   {

      @Override
      protected Integer from(String input) throws Exception
      {
         return Integer.parseInt(input, 10);
      }

      @Override
      protected String to(Integer input) throws Exception
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static DefaultFieldConverter<Boolean> BOOLEAN_CONVERTER = new DefaultFieldConverter<Boolean>()
   {

      @Override
      protected Boolean from(String input) throws Exception
      {
         boolean value = input.equals("0");
         if (value)
         {
            return true;
         } else
         {
            value = input.equals("1");
            if (value)
            {
               return false;
            } else
            {

               throw new IllegalArgumentException();
            }
         }
      }

      @Override
      protected String to(Boolean input) throws Exception
      {
         return (input != null) ? (input) ? "1" : "0" : "0";
      }

   };

   public static FieldConverter<String, Float> FLOAT_CONVERTER = new FieldConverter<String, Float>()
   {

      @Override
      public Float from(Context context, Enum field, String input, Float value,
            boolean required)
      {
         Float result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = Float.parseFloat(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }
         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Float input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, Double> DOUBLE_CONVERTER = new FieldConverter<String, Double>()
   {

      @Override
      public Double from(Context context, Enum field, String input,
            Double value, boolean required)
      {
         Double result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = Double.parseDouble(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }
         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Double input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, Time> TIME_CONVERTER = new FieldConverter<String, Time>()
   {

      @Override
      public Time from(Context context, Enum field, String input, Time value,
            boolean required)
      {
         Time result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = Time.valueOf(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }
         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Time input)
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static FieldConverter<String, Date> DATE_CONVERTER = new FieldConverter<String, Date>()
   {

      @Override
      public Date from(Context context, Enum field, String input, Date value,
            boolean required)
      {

         Date result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = new Date(BASIC_ISO_DATE.parse(input).getTime());
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }

         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Date input)
      {
         return (input != null) ? BASIC_ISO_DATE.format(input) : "";
      }

   };

   public static FieldConverter<String, URL> URL_CONVERTER = new FieldConverter<String, URL>()
   {

      @Override
      public URL from(Context context, Enum field, String input, URL value,
            boolean required)
      {
         URL result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = new URL(input);
               String protocol = result.getProtocol();
               if (!(protocol.equals("http") || protocol.equals("https")))
               {
                  context.put(Context.FIELD, field.name());
                  context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
                  throw new GtfsException(context);
               }
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }
         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, URL input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, TimeZone> TIMEZONE_CONVERTER = new FieldConverter<String, TimeZone>()
   {

      @Override
      public TimeZone from(Context context, Enum field, String input,
            TimeZone value, boolean required)
      {
         TimeZone result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = TimeZone.getTimeZone(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }

         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, TimeZone input)
      {
         return (input != null) ? input.getDisplayName() : "";
      }
   };

   public static FieldConverter<String, Color> COLOR_CONVERTER = new FieldConverter<String, Color>()
   {

      @Override
      public Color from(Context context, Enum field, String input, Color value,
            boolean required)
      {
         Color result = value;
         if (input != null && !input.isEmpty())
         {
            try
            {
               result = new Color(Integer.parseInt(input, 16));
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }

         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, Color input)
      {
         return (input != null) ? Integer.toHexString(input.getRGB())
               .substring(2) : "";
      }
   };

   public static FieldConverter<String, GtfsTime> GTFSTIME_CONVERTER = new FieldConverter<String, GtfsTime>()
   {

      private final DateFormat format = DateFormat
            .getTimeInstance(DateFormat.MEDIUM);

      @Override
      public GtfsTime from(Context context, Enum field, String input,
            GtfsTime value, boolean required)
      {
         GtfsTime result = value;
         if (input != null && !input.isEmpty())
         {
            result = decode(input);
         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, GtfsTime input)
      {
         String result = "";
         if (input != null && input.getTime() != null)
         {
            result = decode(input);
         }
         return result;
      }

      private GtfsTime decode(String input)
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

      private String decode(GtfsTime input)
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
         return (hourString + ":" + minuteString + ":" + secondString);
      }

   };

   public static DefaultFieldConverter<PickupType> PICKUP_CONVERTER = new DefaultFieldConverter<PickupType>()
   {

      @Override
      protected PickupType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return PickupType.values()[ordinal];
      }

      @Override
      protected String to(PickupType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<DropOffType> DROPOFFTYPE_CONVERTER = new DefaultFieldConverter<DropOffType>()
   {

      @Override
      protected DropOffType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return DropOffType.values()[ordinal];
      }

      @Override
      protected String to(DropOffType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<ExceptionType> EXCEPTIONTYPE_CONVERTER = new DefaultFieldConverter<ExceptionType>()
   {

      @Override
      protected ExceptionType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return ExceptionType.values()[ordinal];
      }

      @Override
      protected String to(ExceptionType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<RouteType> ROUTETYPE_CONVERTER = new DefaultFieldConverter<RouteType>()
   {

      @Override
      protected RouteType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return RouteType.values()[ordinal];
      }

      @Override
      protected String to(RouteType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<LocationType> LOCATIONTYPE_CONVERTER = new DefaultFieldConverter<LocationType>()
   {

      @Override
      protected LocationType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return LocationType.values()[ordinal];
      }

      @Override
      protected String to(LocationType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<WheelchairBoardingType> WHEELCHAIRBOARDINGTYPE_CONVERTER = new DefaultFieldConverter<WheelchairBoardingType>()
   {

      @Override
      protected WheelchairBoardingType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return WheelchairBoardingType.values()[ordinal];
      }

      @Override
      protected String to(WheelchairBoardingType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<DirectionType> DIRECTIONTYPE_CONVERTER = new DefaultFieldConverter<DirectionType>()
   {

      @Override
      protected DirectionType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return DirectionType.values()[ordinal];
      }

      @Override
      protected String to(DirectionType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<WheelchairAccessibleType> WHEELCHAIRACCESSIBLETYPE_CONVERTER = new DefaultFieldConverter<WheelchairAccessibleType>()
   {

      @Override
      protected WheelchairAccessibleType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return WheelchairAccessibleType.values()[ordinal];
      }

      @Override
      protected String to(WheelchairAccessibleType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<BikesAllowedType> BIKESALLOWEDTYPE_CONVERTER = new DefaultFieldConverter<BikesAllowedType>()
   {
      @Override
      protected BikesAllowedType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return BikesAllowedType.values()[ordinal];
      }

      @Override
      protected String to(BikesAllowedType input) throws Exception
      {
         return String.valueOf(input.ordinal());
      }
   };

   public static DefaultFieldConverter<TransferType> TRANSFERTYPE_CONVERTER = new DefaultFieldConverter<TransferType>()
   {
      @Override
      protected TransferType from(String input) throws Exception
      {
         int ordinal = Integer.parseInt(input, 10);
         return TransferType.values()[ordinal];
      }

      @Override
      protected String to(TransferType input) throws Exception
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
               result = from(input);
            } catch (Exception e)
            {
               context.put(Context.FIELD, field.name());
               context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
               throw new GtfsException(context, e);
            }

         } else if (required && value == null)
         {
            context.put(Context.FIELD, field.name());
            context.put(Context.CODE, GtfsException.ERROR.MISSING_FIELD);
            throw new GtfsException(context);
         }
         return result;
      }

      @Override
      public String to(Context context, T input)
      {
         try
         {
            return to(input);
         } catch (Exception e)
         {
            context.put(Context.CODE, GtfsException.ERROR.INVALID_FORMAT);
            throw new GtfsException(context, e);
         }
      }

      protected abstract T from(String input) throws Exception;

      protected abstract String to(T input) throws Exception;
   }

   public abstract class FieldConverter<F, T>
   {

      // TODO [DSU] modif exporter
      public T from(F input, boolean required)
      {
         return from(null, null, null, required);
      }

      public T from(F input, T value, boolean required)
      {
         return from(null, null, input, value, required);
      }

      public T from(Context context, Enum field, F input)
      {
         return from(context, field, input, null, false);
      }

      public T from(Context context, Enum field, F input, boolean required)
      {
         return from(context, field, input, null, required);
      }

      public abstract T from(Context context, Enum field, F input, T value,
            boolean required);

      public F to(T input)
      {
         return to(null, input);
      }

      public abstract F to(Context context, T input);

   }

   public abstract class Converter<F, T>
   {

      public abstract T from(F input);

      public abstract F to(T input);

   }
}
