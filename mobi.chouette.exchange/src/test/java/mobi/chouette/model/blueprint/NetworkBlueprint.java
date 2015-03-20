package mobi.chouette.model.blueprint;

import java.util.Date;
import java.util.UUID;

import mobi.chouette.model.Network;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(Network.class)
public class NetworkBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:PTNetwork:" + UUID.randomUUID();
      }

   };

   @Default
   String name = "METRO";

   @Default
   int objectVersion = 1;

   // TODO : Verify date
   @Default
   Date versionDate = new Date(1234479600);

   @Default
   String description = "Réseau de métro de la RATP";

   @Default
   String registrationNumber = "110";

   @Default
   String sourceName = "RATP";

   @Default
   String sourceIdentifier = "RATP-KXD";

   @Default
   String comment = "Mon réseau";

   @Default
   PTNetworkSourceTypeEnum sourceType = PTNetworkSourceTypeEnum.PublicTransport;

}
