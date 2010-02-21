package fr.certu.chouette.service.database.impl.modele;

public class EtatMajArretItineraire 
{
	private Long idArretLogique;
	private Long idArretPhysique;
	private int nouvellePosition;
	private String nom;
	private EnumMaj enumMaj;
	
	public static EtatMajArretItineraire creerSuppression( Long idLogique)
	{
		EtatMajArretItineraire instance = new EtatMajArretItineraire();
		instance.setEnumMaj( EnumMaj.SUPPRIMER);
		instance.setIdArretLogique( idLogique);
		
		return instance;
	}
	
	public static EtatMajArretItineraire creerCreation( int position, Long idPhysique)
	{
		EtatMajArretItineraire instance = new EtatMajArretItineraire();
		instance.setEnumMaj( EnumMaj.CREER);
		instance.setIdArretPhysique(idPhysique);
		instance.setNouvellePosition( position);
		
		return instance;
	}
	
	public static EtatMajArretItineraire creerCreation( int position, String nom)
	{
		EtatMajArretItineraire instance = new EtatMajArretItineraire();
		instance.setEnumMaj( EnumMaj.CREER);
		instance.setNom( nom);
		instance.setNouvellePosition( position);
		
		return instance;
	}
	
	public static EtatMajArretItineraire creerDeplace( int position, Long idLogique)
	{
		EtatMajArretItineraire instance = new EtatMajArretItineraire();
		instance.setEnumMaj( EnumMaj.DEPLACER);
		instance.setIdArretLogique( idLogique);
		instance.setNouvellePosition( position);
		
		return instance;
	}
	
	private void setEnumMaj(EnumMaj enumMaj) {
		this.enumMaj = enumMaj;
	}

	public EnumMaj getEnumMaj() {
		return enumMaj;
	}
	private void setIdArretLogique(Long idArretLogique) {
		this.idArretLogique = idArretLogique;
	}

	private void setIdArretPhysique(Long idArretPhysique) {
		this.idArretPhysique = idArretPhysique;
	}

	private void setNom(String nom) {
		this.nom = nom;
	}

	private void setNouvellePosition(int nouvellePosition) {
		this.nouvellePosition = nouvellePosition;
	}

	public Long getIdArretLogique() {
		return idArretLogique;
	}
	public Long getIdArretPhysique() {
		return idArretPhysique;
	}
	public String getNom() {
		return nom;
	}
	public int getNouvellePosition() {
		return nouvellePosition;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append( "idLogique:");
		buffer.append( idArretLogique);
		buffer.append( " ");
		buffer.append( "idPhysique:");
		buffer.append( idArretPhysique);
		buffer.append( " ");
		buffer.append( "nouvellePosition:");
		buffer.append( nouvellePosition);
		buffer.append( " ");
		buffer.append( "maj:");
		buffer.append( enumMaj);
		return buffer.toString();
	}
	
	
	
	

}
