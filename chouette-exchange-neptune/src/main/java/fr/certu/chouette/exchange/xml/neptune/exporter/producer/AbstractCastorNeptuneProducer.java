package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.exolab.castor.types.Duration;
import org.exolab.castor.types.Time;

import chouette.schema.AccessibilitySuitabilityDetails;
import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.Registration;
import chouette.schema.TridentObjectTypeType;
import chouette.schema.UserNeedGroup;
import chouette.schema.types.EncumbranceEnumeration;
import chouette.schema.types.MedicalNeedEnumeration;
import chouette.schema.types.MobilityEnumeration;
import chouette.schema.types.PyschosensoryNeedEnumeration;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public abstract class AbstractCastorNeptuneProducer<T extends TridentObjectTypeType, U extends NeptuneIdentifiedObject> implements ICastorNeptuneProducer<T, U>
{

   public void populateFromModel(T target,U source)
   {
      // ObjectId : maybe null but not empty
      // TODO : Mandatory ?
      target.setObjectId(source.getObjectId());

      // ObjectVersion
      if (source.getObjectVersion() > 0)
         target.setObjectVersion(source.getObjectVersion());

      // CreationTime : maybe null
      if (source.getCreationTime() != null)
         target.setCreationTime(source.getCreationTime());

      // CreatorId : maybe null but not empty
      if (source.getCreatorId() != null)
         target.setCreatorId(source.getCreatorId());

   }

   protected Registration getRegistration(String registrationNumber) 
   {
      if (registrationNumber == null) return null;
      if (registrationNumber.trim().isEmpty()) return null;
      Registration registration = new Registration();
      registration.setRegistrationNumber(registrationNumber);
      return registration;
   }

   protected String getNonEmptyObjectId(NeptuneIdentifiedObject object) 
   {
      if (object == null) return null;
      return object.getObjectId();
   }

   protected AccessibilitySuitabilityDetails extractAccessibilitySuitabilityDetails(List<UserNeedEnum> userNeeds){
      AccessibilitySuitabilityDetails details = new AccessibilitySuitabilityDetails();
      List<AccessibilitySuitabilityDetailsItem> detailsItems = new ArrayList<AccessibilitySuitabilityDetailsItem>();
      if(userNeeds != null){
         for(UserNeedEnum userNeed : userNeeds){
            if(userNeed != null){
               UserNeedGroup userNeedGroup = new UserNeedGroup();

               switch (userNeed.category()) {
               case ENCUMBRANCE:
                  userNeedGroup.setEncumbranceNeed(EncumbranceEnumeration.fromValue(userNeed.value()));
                  break;
               case MEDICAL:
                  userNeedGroup.setMedicalNeed(MedicalNeedEnumeration.fromValue(userNeed.value()));					
                  break;
               case PSYCHOSENSORY:
                  userNeedGroup.setPsychosensoryNeed(PyschosensoryNeedEnumeration.fromValue(userNeed.value()));	
                  break;
               case MOBILITY:
                  userNeedGroup.setMobilityNeed(MobilityEnumeration.fromValue(userNeed.value()));	
                  break;
               default:
                  throw new IllegalArgumentException("bad value of userNeed");
               }

               if(userNeedGroup.getChoiceValue() != null){
                  AccessibilitySuitabilityDetailsItem item = new AccessibilitySuitabilityDetailsItem();
                  item.setUserNeedGroup(userNeedGroup);
                  detailsItems.add(item);
               }
            }
         }
      }

      if (detailsItems.isEmpty()) return null;
      details.setAccessibilitySuitabilityDetailsItem(detailsItems);
      return details;
   }


   protected Time toCastorTime( Date neptuneTime)
   {
      if ( neptuneTime==null) return null;

      short[] aTimeVal = new short[ 4];
      Calendar aCalendar = Calendar.getInstance();
      aCalendar.setTime( neptuneTime);

      aTimeVal[ 0] = ( short)aCalendar.get( Calendar.HOUR_OF_DAY);
      aTimeVal[ 1] = ( short)aCalendar.get( Calendar.MINUTE);
      aTimeVal[ 2] = ( short)aCalendar.get( Calendar.SECOND);
      aTimeVal[ 3] = ( short)aCalendar.get( Calendar.MILLISECOND);

      return new Time(aTimeVal);
   }


   public abstract T produce(U o);

   protected String getNotEmptyString(String value)
   {
      if (value == null) return null;
      if (value.trim().isEmpty()) return null;
      return value;
   }

   protected Duration toDuration(java.sql.Time time)
   {
      Duration duration = new Duration();
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(time.getTime());
      duration.setHour((short)c.get(Calendar.HOUR_OF_DAY));
      duration.setMinute((short)c.get(Calendar.MINUTE));
      duration.setSeconds((short)c.get(Calendar.SECOND));
      return duration;
   }
}
