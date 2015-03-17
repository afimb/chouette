package mobi.chouette.model.blueprint;

import java.util.UUID;

import mobi.chouette.model.GroupOfLine;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(GroupOfLine.class)
public class GroupOfLineBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:GroupOfLine:" + UUID.randomUUID();
      }

   };

   @Default
   String name = "Noctilien";

   @Default
   int objectVersion = 1;

   @Default
   String comment = "Réseau de nuit";

}
