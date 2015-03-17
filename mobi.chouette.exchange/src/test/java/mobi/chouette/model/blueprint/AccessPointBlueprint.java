package mobi.chouette.model.blueprint;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.UUID;

import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.type.AccessPointTypeEnum;
import mobi.chouette.model.type.LongLatTypeEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(AccessPoint.class)
public class AccessPointBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:AccessPoint:" + UUID.randomUUID();
      }

   };

   @Default
   String name = "AccessPoint";

   @Default
   String comment = "AccessPoint Comment";

   @Default
   int objectVersion = 1;

   @Default
   LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;

   @Default
   BigDecimal longitude = new BigDecimal(2.373D + (UUID.randomUUID()
         .getLeastSignificantBits() % 100) / 1000000);

   @Default
   BigDecimal latitude = new BigDecimal(48.8D + (UUID.randomUUID()
         .getLeastSignificantBits() % 100) / 1000000);

   @Default
   AccessPointTypeEnum type = AccessPointTypeEnum.InOut;

   @Default
   Time openingTime = new Time(173335738);

   @Default
   Time closingTime = new Time(173335738);

}
