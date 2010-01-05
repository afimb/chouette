package fr.certu.chouette.service.database.impl.modele;

public class ReferenceArretPhysique 
{
	private Long id;
	private String nom;
	
	private ReferenceArretPhysique(){ super();};
	private ReferenceArretPhysique( String nom){ 
		super();
		this.nom = nom;
	};
	private ReferenceArretPhysique( Long id){ 
		super();
		this.id = id;
	};
	
	public static ReferenceArretPhysique creerRefenceNouvelle( String nom)
	{
		assert nom!=null;
		return new ReferenceArretPhysique( nom);
	}
	
	public static ReferenceArretPhysique creerRefenceExistante( Long id)
	{
		assert id!=null;
		return new ReferenceArretPhysique( id);
	}
	
	public boolean isNouveau()
	{
		return nom!=null;
	}

	public Long getId() {
		return id;
	}
	
	public String getNom() {
		return nom;
	}
}
