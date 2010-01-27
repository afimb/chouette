package fr.certu.chouette.struts.enumeration;

/**
 * Objet contenu dans toutes nos enumerations pour accéder aux propriétés de chaque élément énuméré (besoin struts)
 * @author luc
 *
 */
@SuppressWarnings("unchecked")
public class ObjetEnumere
{
	
	private Enum  enumeratedTypeAccess;
	private String textePropriete;
	
	public ObjetEnumere(Enum enumeratedTypeAccess, String textePropriete)
	//public ObjetEnumere(EnumeratedTypeAccess  enumeratedTypeAccess, String textePropriete) 
	{
		this.enumeratedTypeAccess = enumeratedTypeAccess;
		this.textePropriete = textePropriete;
	}
	
	public ObjetEnumere(String textePropriete) 
	{
		this.textePropriete = textePropriete;
	}

	public Enum getEnumeratedTypeAccess() 
	{
		return enumeratedTypeAccess;
	}

	public void setEnumeratedTypeAccess(Enum enumeratedTypeAccess) 
	{
		this.enumeratedTypeAccess = enumeratedTypeAccess;
	}

	public String getTextePropriete() 
	{
		return textePropriete;
	}

	public void setTextePropriete(String textePropriete) 
	{
		this.textePropriete = textePropriete;
	}

}
