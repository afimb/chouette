package fr.certu.chouette.service.amivif.util;

public class Paire 
{
	private String premier;
	private String second;
	
	public Paire(String premier, String second) {
		if ( premier==null) throw new IllegalArgumentException( "1er agrument non défini");
		if ( second==null) throw new IllegalArgumentException( "2ème agrument non défini");
		this.premier = premier;
		this.second = second;
	}

	public String getPremier() {
		return premier;
	}

	public String getSecond() {
		return second;
	}
	
	public String getNouvelId()
	{
		String[] tblRacine = premier.split( ":");
		String[] tblObjectId = second.split( ":");
		
		if ( tblRacine.length!=3) throw new IllegalArgumentException( "Identifiant "+premier+" non valide");
		if ( tblObjectId.length!=3) throw new IllegalArgumentException( "Identifiant "+second+" non valide");
		
		StringBuffer buf = new StringBuffer( tblObjectId[ 0]);
		buf.append( ":");
		buf.append( tblObjectId[ 1]);
		buf.append( ":");
		buf.append( tblRacine[ 2]);
		buf.append( "A");
		buf.append( tblObjectId[ 2]);
		
		return buf.toString();
	}

	@Override
	public int hashCode() {
		StringBuffer buffer = new StringBuffer( premier);
		buffer.append( "#");
		buffer.append( second);
		return buffer.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Paire other = (Paire) obj;
		if (premier == null) {
			if (other.premier != null)
				return false;
		} else if (!premier.equals(other.premier))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return premier+"|"+second;
	}
	
}
