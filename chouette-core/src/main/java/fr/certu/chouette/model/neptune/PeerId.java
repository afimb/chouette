package fr.certu.chouette.model.neptune;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class PeerId implements Serializable{

	private static final long serialVersionUID = -8800619354993403437L;
	
	@Getter @Setter private Long id;
	@Getter @Setter private String objectid;

	public PeerId() {}
	public PeerId(Long id, String objecid)
	{
		this.id=id;
		this.objectid=objecid;
	}
}
