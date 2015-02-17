package mobi.chouette.exchange.neptune.model.facility;

import java.io.Serializable;

public enum MobilityFacilityEnumeration implements Serializable
{
   // ------------------/
   // - Enum Constants -/
   // ------------------/

   /**
    * Constant PTI23_255_4
    */
   PTI23_255_4("pti23_255_4"),
   /**
    * Constant UNKNOWN
    */
   UNKNOWN("unknown"),
   /**
    * Constant PTI23_16
    */
   PTI23_16("pti23_16"),
   /**
    * Constant SUITABLEFORWHEELCHAIRS
    */
   SUITABLEFORWHEELCHAIRS("suitableForWheelChairs"),
   /**
    * Constant PTI23_16_1
    */
   PTI23_16_1("pti23_16_1"),
   /**
    * Constant LOWFLOOR
    */
   LOWFLOOR("lowFloor"),
   /**
    * Constant PTI23_16_2
    */
   PTI23_16_2("pti23_16_2"),
   /**
    * Constant BOARDINGASSISTANCE
    */
   BOARDINGASSISTANCE("boardingAssistance"),
   /**
    * Constant PTI23_16_3
    */
   PTI23_16_3("pti23_16_3"),
   /**
    * Constant STEPFREEACCESS
    */
   STEPFREEACCESS("stepFreeAccess"),
   /**
    * Constant TACTILEPATFORMEDGES
    */
   TACTILEPATFORMEDGES("tactilePatformEdges"),
   /**
    * Constant ONBOARDASSISTANCE
    */
   ONBOARDASSISTANCE("onboardAssistance"),
   /**
    * Constant UNACCOMPANIEDMINORASSISTANCE
    */
   UNACCOMPANIEDMINORASSISTANCE("unaccompaniedMinorAssistance"),
   /**
    * Constant AUDIOINFORMATION
    */
   AUDIOINFORMATION("audioInformation"),
   /**
    * Constant VISUALINFORMATION
    */
   VISUALINFORMATION("visualInformation"),
   /**
    * Constant DISPLAYSFORVISUALLYIMPAIRED
    */
   DISPLAYSFORVISUALLYIMPAIRED("displaysForVisuallyImpaired"),
   /**
    * Constant AUDIOFORHEARINGIMPAIRED
    */
   AUDIOFORHEARINGIMPAIRED("audioForHearingImpaired");

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

   private MobilityFacilityEnumeration(final java.lang.String value)
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
   public static MobilityFacilityEnumeration fromValue(
         final java.lang.String value)
   {
      for (MobilityFacilityEnumeration c : MobilityFacilityEnumeration.values())
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
