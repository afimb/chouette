package mobi.chouette.model.blueprint;

import java.util.List;
import java.util.UUID;

import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.type.TransportModeNameEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.callback.FieldCallback;

@Blueprint(Line.class)
public class LineBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:Line:" + UUID.randomUUID();
      }

   };

   @Default
   String name = "7B";

   @Default
   String number = "7Bis";

   @Default
   String publishedName = "Mairie d Issy porte d Orleans";

   @Default
   String comment = "Extension à partir de juin";

   @Default
   int objectVersion = 1;

   @Default
   TransportModeNameEnum transportModeName = TransportModeNameEnum.Metro;

   @Default
   String registrationNumber = "100110107";

   @Mapped
   PTNetwork ptNetwork;

   @Mapped
   Company company;

   @MappedList(target = Route.class, size = 0)
   List<Route> routes;

   @MappedList(target = GroupOfLine.class, size = 0)
   List<GroupOfLine> groupOfLines;

}
