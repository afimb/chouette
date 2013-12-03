package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.xml.sax.Locator;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class SharedData 
{
    @Getter private NeptuneIdentifiedObject object;
    @Getter private List<Origin> origins = new ArrayList<>();
    @Getter @Setter private boolean duplicationError = false; 
    
    public SharedData(NeptuneIdentifiedObject object,String sourceFile,String sourceData, Locator sourceLocation) {
		super();
		this.object = object;
		addOrigin(sourceFile, sourceData, sourceLocation);
		
	}

    public void addOrigin(String sourceFile,String sourceData, Locator sourceLocation)
    {
    	Origin origin = new Origin(sourceFile, sourceData, sourceLocation);
    	origins.add(origin);
    }

	public class Origin
    {
        @Getter private String sourceFile;
        @Getter private String sourceData;
        @Getter private Locator sourceLocation;
        
		public Origin(String sourceFile, String sourceData,
				Locator sourceLocation) 
		{
			this.sourceFile = sourceFile;
			this.sourceData = sourceData;
			this.sourceLocation = sourceLocation;
		}
        
    }
}

