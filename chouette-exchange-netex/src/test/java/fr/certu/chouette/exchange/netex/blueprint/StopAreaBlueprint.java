package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import com.tobedevoured.modelcitizen.field.FieldCallback;
// import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Blueprint(StopArea.class)
public class StopAreaBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "RATP_PIVI:StopArea:" + UUID.randomUUID();
      }

   };

   @Default
   int objectVersion = 1;

   @Default
   String name = "A" + UUID.randomUUID();

   @Default
   String comment = "mon arret " + UUID.randomUUID();

   @Default
   String registrationNumber = "C-" + UUID.randomUUID();

   @Default
   String nearestTopicName = "POI-" + UUID.randomUUID();

   @Default
   int fareCode = 1;

   @Default
   LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;

   @Default
   BigDecimal longitude = new BigDecimal(2.373D + (UUID.randomUUID()
         .getLeastSignificantBits() % 100) / 1000000);

   @Default
   BigDecimal latitude = new BigDecimal(48.8D + (UUID.randomUUID()
         .getLeastSignificantBits() % 100) / 1000000);

   @Default
   String streetName = "Rue " + UUID.randomUUID();

   @Default
   String countryCode = "ZIP " + UUID.randomUUID();

   @Default
   String projectionType = "EPSG:9801";

   @Default
   BigDecimal x = new BigDecimal(602747 + (UUID.randomUUID()
         .getLeastSignificantBits() % 100));

   @Default
   BigDecimal y = new BigDecimal(2431390 + (UUID.randomUUID()
         .getLeastSignificantBits() % 100));

   @Default
   ChouetteAreaEnum areaType = ChouetteAreaEnum.BoardingPosition;

   @Nullable
   @Mapped
   StopArea parent;

   @MappedList(target = StopArea.class, size = 0)
   List<StopArea> containedStopAreas;

   @MappedList(target = StopPoint.class, size = 0)
   List<StopPoint> containedStopPoints;

}
