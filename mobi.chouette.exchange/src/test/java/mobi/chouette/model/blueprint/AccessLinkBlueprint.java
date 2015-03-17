package mobi.chouette.model.blueprint;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.UUID;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.LinkOrientationEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(AccessLink.class)
public class AccessLinkBlueprint
{

@Default
//   String objectId = "TEST:AccessLink:" + UUID.randomUUID();
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:AccessLink:" + UUID.randomUUID();
      }

   };

   @Default
   String name = "AccessLink";

   @Default
   int objectVersion = 1;

   @Default
   ConnectionLinkTypeEnum linkType = ConnectionLinkTypeEnum.Mixed;

   @Default
   LinkOrientationEnum linkOrientation = LinkOrientationEnum.AccessPointToStopArea;

   @Default
   BigDecimal linkDistance = new BigDecimal(2);

   @Default
   Time defaultDuration = new Time(173335738);

   @Default
   Time frequentTravellerDuration = new Time(173335738);

   @Default
   Time occasionalTravellerDuration = new Time(173335738);

   @Mapped
   boolean mobilityRestrictedSuitable = true;

}
