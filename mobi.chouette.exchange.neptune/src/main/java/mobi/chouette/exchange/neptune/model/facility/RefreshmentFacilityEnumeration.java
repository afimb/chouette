package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum RefreshmentFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant PTI23_1
    */
   PTI23_1("pti23_1"),
   /**
    * Constant RESTAURANTSERVICE
    */
   RESTAURANTSERVICE("restaurantService"),
   /**
    * Constant PTI23_2
    */
   PTI23_2("pti23_2"),
   /**
    * Constant SNACKSSERVICE
    */
   SNACKSSERVICE("snacksService"),
   /**
    * Constant PTI23
    */
   PTI23("pti23"),
   /**
    * Constant TROLLEY
    */
   TROLLEY("trolley"),
   /**
    * Constant PTI23_18
    */
   PTI23_18("pti23_18"),
   /**
    * Constant BAR
    */
   BAR("bar"),
   /**
    * Constant PTI23_19
    */
   PTI23_19("pti23_19"),
   /**
    * Constant FOODNOTAVAILABLE
    */
   FOODNOTAVAILABLE("foodNotAvailable"),
   /**
    * Constant PTI23_20
    */
   PTI23_20("pti23_20"),
   /**
    * Constant BEVERAGESNOTAVAILABLE
    */
   BEVERAGESNOTAVAILABLE("beveragesNotAvailable"),
   /**
    * Constant PTI23_26
    */
   PTI23_26("pti23_26"),
   /**
    * Constant BISTRO
    */
   BISTRO("bistro"),
   /**
    * Constant FOODVENDINGMACHINE
    */
   FOODVENDINGMACHINE("foodVendingMachine"),
   /**
    * Constant BEVERAGEVENDINGMACHINE
    */
   BEVERAGEVENDINGMACHINE("beverageVendingMachine");

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

   private RefreshmentFacilityEnumeration(final java.lang.String value)
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
   public static RefreshmentFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (RefreshmentFacilityEnumeration c : RefreshmentFacilityEnumeration
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
