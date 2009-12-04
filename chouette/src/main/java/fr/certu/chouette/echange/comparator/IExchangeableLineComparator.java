package fr.certu.chouette.echange.comparator;

import java.io.File;
import java.util.List;
import fr.certu.chouette.echange.ILectureEchange;

public interface IExchangeableLineComparator {
	boolean doComparison(File source, File target) throws Exception;
	List<ChouetteObjectState> getObjectStateList();
	ILectureEchange getSource();
	ILectureEchange getTarget();
	void addMappingIds(String sourceObjectId, String targetObjectId);
	void addObjectState(ChouetteObjectState objectState);
	ExchangeableLineObjectIdMapper getSourceExchangeMap();
    ExchangeableLineObjectIdMapper getTargetExchangeMap();
    String getSourceId(String targetId);
    String getTargetId(String sourceId);
}
