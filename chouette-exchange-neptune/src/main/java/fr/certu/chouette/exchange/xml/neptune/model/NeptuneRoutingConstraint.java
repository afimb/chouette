package fr.certu.chouette.exchange.xml.neptune.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;

@NoArgsConstructor
public class NeptuneRoutingConstraint extends NeptuneIdentifiedObject
{

   private static final long serialVersionUID = -3352809559542729900L;

   @Getter @Setter private String lineId;
   @Getter @Setter private List<String> routingConstraintIds = new ArrayList<String>();

   public void addRoutingConstraintId(String routingConstraintId)
   {
      if (routingConstraintIds == null) routingConstraintIds = new ArrayList<String>();
      if (!routingConstraintIds.contains(routingConstraintId)) 
         routingConstraintIds.add(routingConstraintId);
   }

   public void removeRoutingConstraintId(String routingConstraintId)
   {
      if (routingConstraintIds == null) routingConstraintIds = new ArrayList<String>();
      if (routingConstraintIds.contains(routingConstraintId)) 
         routingConstraintIds.remove(routingConstraintId);
   }
   
	@Override
	public <T extends NeptuneObject> boolean compareAttributes(
			T anotherObject) {
		if (anotherObject instanceof NeptuneRoutingConstraint)
		{
			NeptuneRoutingConstraint another = (NeptuneRoutingConstraint) anotherObject;
			if (!sameValue(this.getObjectId(), another.getObjectId())) return false;
			if (!sameValue(this.getObjectVersion(), another.getObjectVersion())) return false;
			if (!sameValue(this.getName(), another.getName())) return false;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toURL() {
		return null;
	}

}
