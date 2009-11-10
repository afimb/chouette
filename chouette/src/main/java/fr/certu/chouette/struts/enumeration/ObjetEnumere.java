package fr.certu.chouette.struts.enumeration;

import org.exolab.castor.types.EnumeratedTypeAccess;
/**
 * Objet contenu dans toutes nos enumerations pour accéder aux propriétés de chaque élément énuméré (besoin struts)
 * @author luc
 *
 */
public class ObjetEnumere
{
	private EnumeratedTypeAccess  enumeratedTypeAccess;
	private String textePropriete;
	
	public ObjetEnumere(EnumeratedTypeAccess  enumeratedTypeAccess, String textePropriete) 
	{
		this.enumeratedTypeAccess = enumeratedTypeAccess;
		this.textePropriete = textePropriete;
	}
	
	public ObjetEnumere(String textePropriete) 
	{
		this.textePropriete = textePropriete;
	}

	public EnumeratedTypeAccess getEnumeratedTypeAccess() {
		return enumeratedTypeAccess;
	}

	public void setEnumeratedTypeAccess(EnumeratedTypeAccess enumeratedTypeAccess) {
		this.enumeratedTypeAccess = enumeratedTypeAccess;
	}

	public String getTextePropriete() {
		return textePropriete;
	}

	public void setTextePropriete(String textePropriete) {
		this.textePropriete = textePropriete;
	}
	
	
}
