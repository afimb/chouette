package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum RetailFacilityEnumeration implements Serializable
{

   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant FOOD
    */
   FOOD("food"),
   /**
    * Constant NEWSPAPERTOBACCO
    */
   NEWSPAPERTOBACCO("newspaperTobacco"),
   /**
    * Constant RECREATIONTRAVEL
    */
   RECREATIONTRAVEL("recreationTravel"),
   /**
    * Constant HYGIENEHEALTHBEAUTY
    */
   HYGIENEHEALTHBEAUTY("hygieneHealthBeauty"),
   /**
    * Constant FASHIONACCESSORIES
    */
   FASHIONACCESSORIES("fashionAccessories"),
   /**
    * Constant BANKFINANCEINSURANCE
    */
   BANKFINANCEINSURANCE("bankFinanceInsurance"),
   /**
    * Constant CASHMACHINE
    */
   CASHMACHINE("cashMachine"),
   /**
    * Constant CURRENCYEXCHANGE
    */
   CURRENCYEXCHANGE("currencyExchange"),
   /**
    * Constant TOURISMSERVICE
    */
   TOURISMSERVICE("tourismService"),
   /**
    * Constant PHOTOBOOTH
    */
   PHOTOBOOTH("photoBooth");

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

   private RetailFacilityEnumeration(final java.lang.String value)
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
   public static RetailFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (RetailFacilityEnumeration c : RetailFacilityEnumeration.values())
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
