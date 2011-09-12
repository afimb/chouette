package fr.certu.chouette.struts.enumeration;

/**
 * Objet contenu dans toutes nos enumerations pour accéder aux propriétés de chaque élément énuméré (besoin struts)
 * @author luc
 *
 */
public class ObjetEnumere
{
	
	@SuppressWarnings("rawtypes")
	private Enum  enumeratedTypeAccess;
	private String textePropriete;
	
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public Enum getEnumeratedTypeAccess() 
	{
		return enumeratedTypeAccess;
	}

	@SuppressWarnings("rawtypes")
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
