package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum FareClassFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant PTI23_0
    */
   PTI23_0("pti23_0"),
   /**
    * Constant UNKNOWN_2
    */
   UNKNOWN_2("unknown_2"),
   /**
    * Constant PTI23_6
    */
   PTI23_6("pti23_6"),
   /**
    * Constant FIRSTCLASS
    */
   FIRSTCLASS("firstClass"),
   /**
    * Constant PTI23_7
    */
   PTI23_7("pti23_7"),
   /**
    * Constant SECONDCLASS
    */
   SECONDCLASS("secondClass"),
   /**
    * Constant PTI23_8
    */
   PTI23_8("pti23_8"),
   /**
    * Constant THIRDCLASS
    */
   THIRDCLASS("thirdClass"),
   /**
    * Constant PTI23_9
    */
   PTI23_9("pti23_9"),
   /**
    * Constant ECONOMYCLASS
    */
   ECONOMYCLASS("economyClass"),
   /**
    * Constant PTI23_10
    */
   PTI23_10("pti23_10"),
   /**
    * Constant BUSINESSCLASS
    */
   BUSINESSCLASS("businessClass");

   // --------------------------/
   // - Class/Member Variables -/
   // --------------------------/

   /**
    * Field value.
    */
   private final java.lang.String value;

   // ----------------/
   // - Constructors -/
   // ----------------/

   private FareClassFacilityEnumeration(final java.lang.String value)
   {
      this.value = value;
   }

   // -----------/
   // - Methods -/
   // -----------/

   /**
    * Method fromValue.
    * 
    * @param value
    * @return the constant for this value
    */
   public static FareClassFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (FareClassFacilityEnumeration c : FareClassFacilityEnumeration
            .values())
      {
         if (c.value.equals(value))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(value);
   }

   /**
    * 
    * 
    * @param value
    */
   public void setValue(final java.lang.String value)
   {
   }

   /**
    * Method toString.
    * 
    * @return the value of this constant
    */
   public java.lang.String toString()
   {
      return this.value;
   }

   /**
    * Method value.
    * 
    * @return the value of this constant
    */
   public java.lang.String value()
   {
      return this.value;
   }

}
