package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum HireFacilityEnumeration implements Serializable
{
   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant CARHIRE
    */
   CARHIRE("carHire"),
   /**
    * Constant MOTORCYCLEHIRE
    */
   MOTORCYCLEHIRE("motorCycleHire"),
   /**
    * Constant CYCLEHIRE
    */
   CYCLEHIRE("cycleHire"),
   /**
    * Constant TAXI
    */
   TAXI("taxi"),
   /**
    * Constant RECREATIONDEVICEHIRE
    */
   RECREATIONDEVICEHIRE("recreationDeviceHire");

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

   private HireFacilityEnumeration(final java.lang.String value)
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
   public static HireFacilityEnumeration fromValue(final java.lang.String value)
   {
      for (HireFacilityEnumeration c : HireFacilityEnumeration.values())
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
