package fr.certu.chouette.struts.json.data;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

@SuppressWarnings("serial")
public class JSONStopArea extends NeptuneIdentifiedObject 
{
   private static final Logger logger = Logger.getLogger(JSONStopArea.class);
	@Getter @Setter private AreaCentroid areaCentroid;
	@Getter @Setter private String comment;
	@Getter @Setter private ChouetteAreaEnum areaType;
	@Getter @Setter private Integer fareCode;
	@Getter @Setter private Boolean liftAvailable;
	@Getter @Setter private Boolean mobilityRestrictedSuitable;
	@Getter @Setter private Boolean stairsAvailable;
	@Getter @Setter private String nearestTopicName;
	@Getter @Setter private String registrationNumber;


	public JSONStopArea(StopArea area) 
	{
		try
      {
         BeanUtils.copyProperties(this, area);
         areaCentroid.setContainedInStopArea(null);
      }
      catch (Exception e)
      {
         logger.error("copy failed",e);
      }
	}
}
