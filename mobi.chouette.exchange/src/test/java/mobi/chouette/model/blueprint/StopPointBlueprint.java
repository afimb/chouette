package mobi.chouette.model.blueprint;

import java.util.UUID;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(StopPoint.class)
public class StopPointBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:StopPoint:" + UUID.randomUUID();
      }

   };

   @Default
   int objectVersion = 1;

   @Nullable
   @Mapped
   StopArea containedInStopArea;

   // @Default
   // BigDecimal longitude = new BigDecimal("1.27");
   //
   // @Default
   // BigDecimal latitude = new BigDecimal("2.27");

   @Default
   int position = 1;

}
