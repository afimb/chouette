package fr.certu.chouette.service.validation.util;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.ChouetteAreaProducer;
import fr.certu.chouette.service.validation.util.ChouetteLineDescriptionProducer;
import fr.certu.chouette.service.validation.util.CompanyProducer;
import fr.certu.chouette.service.validation.util.Connecter;
import fr.certu.chouette.service.validation.util.ConnectionLinkProducer;
import fr.certu.chouette.service.validation.util.GroupOfLineProducer;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.validation.util.PTNetworkProducer;
import fr.certu.chouette.service.validation.util.TimeSlotProducer;
import fr.certu.chouette.service.validation.util.TimetableProducer;
import fr.certu.chouette.service.validation.ChouetteArea;
import fr.certu.chouette.service.validation.ChouetteLineDescription;
import fr.certu.chouette.service.validation.ChouettePTNetwork;
import fr.certu.chouette.service.validation.Company;
import fr.certu.chouette.service.validation.ConnectionLink;
import fr.certu.chouette.service.validation.GroupOfLine;
import fr.certu.chouette.service.validation.PTNetwork;
import fr.certu.chouette.service.validation.TimeSlot;
import fr.certu.chouette.service.validation.Timetable;
import java.util.HashSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MainSchemaProducer implements IMainSchemaProducer {
    
    private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.MainSchemaProducer.class);
    private              ValidationException validationException = new ValidationException();
    private              ChouettePTNetwork   chouettePTNetwork   = null;
    
    @Override
    public void setValidationException(ValidationException validationException) {
        this.validationException = validationException;
    }
    
    @Override
    public ValidationException getValidationException() {
        return validationException;
    }

    @Override
    public void setChouettePTNetwork(ChouettePTNetwork chouettePTNetwork) {
        this.chouettePTNetwork = chouettePTNetwork;
    }
    
    @Override
    public ChouettePTNetwork getChouettePTNetwork() {
        return chouettePTNetwork;
    }

    /**
     * Construit le Graphe Syntaxique Abstrait (ASG : Abstract Syntaxic Graph) de Chouette.
     * @param castorChouettePTNetwork
     * @return
     **/
    @Override
    public ChouettePTNetwork getASG(ChouettePTNetworkTypeType castorChouettePTNetwork)  {
        LoggingManager.setMessages(new HashSet<String>());
        String[] params = null;

        LoggingManager.log(logger, "DEBUT DE LA GENERATION DE L'ASG.", Level.DEBUG);
        if (castorChouettePTNetwork == null) {
            LoggingManager.log(logger, "L'objet de type \"ChouettePTNetwork\" est null.", Level.FATAL);
            validationException.add(TypeInvalidite.NULL_CHOUETTEPTNETWORK, "L'objet de type \"ChouettePTNetwork\" est null.");
            throw validationException;
        }

        chouettePTNetwork = new ChouettePTNetwork();
        
        // PTNetwork
        LoggingManager.log(logger, "\tCREATION DU PTNetwork.", Level.DEBUG);
        chouette.schema.PTNetwork castorPTNetwork = castorChouettePTNetwork.getPTNetwork();
        if (castorPTNetwork == null) {
            LoggingManager.log(logger, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"PTNetwork\" non null.", Level.ERROR);
            validationException.add(TypeInvalidite.NULL_PTNETWORK, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"PTNetwork\" non null.");
        }
        else {
            PTNetwork pTNetwork = (new PTNetworkProducer(validationException)).getASG(castorPTNetwork);
            if (pTNetwork == null) {
                params = null;
                if (castorPTNetwork.getObjectId() != null)
                    params = new String[]{castorPTNetwork.getObjectId()};
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"PTNetwork\" ().", params, Level.ERROR);
            }
            else {
                chouettePTNetwork.setPTNetwork(pTNetwork);
                pTNetwork.setChouettePTNetwork(chouettePTNetwork);
            }
        }
	
	// GroupOfLine 0..w
	LoggingManager.log(logger, "\tCREATION DU GroupOfLine.", Level.DEBUG);
	int numberOfGroupOfLines = castorChouettePTNetwork.getGroupOfLineCount();
	if (numberOfGroupOfLines <= 0)
	    LoggingManager.log(logger, "Il n'y a pas de \"GroupOfLine\" pour ce \"ChouettePTNetwork\".", Level.INFO);
	for (int i = 0; i < numberOfGroupOfLines; i++) {
	    chouette.schema.GroupOfLine castorGroupOfLine = castorChouettePTNetwork.getGroupOfLine(i);
	    if (castorGroupOfLine == null) {
		LoggingManager.log(logger, "Un objet de type \"GroupOfLine\" est null.", Level.WARN);
		validationException.add(TypeInvalidite.NULL_GROUPOFLINE, "Un objet de type \"GroupOfLine\" est null.");
		continue;
	    }
	    GroupOfLine groupOfLine = (new GroupOfLineProducer(validationException)).getASG(castorGroupOfLine);
	    if (groupOfLine == null) {
		params = null;
		if (groupOfLine.getObjectId() != null)
		    params = new String[]{castorPTNetwork.getObjectId()};
		LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"GroupOfLine\" ().", params, Level.ERROR);
	    }
	    else {
		chouettePTNetwork.addGroupOfLine(groupOfLine);
		groupOfLine.setChouettePTNetwork(chouettePTNetwork);				
	    }
	}
        
        // Company[0..w]
        LoggingManager.log(logger, "\tCREATION DES Company.", Level.DEBUG);
        int numberOfCompanies = castorChouettePTNetwork.getCompanyCount();
        if (numberOfCompanies <= 0)
            LoggingManager.log(logger, "Il n'y a pas de \"Company\" exploitante pour ce \"ChouettePTNetwork\".", Level.INFO);
        for (int i = 0; i < numberOfCompanies; i++) {
            chouette.schema.Company castorCompany = castorChouettePTNetwork.getCompany(i);
            if (castorCompany == null) {
                LoggingManager.log(logger, "Un objet de type \"Company\" est null.", Level.WARN);
                validationException.add(TypeInvalidite.NULL_COMPANY, "Un objet de type \"Company\" est null.");
                continue;
            }
            Company company = (new CompanyProducer(validationException)).getASG(castorCompany);
            if (company == null) {
                params = null;
                if (castorCompany.getObjectId() != null)
                    params = new String[]{castorPTNetwork.getObjectId()};
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"Company\" ().", params, Level.ERROR);
            }
            else {
                chouettePTNetwork.addCompany(company);
                company.setChouettePTNetwork(chouettePTNetwork);
            }
        }
        
        // ChouetteArea
        LoggingManager.log(logger, "\tCREATION DU ChouetteArea.", Level.DEBUG);
        chouette.schema.ChouetteArea castorChouetteArea = castorChouettePTNetwork.getChouetteArea();
        if (castorChouetteArea == null) {
            LoggingManager.log(logger, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"ChouetteArea\" non null.", Level.FATAL);
            validationException.add(TypeInvalidite.NULL_CHOUETTEAREA, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"ChouetteArea\" non null.");
            throw validationException;
        }
        else {
            ChouetteArea chouetteArea = (new ChouetteAreaProducer(validationException)).getASG(castorChouetteArea);
            if (chouetteArea == null)
                LoggingManager.log(logger, "Une erreure fatale s'est produite lors de la construction de l'objet \"ChouetteArea\".", Level.FATAL);
            else {
                chouettePTNetwork.setChouetteArea(chouetteArea);
                chouetteArea.setChouettePTNetwork(chouettePTNetwork);
            }
        }

        // ConnectionLink[0..w]
        LoggingManager.log(logger, "\tCREATION DES ConnectionLink.", Level.DEBUG);
        int numberOfConnectionLinks = castorChouettePTNetwork.getConnectionLinkCount();
        if (numberOfConnectionLinks <= 0)
            LoggingManager.log(logger, "Il n'y a pas de \"ConnectionLink\" pour ce \"ChouettePTNetwork\".", Level.INFO);
        for (int i = 0; i < numberOfConnectionLinks; i++) {
            chouette.schema.ConnectionLink castorConnectionLink = castorChouettePTNetwork.getConnectionLink(i);
            if (castorConnectionLink == null) {
                LoggingManager.log(logger, "Un objet de type \"ConnectionLink\" est null.", Level.WARN);
                validationException.add(TypeInvalidite.NULL_CONNECTIONLINK, "Un objet de type \"ConnectionLink\" est null.");
                continue;
            }
            ConnectionLink connectionLink = (new ConnectionLinkProducer(validationException)).getASG(castorConnectionLink);
            if (connectionLink == null) {
                params = null;
                if (castorConnectionLink.getObjectId() != null)
                    params = new String[] {castorConnectionLink.getObjectId()};
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"ConnectionLink\" ().", params, Level.ERROR);
            }
            else {
                chouettePTNetwork.addConnectionLink(connectionLink);
                connectionLink.setChouettePTNetwork(chouettePTNetwork);
            }
        }

        // Timetable[0..w]
        LoggingManager.log(logger, "\tCREATION DES Timetable.", Level.DEBUG);
        int numberOfTimetables = castorChouettePTNetwork.getTimetableCount();
        if (numberOfTimetables <= 0)
            LoggingManager.log(logger, "Il n'y a pas de \"Timetable\" pour ce \"ChouettePTNetwork\".", Level.INFO);
        for (int i = 0; i < numberOfTimetables; i++) {
            chouette.schema.Timetable castorTimetable = castorChouettePTNetwork.getTimetable(i);
            if (castorTimetable == null) {
                LoggingManager.log(logger, "Un objet de type \"Timetable\" est null.", Level.WARN);
                validationException.add(TypeInvalidite.NULL_TIMETABLE, "Un objet de type \"Timetable\" est null.");
                continue;
            }
            Timetable timetable = (new TimetableProducer(validationException)).getASG(castorTimetable);
            if (timetable == null) {
                params = null;
                if (castorTimetable.getObjectId() != null)
                    params = new String[] {castorTimetable.getObjectId()};
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"Timetable\" ().", params, Level.ERROR);
            }
            else {
                chouettePTNetwork.addTimetable(timetable);
                timetable.setChouettePTNetwork(chouettePTNetwork);
            }
        }

        // TimeSlot[0..w]
        LoggingManager.log(logger, "\tCREATION DES TimeSlot.", Level.DEBUG);
        int numberOfTimeSlots = castorChouettePTNetwork.getTimeSlotCount();
        if (numberOfTimeSlots <= 0)
            LoggingManager.log(logger, "Il n'y a pas de \"TimeSlot\" pour ce \"ChouettePTNetwork\".", Level.INFO);
        for (int i = 0; i < numberOfTimeSlots; i++) {
            chouette.schema.TimeSlot castorTimeSlot = castorChouettePTNetwork.getTimeSlot(i);
            if (castorTimeSlot == null) {
                LoggingManager.log(logger, "Un objet de type \"TimeSlot\" est null.", Level.WARN);
                validationException.add(TypeInvalidite.NULL_TIMESLOT, "Un objet de type \"TimeSlot\" est null.");
                continue;
            }
            TimeSlot timeSlot = (new TimeSlotProducer(validationException)).getASG(castorTimeSlot);
            if (timeSlot == null) {
                params = null;
                if (castorTimeSlot.getObjectId() != null)
                    params = new String[] {castorTimeSlot.getObjectId()};
                LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"TimeSlot\" ().", params, Level.ERROR);
            }
            else {
                chouettePTNetwork.addTimeSlot(timeSlot);
                timeSlot.setChouettePTNetwork(chouettePTNetwork);
            }
        }

        // ChouetteLineDescription
        LoggingManager.log(logger, "\tCREATION DU ChouetteLineDescription", Level.DEBUG);
        chouette.schema.ChouetteLineDescription castorChouetteLineDescription = castorChouettePTNetwork.getChouetteLineDescription();
        if (castorChouetteLineDescription == null) {
            LoggingManager.log(logger, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"ChouetteLineDescription\" non null.", Level.FATAL);
            validationException.add(TypeInvalidite.NULL_CHOUETTELINEDESCRIPTION, "Un objet de type \"ChouettePTNetwork\" doit absolument contenir un objet \"ChouetteLineDescription\" non null.");
            throw validationException;
        }
        else {
            ChouetteLineDescription chouetteLineDescription = (new ChouetteLineDescriptionProducer(validationException)).getASG(castorChouetteLineDescription);
            if (chouetteLineDescription == null)
                LoggingManager.log(logger, "Une erreure fatale s'est produite lors de la construction de l'objet \"ChouetteLineDescription\".", Level.FATAL);
            else {
                chouettePTNetwork.setChouetteLineDescription(chouetteLineDescription);
                chouetteLineDescription.setChouettePTNetwork(chouettePTNetwork);
            }
        }

        // Connexion des objets
        LoggingManager.log(logger, "\tCONNEXION DES ELEMENTS.", Level.DEBUG);
        (new Connecter(validationException)).connectElements(chouettePTNetwork);
        
        LoggingManager.log(logger, "FIN DE LA GENERATION DE L'ASG.", Level.DEBUG);
        if (validationException.getCategories() != null)
            if (validationException.getCategories().size() > 0)
                throw validationException;
        
        return chouettePTNetwork;
    }
    
    public static boolean isTridentLike(String objectId) {
        String[] subNames = objectId.split(":");
        if (subNames == null)
            return false;
        if (subNames.length != 3)
            return false;
        for (int i = 0; i < subNames.length; i++)
            if ((subNames[i] == null) || (subNames[i].trim().length() == 0))
                return false;
        //(\p{L}|_)+:\p{L}+:[0-9A-Za-z]+ remplaced by (\p{L}|_)+:\p{L}+:[0-9A-Za-z-]+
        return true;
    }
}
