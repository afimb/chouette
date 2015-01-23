package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import lombok.extern.log4j.Log4j;

import org.trident.schema.trident.RegistrationType;
import org.trident.schema.trident.TridentObjectType;
import org.xml.sax.Locator;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.SharedData.Origin;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public abstract class AbstractModelProducer<T extends NeptuneIdentifiedObject, U extends TridentObjectType>
      extends AbstractProducer implements IModelProducer<T, U>
{
   // Validation CheckPoints
   public static final String COMMON_1 = "2-NEPTUNE-Common-1";
   public static final String COMMON_2 = "2-NEPTUNE-Common-2";
   public static final String COMMON_3 = "2-NEPTUNE-Common-3";


   private static JAXBContextCache jaxbContextCache = new JAXBContextCache();

   public void populateFromCastorNeptune(Context context, T target, U source)
   {
      // ObjectId : maybe null but not empty
      // TODO : Mandatory ?
      target.setObjectId(getNonEmptyTrimedString(source.getObjectId()));
      if (target.getObjectId() == null)
      {
         ReportItem item = new ExchangeReportItem(
               ExchangeReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR,
               "ObjectId");
         context.getImportReport().addItem(item);
      }

      // ObjectVersion
      if (source.getObjectVersion() != null)
      {
         int jaxbObjectVersion = source.getObjectVersion().intValue();
         target.setObjectVersion(jaxbObjectVersion);
      }

      // CreationTime : maybe null
      if (source.getCreationTime() != null)
      {
         target.setCreationTime(source.getCreationTime().toGregorianCalendar()
               .getTime());
      } else
      {
         target.setCreationTime(Calendar.getInstance().getTime());
      }

      // CreatorId : maybe null but not empty
      target.setCreatorId(getNonEmptyTrimedString(source.getCreatorId()));

   }

   protected String getRegistrationNumber(Context context, RegistrationType registration)
   {
      if (registration == null)
         return null;
      String number = registration.getRegistrationNumber();
      if (number == null || number.trim().length() == 0)
      {
         return null;
      }
      return number.trim();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private String marshal(JAXBContext jc, U object) throws Exception
   {
      Class<? extends TridentObjectType> clazz = object.getClass();
      Marshaller marshaller = jc.createMarshaller();
      // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
      // Boolean.TRUE);
      StringWriter writer = new StringWriter();
      marshaller.marshal(new JAXBElement(new QName(clazz.getSimpleName()),
            clazz, object), writer);
      return writer.toString();
   }

   protected String toString(U object)
   {

      String name = object.getClass().getPackage().getName();
      JAXBContext jc;
      try
      {
         jc = jaxbContextCache.getContext(name);
         String text = marshal(jc, object);
         return text;
      } catch (Exception e)
      {
         log.error("unable to marshal object " + name, e);
      }
      return "unable to marshal object " + name;

   }

   protected T checkUnsharedData(Context context,
         T object, U source)
   {
      prepareCheckPoint(context, COMMON_3);
      try
      {
         context.getUnshareableData().add(object, context.getSourceFile());
      }
      catch (ExchangeException e)
      {
         // error already set : add detail
         Locator srcLoc = source.sourceLocation();
         ReportLocation location = new ReportLocation(context.getSourceFile(),
               srcLoc.getLineNumber(), srcLoc.getColumnNumber());
         DetailReportItem detail = new DetailReportItem(COMMON_3,
               object.getObjectId(), Report.STATE.ERROR, location, null);
         addValidationError(context, COMMON_3, detail);
         return null;         
      }
      return object;
   }
   
   @SuppressWarnings("unchecked")
   protected T getOrAddSharedData(Context context,
         T model,  U source)
   {
      prepareCheckPoint(context, COMMON_1);
      SharedData data = (SharedData) context.getSharedData().get(model.getClass(),
            model.getObjectId());
      if (data == null)
      {
         data = new SharedData(model, context.getSourceFile(), toString(source),
               source.sourceLocation());
         context.getSharedData().add(model.getClass(), model.getObjectId(), data);
      } else
      {
         // 2-NEPTUNE-Common-1 : check if a shareable object is identical in
         // each file
         String sourceObject = toString(source);
         data.addOrigin(context.getSourceFile(), sourceObject, source.sourceLocation());
         if (data.isDuplicationError())
         {
            // error already set : add detail
            Locator srcLoc = source.sourceLocation();
            ReportLocation location = new ReportLocation(context.getSourceFile(),
                  srcLoc.getLineNumber(), srcLoc.getColumnNumber());
            DetailReportItem detail = new DetailReportItem(COMMON_1,
                  model.getObjectId(), Report.STATE.WARNING, location, null);
            addValidationError(context, COMMON_1, detail);

         } else
         {
            boolean duplicationError = false;
            for (Origin origin : data.getOrigins())
            {
               if (!origin.getSourceData().equals(sourceObject))
               {
                  duplicationError = true;
                  break;
               }
            }
            if (duplicationError)
            {
               data.setDuplicationError(true);
               // first difference, add all location already encountered
               for (Origin origin : data.getOrigins())
               {
                  Locator srcLoc = origin.getSourceLocation();
                  ReportLocation location = new ReportLocation(
                        origin.getSourceFile(), srcLoc.getLineNumber(),
                        srcLoc.getColumnNumber());
                  DetailReportItem detail = new DetailReportItem(COMMON_1,
                        model.getObjectId(), Report.STATE.WARNING, location,
                        null);
                  addValidationError(context, COMMON_1, detail);
               }
            }
         }
      }

      checkRegistrationNumber(context, model, source);
      return (T) data.getObject();
   }

   private void checkRegistrationNumber(Context context,
         T model, U source)
   {
      if (model.getRegistrationNumber() == null
            || model.getRegistrationNumber().isEmpty())
         return;
      prepareCheckPoint(context, COMMON_2);
      // 2-NEPTUNE-Common-2 : check if a shareable object has a unique
      // registrationNumber
      SharedData data = (SharedData) context.getSharedData().get(model.getClass(),
            model.getRegistrationNumber());
      if (data == null)
      {
         data = new SharedData(model,context.getSourceFile(), source.getObjectId(),
               source.sourceLocation());
         context.getSharedData().add(model.getClass(),
               model.getRegistrationNumber(), data);
      } else
      {
         String sourceObject = source.getObjectId();
         data.addOrigin(context.getSourceFile(), sourceObject, source.sourceLocation());
         if (data.isDuplicationError())
         {
            // error already set : add detail
            Locator srcLoc = source.sourceLocation();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("RegistrationNumber", model.getRegistrationNumber());
            ReportLocation location = new ReportLocation(context.getSourceFile(),
                  srcLoc.getLineNumber(), srcLoc.getColumnNumber());
            DetailReportItem detail = new DetailReportItem(COMMON_2,
                  source.getObjectId(), Report.STATE.ERROR, location, map);
            addValidationError(context, COMMON_2, detail);

         } else
         {
            boolean duplicationError = false;
            for (Origin origin : data.getOrigins())
            {
               if (!origin.getSourceData().equals(sourceObject))
               {
                  duplicationError = true;
                  break;
               }
            }
            if (duplicationError)
            {
               data.setDuplicationError(true);
               // first difference, add all location already encountered
               for (Origin origin : data.getOrigins())
               {
                  Locator srcLoc = origin.getSourceLocation();
                  Map<String, Object> map = new HashMap<String, Object>();
                  map.put("RegistrationNumber", model.getRegistrationNumber());
                  ReportLocation location = new ReportLocation(
                        origin.getSourceFile(), srcLoc.getLineNumber(),
                        srcLoc.getColumnNumber());
                  DetailReportItem detail = new DetailReportItem(COMMON_2,
                        origin.getSourceData(), Report.STATE.ERROR, location,
                        map);
                  addValidationError(context, COMMON_2, detail);
               }
            }
         }
      }

   }

   protected void addValidationError(Context context,
         String checkPointKey, DetailReportItem item)
   {
      CheckPointReportItem checkPoint = context.getValidationReport().getItem(checkPointKey);
      checkPoint.addItem(item);

   }

   protected void prepareCheckPoint(Context context,
         String checkPointKey)
   {
      CheckPointReportItem checkPoint = context.getValidationReport().getItem(checkPointKey);
      if (!checkPoint.hasItems())
         checkPoint.updateStatus(Report.STATE.OK);
   }
}
