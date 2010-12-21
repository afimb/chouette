package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractChouetteDataComparator
  implements IChouetteDataComparator
{
  protected IExchangeableLineComparator master;
  private String mappingKey;
  private boolean stopOnFailure = false;
  private boolean verbose = false;

  protected List<String> convertToSourceId(List<String> targetList)
  {
    List<String> sourceList = new ArrayList<String>(targetList.size());
    for (String targetId : targetList)
    {
      String sourceId = this.master.getSourceId(targetId);
      if (sourceId == null) sourceId = "no match";
      sourceList.add(sourceId);
    }
    return sourceList;
  }

  public String getMappingKey()
  {
    return this.mappingKey;
  }

  public void setMappingKey(String mappingKey)
  {
    this.mappingKey = mappingKey;
  }

  public Map<String, ChouetteObjectState> getStateMap()
  {
    return null;
  }

  public void setStopOnFailure(boolean flag)
  {
    this.stopOnFailure = flag;
  }

  public boolean mustStopOnFailure()
  {
    return this.stopOnFailure;
  }

  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }

  public boolean isVerbose()
  {
    return this.verbose;
  }
}