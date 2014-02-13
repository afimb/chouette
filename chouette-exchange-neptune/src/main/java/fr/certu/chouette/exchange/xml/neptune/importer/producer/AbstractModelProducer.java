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

import fr.certu.chouette.exchange.xml.neptune.importer.producer.SharedData.Origin;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public abstract class AbstractModelProducer<T extends NeptuneIdentifiedObject, U extends TridentObjectType> extends AbstractProducer implements IModelProducer<T, U>
{
    // Validation CheckPoints
	public static final String COMMON_1 = "2-NEPTUNE-Common-1";
	public static final String COMMON_2 = "2-NEPTUNE-Common-2";
	
	
	public void populateFromCastorNeptune(T target,U source ,ReportItem report)
	{
		// ObjectId : maybe null but not empty
		// TODO : Mandatory ?
		target.setObjectId(getNonEmptyTrimedString(source.getObjectId()));
		if (target.getObjectId() == null)
		{
			ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.MANDATORY_TAG,Report.STATE.ERROR,"ObjectId") ;
			report.addItem(item);
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
			target.setCreationTime(source.getCreationTime().toGregorianCalendar().getTime());
		}
		else
		{
			target.setCreationTime(Calendar.getInstance().getTime());
		}

		// CreatorId : maybe null but not empty
		target.setCreatorId(getNonEmptyTrimedString(source.getCreatorId()));

	}


	protected String getRegistrationNumber(RegistrationType registration,ReportItem report) 
	{
		if (registration == null) return null;
		String number = registration.getRegistrationNumber();
		if (number == null || number.trim().length() == 0) 
		{
//			ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.MANDATORY_TAG,Report.STATE.ERROR,"RegistrationNumber") ;
//			report.addItem(item);
			return null;
		}
		return number.trim();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String marshal(JAXBContext jc,U object) throws Exception
	{
		Class<? extends TridentObjectType> clazz = object.getClass();
		Marshaller marshaller = jc.createMarshaller();
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(new JAXBElement(new QName(clazz.getSimpleName()), clazz, object), writer);
		return writer.toString();
	}


	protected String toString(U object)
	{

		String name = object.getClass().getPackage().getName();
		JAXBContext jc;
		try 
		{
			jc = JAXBContext.newInstance(name);
			String text = marshal(jc,object);
			return text;
		} 
		catch (Exception e) 
		{
			log.error("unable to marshal object "+name, e);
		}
		return "unable to marshal object "+name;

	}

	@SuppressWarnings("unchecked")
	protected T getOrAddSharedData(SharedImportedData sharedImportedData, T model, String sourceFile, U source, PhaseReportItem validationReport) 
	{
		prepareCheckPoint(validationReport,COMMON_1);
		SharedData data = (SharedData) sharedImportedData.get(model.getClass(),model.getObjectId());
		if (data == null)
		{
			data = new SharedData(model, sourceFile, toString(source), source.sourceLocation());
			sharedImportedData.add(model.getClass(), model.getObjectId(), data);
		}
		else
		{
			// 2-NEPTUNE-Common-1 : check if a shareable object is identical in each file 
			String sourceObject = toString(source);
			data.addOrigin(sourceFile, sourceObject, source.sourceLocation());
			if (data.isDuplicationError())
			{
				// error already set : add detail
				Locator srcLoc = source.sourceLocation() ;
				ReportLocation location = new ReportLocation(sourceFile, srcLoc.getLineNumber(), srcLoc.getColumnNumber());
				DetailReportItem detail = new DetailReportItem(COMMON_1,model.getObjectId(), Report.STATE.ERROR, location ,null);
				addValidationError(validationReport,COMMON_1, detail);
				
			}
			else
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
						Locator srcLoc = origin.getSourceLocation() ;
						ReportLocation location = new ReportLocation(origin.getSourceFile(), srcLoc.getLineNumber(), srcLoc.getColumnNumber());
						DetailReportItem detail = new DetailReportItem(COMMON_1, model.getObjectId(), Report.STATE.ERROR, location, null);
						addValidationError(validationReport,COMMON_1, detail);
					}
				}
			}
		}
		
		checkRegistrationNumber(sharedImportedData, model, sourceFile, source, validationReport);
		return (T) data.getObject();
	}

	private void checkRegistrationNumber(SharedImportedData sharedImportedData, T model, String sourceFile, U source, PhaseReportItem validationReport)
	{
		if (model.getRegistrationNumber() == null || model.getRegistrationNumber().isEmpty()) return;
		prepareCheckPoint(validationReport,COMMON_2);
		// 2-NEPTUNE-Common-2 : check if a shareable object has a unique registrationNumber 
		SharedData data = (SharedData) sharedImportedData.get(model.getClass(),model.getRegistrationNumber());
		if (data == null)
		{
			data = new SharedData(model, sourceFile, source.getObjectId(), source.sourceLocation());
			sharedImportedData.add(model.getClass(), model.getRegistrationNumber(), data);
		}
		else
		{
			String sourceObject = source.getObjectId();
			data.addOrigin(sourceFile, sourceObject , source.sourceLocation());
			if (data.isDuplicationError())
			{
				// error already set : add detail
				Locator srcLoc = source.sourceLocation() ;
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("RegistrationNumber", model.getRegistrationNumber());
				ReportLocation location = new ReportLocation(sourceFile, srcLoc.getLineNumber(), srcLoc.getColumnNumber());
				DetailReportItem detail = new DetailReportItem(COMMON_2, source.getObjectId(), Report.STATE.ERROR, location, map);
				addValidationError(validationReport,COMMON_2, detail);
				
			}
			else
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
						Locator srcLoc = origin.getSourceLocation() ;
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("RegistrationNumber", model.getRegistrationNumber());
						ReportLocation location = new ReportLocation(sourceFile, srcLoc.getLineNumber(), srcLoc.getColumnNumber());
						DetailReportItem detail = new DetailReportItem(COMMON_2, origin.getSourceData(), Report.STATE.ERROR, location, map);
						addValidationError(validationReport,COMMON_2, detail);
					}
				}
			}
		}

	}

	
	protected void addValidationError(PhaseReportItem validationReport,String checkPointKey,DetailReportItem item)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		checkPoint.addItem(item);
		
	}
	
	protected void prepareCheckPoint(PhaseReportItem validationReport,String checkPointKey)
	{
		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
		if (!checkPoint.hasItems()) checkPoint.updateStatus(Report.STATE.OK);
	}
}
