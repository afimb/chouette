package mobi.chouette.model.blueprint;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(StopArea.class)
public class StopAreaBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:StopArea:" + UUID.randomUUID();
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
   ChouetteAreaEnum areaType = ChouetteAreaEnum.BoardingPosition;

   @Nullable
   @Mapped
   StopArea parent;

   @MappedList(target = StopArea.class, size = 0)
   List<StopArea> containedStopAreas;

   @MappedList(target = StopPoint.class, size = 0)
   List<StopPoint> containedStopPoints;

}
