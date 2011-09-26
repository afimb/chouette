package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.Registration;
import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

public abstract class AbstractModelProducer<T extends NeptuneIdentifiedObject, U extends TridentObjectTypeType> extends AbstractProducer implements IModelProducer<T, U>
{

	public void populateFromCastorNeptune(T target,U source ,ReportItem report)
	{
		// ObjectId : maybe null but not empty
		// TODO : Mandatory ?
		target.setObjectId(getNonEmptyTrimedString(source.getObjectId()));
		if (target.getObjectId() == null)
		{
			ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.MANDATORY_TAG,Report.STATE.ERROR,"ObjectId") ;
			report.addItem(item);
		}

		// ObjectVersion
		if (source.hasObjectVersion()) 
		{
			int castorObjectVersion = (int)source.getObjectVersion();
			target.setObjectVersion(castorObjectVersion);
		}

		// CreationTime : maybe null
		target.setCreationTime(source.getCreationTime());

		// CreatorId : maybe null but not empty
		target.setCreatorId(getNonEmptyTrimedString(source.getCreatorId()));

	}


	protected String getRegistrationNumber(Registration registration,ReportItem report) 
	{
		if (registration == null) return null;
		String number = registration.getRegistrationNumber();
		if (number == null || number.trim().length() == 0) 
		{
			ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.MANDATORY_TAG,Report.STATE.ERROR,"RegistrationNumber") ;
			report.addItem(item);
			return null;
		}
		return number.trim();
	}



}
