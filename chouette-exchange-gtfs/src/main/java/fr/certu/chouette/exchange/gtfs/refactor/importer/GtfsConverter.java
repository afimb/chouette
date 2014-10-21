package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate.ExceptionType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute.RouteType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.LocationType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop.WheelchairBoardingType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.BikesAllowedType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip.DirectionType;

public interface GtfsConverter
{

   public static FieldConverter<String, String> STRING_CONVERTER = new FieldConverter<String, String>()
   {

      @Override
      public String from(String input, String value, boolean required)
      {
         String result = value;
         if (input != null && !input.isEmpty())
         {
            result = input.trim();
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(String input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, Integer> INTEGER_CONVERTER = new FieldConverter<String, Integer>()
   {

      @Override
      public Integer from(String input, Integer value, boolean required)
      {
         Integer result = value;
         if (input != null && !input.isEmpty())
         {
            result = Integer.parseInt(input, 10);
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(Integer input)
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static FieldConverter<String, Float> FLOAT_CONVERTER = new FieldConverter<String, Float>()
   {

      @Override
      public Float from(String input, Float value, boolean required)
      {
         Float result = value;
         if (input != null && !input.isEmpty())
         {
            result = Float.parseFloat(input);
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(Float input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, Double> DOUBLE_CONVERTER = new FieldConverter<String, Double>()
   {

      @Override
      public Double from(String input, Double value, boolean required)
      {
         Double result = value;
         if (input != null && !input.isEmpty())
         {
            result = Double.parseDouble(input);
         } else if (required && value == null)
         {
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(Double input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, Time> TIME_CONVERTER = new FieldConverter<String, Time>()
   {

      @Override
      public Time from(String input, Time value, boolean required)
      {
         Time result = value;
         if (input != null && !input.isEmpty())
         {
            result = Time.valueOf(input);
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(Time input)
      {
         return (input != null) ? input.toString() : "";
      }

   };

   public static FieldConverter<String, Date> DATE_CONVERTER = new FieldConverter<String, Date>()
   {

      @Override
      public Date from(String input, Date value, boolean required)
      {

         Date result = value;
         if (input != null && !input.isEmpty())
         {
            result = Date.valueOf(input);

         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(Date input)
      {
         return (input != null) ? input.toString() : "";
      }
   };

   public static FieldConverter<String, GtfsTime> GTFSTIME_CONVERTER = new FieldConverter<String, GtfsTime>()
   {

      private final DateFormat format = DateFormat
            .getTimeInstance(DateFormat.MEDIUM);

      @Override
      public GtfsTime from(String input, GtfsTime value, boolean required)
      {
         GtfsTime result = value;
         if (input != null && !input.isEmpty())
         {
            result = convert(input);
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(GtfsTime input)
      {
         String result = "";
         if (input != null && input.getTime() != null)
         {
            result = convert(input);
         }
         return result;
      }

      private GtfsTime convert(String input)
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

      private String convert(GtfsTime input)
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

   public static FieldConverter<String, PickupType> PICKUP_CONVERTER = new FieldConverter<String, PickupType>()
   {

      @Override
      public PickupType from(String input, PickupType value, boolean required)
      {
         PickupType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = PickupType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(PickupType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, DropOffType> DROPOFFTYPE_CONVERTER = new FieldConverter<String, DropOffType>()
   {

      @Override
      public DropOffType from(String input, DropOffType value, boolean required)
      {
         DropOffType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = DropOffType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(DropOffType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, ExceptionType> EXCEPTIONTYPE_CONVERTER = new FieldConverter<String, ExceptionType>()
   {

      @Override
      public ExceptionType from(String input, ExceptionType value,
            boolean required)
      {
         ExceptionType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = ExceptionType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(ExceptionType input)
      {
         String result = "1";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, RouteType> ROUTETYPE_CONVERTER = new FieldConverter<String, RouteType>()
   {

      @Override
      public RouteType from(String input, RouteType value, boolean required)
      {
         RouteType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = RouteType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(RouteType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, LocationType> LOCATIONTYPE_CONVERTER = new FieldConverter<String, LocationType>()
   {

      @Override
      public LocationType from(String input, LocationType value,
            boolean required)
      {
         LocationType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = LocationType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(LocationType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, WheelchairBoardingType> WHEELCHAIRBOARDINGTYPE_CONVERTER = new FieldConverter<String, WheelchairBoardingType>()
   {

      @Override
      public WheelchairBoardingType from(String input,
            WheelchairBoardingType value, boolean required)
      {
         WheelchairBoardingType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = WheelchairBoardingType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(WheelchairBoardingType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, DirectionType> DIRECTIONTYPE_CONVERTER = new FieldConverter<String, DirectionType>()
   {

      @Override
      public DirectionType from(String input, DirectionType value,
            boolean required)
      {
         DirectionType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = DirectionType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(DirectionType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public static FieldConverter<String, BikesAllowedType> BIKESALLOWEDTYPE_CONVERTER = new FieldConverter<String, BikesAllowedType>()
   {

      @Override
      public BikesAllowedType from(String input, BikesAllowedType value,
            boolean required)
      {
         BikesAllowedType result = value;
         if (input != null && !input.isEmpty())
         {
            int ordinal = Integer.parseInt(input, 10);
            result = BikesAllowedType.values()[ordinal];
         } else if (required && value == null)
         {
            throw new IllegalArgumentException();
         }
         return result;
      }

      @Override
      public String to(BikesAllowedType input)
      {
         String result = "0";
         if (input != null)
         {
            result = String.valueOf(input.ordinal());
         }

         return result;
      }
   };

   public abstract class FieldConverter<F, T> extends Converter<F, T>
   {

      @Override
      public T from(F input)
      {
         return from(input, null, false);
      }

      public T from(F input, boolean required)
      {
         return from(input, null, required);
      }

      public abstract T from(F input, T value, boolean required);

   }

   public abstract class Converter<F, T>
   {

      public abstract T from(F input);

      public abstract F to(T input);

   }

}
