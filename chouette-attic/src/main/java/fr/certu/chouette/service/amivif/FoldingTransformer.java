package fr.certu.chouette.service.amivif;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import amivif.schema.PTLink;
import amivif.schema.RespPTLineStructTimetableTypeType;
import amivif.schema.Route;
import fr.certu.chouette.service.identification.ObjectIdLecteur;

public class FoldingTransformer 
{
    private static final Logger logger = Logger.getLogger( FoldingTransformer.class);

    private Map<String, String> principalIdParId = new Hashtable<String, String>();
    private Map<String, PTLink> ptLinkParPrincipalId = new Hashtable<String, PTLink>();
    
    private RespPTLineStructTimetableTypeType lineStrut;
    private static final String SEP = "A";
    
    private void initialise()
    {
    	principalIdParId.clear();
    	ptLinkParPrincipalId.clear();
    	
    	int total = lineStrut.getPTLinkCount();
    	for (int i = 0; i < total; i++) {
			PTLink ptLink = lineStrut.getPTLink( i);
			String principalId = getNouvelId( ptLink);
			principalIdParId.put( ptLink.getObjectId(), principalId);
			ptLinkParPrincipalId.put( principalId, ptLink);
		}
    }
    
	public void transform()
	{
		initialise();
		transformPtLink();
		transformRoute();
	}
    
	private void transformRoute()
	{
		int total = lineStrut.getRouteCount();
		for (int i = 0; i < total; i++) {
			Route route = lineStrut.getRoute( i);
			int totalLinks = route.getPtLinkIdCount();
			List<String> ptLinkIds = new ArrayList<String>( totalLinks);
			for (int j = 0; j < totalLinks; j++) {
				ptLinkIds.add( principalIdParId.get( route.getPtLinkId( j)));
			}
			route.setPtLinkId( ptLinkIds.toArray( new String[ 0]));
		}
	}
    
	private void transformPtLink()
	{
		List<PTLink> ptLinks = new ArrayList<PTLink>();
		for (String principalId : ptLinkParPrincipalId.keySet()) {
			PTLink ptLink = ptLinkParPrincipalId.get( principalId);
			ptLink.setObjectId( principalId);
			ptLinks.add( ptLink);
		}
		lineStrut.setPTLink( ptLinks.toArray( new PTLink[ 0]));
	}
    
    private String getNouvelId( PTLink ptLink)
    {
    	String objectId = ptLink.getObjectId();
    	StringBuffer buffer = new StringBuffer( ObjectIdLecteur.lirePartieSysteme( objectId));
    	buffer.append( ":");
    	buffer.append( ObjectIdLecteur.lireTypeDonnee( objectId));
    	buffer.append( ":");
    	buffer.append( ObjectIdLecteur.lirePartieCode( ptLink.getStartOfLink()));
    	buffer.append( SEP);
    	buffer.append( ObjectIdLecteur.lirePartieCode( ptLink.getEndOfLink()));
    	return buffer.toString();
    }

	public void setLineStrut(RespPTLineStructTimetableTypeType lineStrut) 
	{
		this.lineStrut = lineStrut;
	}
}
