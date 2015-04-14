package mobi.chouette.exchange.hub.model;

public abstract class HubObject {
	
	public static final int SENS_ALLER=1;
	public static final int SENS_RETOUR=2;
	
    public static final String TYPE_DEPART = "D";
    public static final String TYPE_ARRIVEE = "A";
    
    public static final int ITL_MONTEE = 1;
    public static final int ITL_DESCENTE = 2;

    
    public static final int lundi = 1;
    public static final int mardi = 2;
    public static final int mercredi = 4;
    public static final int jeudi = 8;
    public static final int vendredi = 16;
    public static final int samedi = 32;
    public static final int dimanche = 64;
    
    public abstract void clear();
    
	public enum MODE_TRANSPORT {
	AVION,
	BATEAU,
	BUS,
	BUS_PMR,
	CAR,
	CAR_PMR,
	FUNICULAIRE,
	METRO, 
	TAD, 
	TAD_PMR, 
	TAXIBUS, 
	TRAIN, 
	TRAM, 
	TROLLEY, 
	VELO
	};

}
