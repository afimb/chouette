package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;

public class ReducteurTableauMarche 
{
	private final static String SEP = "$";
 	
	Map<String, String> idTMPrincipalParSignature;
	Map<String, String> signatureParIdTM;	
	Map<String, TableauMarche> tmParId;	
	Map<String, List<String>> idsTMParSignature;
	
	private ILectureEchange echange;

	public void reduire( ILectureEchange echange)
	{
		this.echange = echange;
		
		initialiser();
		
		transformer();
	}
	
	
	private void initialiser()
	{
		tmParId = new Hashtable<String, TableauMarche>();
		signatureParIdTM = new Hashtable<String, String>();
		idTMPrincipalParSignature = new Hashtable<String, String>();
		idsTMParSignature = new Hashtable<String, List<String>>();
		
		List<TableauMarche> tableaux = echange.getTableauxMarche();
		for (TableauMarche marche : tableaux) {
			String signature = getSignature( marche);
			signatureParIdTM.put( marche.getObjectId(), signature);
			tmParId.put( marche.getObjectId(), marche);
			
			List<String> idsTM = idsTMParSignature.get( signature);
			if ( idsTM==null)
			{
				idsTM = new ArrayList<String>();
				idsTMParSignature.put( signature, idsTM);
				idTMPrincipalParSignature.put( signature, marche.getObjectId());
			}
			idsTM.add( marche.getObjectId());
		}
	}
	
	private void transformer()
	{
		// parcours des tm
		List<TableauMarche> tableaux = echange.getTableauxMarche();
		List<TableauMarche> tableauxDeTrop = new ArrayList<TableauMarche>(); 
		for (TableauMarche marche : tableaux) {
			String signature = signatureParIdTM.get( marche.getObjectId());
			String idPrincipal = idTMPrincipalParSignature.get( signature);
			
			if ( !idPrincipal.equals( marche.getObjectId()))
			{
				TableauMarche principal = tmParId.get( idPrincipal);
				tableauxDeTrop.add( marche);
				
				// report des courses sur le TM principal
				int totalCourses = marche.getVehicleJourneyIdCount();
				for (int i = 0; i < totalCourses; i++) {
					principal.addVehicleJourneyId( marche.getVehicleJourneyId( i));
				}
			}
		}
		tableaux.removeAll( tableauxDeTrop);
	}

	
	private String getSignature( TableauMarche tableauMarche)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( tableauMarche.getComment());
		buffer.append( SEP);
		int totalPeriode = tableauMarche.getTotalPeriodes();
		for (int i = 0; i < totalPeriode; i++) {
			Periode periode = tableauMarche.getPeriodes().get( i);
			buffer.append( periode.getDebut());
			buffer.append( SEP);
			buffer.append( periode.getFin());
			buffer.append( SEP);
			buffer.append( tableauMarche.getIntDayTypes());
		}
		
		return buffer.toString();
	}
}
