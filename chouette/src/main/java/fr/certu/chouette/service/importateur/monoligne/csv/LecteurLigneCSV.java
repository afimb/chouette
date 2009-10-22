package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.Line;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurLigneCSV {
	
	private IIdentificationManager identificationManager;
	private String cleNom;
	private String cleNomPublic;
	private String cleNumero;
	private String cleCommentaire;
	private String cleMode;


	public Ligne lire( Map<String, String> contenu) {
		Ligne ligne = new Ligne();
		ligne.setName( contenu.get( cleNom));
		ligne.setPublishedName( contenu.get( cleNomPublic));
		ligne.setNumber( contenu.get( cleNumero));
		ligne.setComment( contenu.get( cleCommentaire));
		ligne.setCreationTime( new Date());
		String mode = contenu.get( cleMode);
		if (mode != null) {
			if ( TransportModeNameType.BUS.equals( mode))
				ligne.setTransportModeName( TransportModeNameType.BUS);
			else if ( TransportModeNameType.METRO.equals( mode))
				ligne.setTransportModeName( TransportModeNameType.METRO);
			else if ( TransportModeNameType.TRAMWAY.equals( mode))
				ligne.setTransportModeName( TransportModeNameType.TRAMWAY);
			else if ( TransportModeNameType.TRAIN.equals( mode))
				ligne.setTransportModeName( TransportModeNameType.TRAIN);
			else if ( mode.equals( "RER"))
				ligne.setTransportModeName( TransportModeNameType.LOCALTRAIN);
		}
		ligne.setObjectId( identificationManager.getIdFonctionnel( "Line", String.valueOf( 1)));
		ligne.setObjectVersion(1);
		return ligne;
	}
	
	public List<String[]> ecrire(Line ligne,int length, int colonneTitrePartieFixe) {
		List<String[]> resultat = new ArrayList<String[]>();
		for (int i = 0; i < 5; i++) {
			String[] line = new String[length];
			if (ligne != null)
				switch(i) {
				case 0:
					line[colonneTitrePartieFixe] = cleNom;
					if (ligne.getName() != null)
						line[colonneTitrePartieFixe+1] = ligne.getName();
					break;
				case 1:
					line[colonneTitrePartieFixe] = cleNomPublic;
					if (ligne.getPublishedName() != null)
						line[colonneTitrePartieFixe+1] = ligne.getPublishedName();
					break;
				case 2:
					line[colonneTitrePartieFixe] = cleNumero;
					if (ligne.getNumber() != null)
						line[colonneTitrePartieFixe+1] = ligne.getNumber();
					break;
				case 3:
					line[colonneTitrePartieFixe] = cleCommentaire;
					if (ligne.getComment() != null)
						line[colonneTitrePartieFixe+1] = ligne.getComment();
					break;
				case 4:
					line[colonneTitrePartieFixe] = cleMode;
					if (ligne.getTransportModeName() != null)
						switch (ligne.getTransportModeName().getType()) {
						case TransportModeNameType.AIR_TYPE:
							line[colonneTitrePartieFixe+1] = "AIR";
							break;
						case TransportModeNameType.BICYCLE_TYPE:
							line[colonneTitrePartieFixe+1] = "BICYCLE";
							break;
						case TransportModeNameType.BUS_TYPE:
							line[colonneTitrePartieFixe+1] = "BUS";
							break;
						case TransportModeNameType.COACH_TYPE:
							line[colonneTitrePartieFixe+1] = "COACH";
							break;
						case TransportModeNameType.FERRY_TYPE:
							line[colonneTitrePartieFixe+1] = "FERRY";
							break;
						case TransportModeNameType.LOCALTRAIN_TYPE:
							line[colonneTitrePartieFixe+1] = "RER";
							break;
						case TransportModeNameType.LONGDISTANCETRAIN_TYPE:
							line[colonneTitrePartieFixe+1] = "LONGDISTANCETRAIN";
							break;
						case TransportModeNameType.METRO_TYPE:
							line[colonneTitrePartieFixe+1] = "METRO";
							break;
						case TransportModeNameType.OTHER_TYPE:
							line[colonneTitrePartieFixe+1] = "OTHER";
							break;
						case TransportModeNameType.PRIVATEVEHICLE_TYPE:
							line[colonneTitrePartieFixe+1] = "PRIVATEVEHICLE";
							break;
						case TransportModeNameType.RAPIDTRANSIT_TYPE:
							line[colonneTitrePartieFixe+1] = "RAPIDTRANSIT";
							break;
						case TransportModeNameType.SHUTTLE_TYPE:
							line[colonneTitrePartieFixe+1] = "SHUTTLE";
							break;
						case TransportModeNameType.TAXI_TYPE:
							line[colonneTitrePartieFixe+1] = "TAXI";
							break;
						case TransportModeNameType.TRAIN_TYPE:
							line[colonneTitrePartieFixe+1] = "TRAIN";
							break;
						case TransportModeNameType.TRAMWAY_TYPE:
							line[colonneTitrePartieFixe+1] = "TRAMWAY";
							break;
						case TransportModeNameType.TROLLEYBUS_TYPE:
							line[colonneTitrePartieFixe+1] = "TROLLEYBUS";
							break;
						case TransportModeNameType.VAL_TYPE:
							line[colonneTitrePartieFixe+1] = "VAL";
							break;
						case TransportModeNameType.WALK_TYPE:
							line[colonneTitrePartieFixe+1] = "WALK";
							break;
						case TransportModeNameType.WATERBORNE_TYPE:
							line[colonneTitrePartieFixe+1] = "WATERBORNE";
							break;
						}
					break;
				}					
			resultat.add(line);
		}
		return resultat;
	}
	
	public Set<String> getCles() {
		Set<String> cles = new HashSet<String>();
		cles.add( cleNom);
		cles.add( cleNomPublic);
		cles.add( cleNumero);
		cles.add( cleCommentaire);
		cles.add( cleMode);
		return cles;
	}

	public void setCleCommentaire(String cleCommentaire) {
		this.cleCommentaire = cleCommentaire;
	}

	public void setCleMode(String cleMode) {
		this.cleMode = cleMode;
	}

	public void setCleNomPublic(String cleNomPublic) {
		this.cleNomPublic = cleNomPublic;
	}

	public void setCleNumero(String cleNumero) {
		this.cleNumero = cleNumero;
	}

	public void setCleNom(String cleNom) {
		this.cleNom = cleNom;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
