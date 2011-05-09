package fr.certu.chouette.service.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PtLink {
	
	private ChouetteLineDescription chouetteLineDescription;
	private String comment;
	private Date creationTime;
	private String creatorId;
	private BigDecimal linkDistance;
	private String name;
	private String objectId;
	private boolean hasObjectVersion = false;
	private int objectVersion;
	private List<ChouetteRoute> chouetteRoutes = new ArrayList<ChouetteRoute>();
	private String startOfLinkId;
	private StopPoint startOfLink;
	private String endOfLinkId;
	private StopPoint endOfLink;
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public void setLinkDistance(BigDecimal linkDistance) {
		this.linkDistance = linkDistance;
	}
	
	public BigDecimal getLinkDistance() {
		return linkDistance;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectVersion(int objectVersion) {
		if (objectVersion >= 1) {
			hasObjectVersion = true;
		this.objectVersion = objectVersion;
                }
                else {
			hasObjectVersion = false;
		this.objectVersion = 1;
                }
	}
	
	public int getObjectVersion() {
		return objectVersion;
	}
	
	public boolean hasObjectVersion() {
		return hasObjectVersion;
	}

	public void addChouetteRoute(ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
		chouetteRoutes.add(chouetteRoute);
	}
	
	public void addChouetteRoute(int index, ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
		chouetteRoutes.add(index, chouetteRoute);
	}
	
	public void removeChouetteRoute(int index) throws IndexOutOfBoundsException {
		chouetteRoutes.remove(index);
	}
	
	public void removeChouetteRoute(ChouetteRoute chouetteRoute) {
		chouetteRoutes.remove(chouetteRoute);
	}
	
	public void clearChouetteRoutes() {
		chouetteRoutes.clear();
	}
	
	public void setChouetteRoutes(List<ChouetteRoute> chouetteRoutes) {
		this.chouetteRoutes = chouetteRoutes;
	}
	
	public List<ChouetteRoute> getChouetteRoutes() {
		return chouetteRoutes;
	}
	
	public ChouetteRoute[] getChouetteRouteAsTable() {
		int size = chouetteRoutes.size();
		ChouetteRoute[] mArray = new ChouetteRoute[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (ChouetteRoute)chouetteRoutes.get(index);
		return mArray;
	}
	
	public ChouetteRoute getChouetteRoute(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > chouetteRoutes.size()))
            throw new IndexOutOfBoundsException();
		return (ChouetteRoute) chouetteRoutes.get(index);
	}
	
	public int getChouetteRouteCount() {
        return chouetteRoutes.size();
    }
	
	public void setChouetteRoutes(ArrayList<ChouetteRoute> chouetteRoutes) {
        this.chouetteRoutes = chouetteRoutes;
    }
	
    public void setChouetteRoutes(ChouetteRoute[] arrayOfChouetteRoutes) {
    	chouetteRoutes.clear();
        for (int i = 0; i < arrayOfChouetteRoutes.length; i++)
            chouetteRoutes.add(arrayOfChouetteRoutes[i]);
    }
    
    public void setChouetteRoute(int index, ChouetteRoute chouetteRoute) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > chouetteRoutes.size()))
    		throw new IndexOutOfBoundsException();
    	chouetteRoutes.set(index, chouetteRoute);
    }
	
	public void setStartOfLinkId(String startOfLinkId) {
		this.startOfLinkId = startOfLinkId;
	}
	
	public String getStartOfLinkId() {
		return startOfLinkId;
	}
	
	public void setStartOfLink(StopPoint startOfLink) {
		this.startOfLink = startOfLink;
	}
	
	public StopPoint getStartOfLink() {
		return startOfLink;
	}
	
	public void setEndOfLinkId(String endOfLinkId) {
		this.endOfLinkId = endOfLinkId;
	}
	
	public String getEndOfLinkId() {
		return endOfLinkId;
	}
	
	public void setEndOfLink(StopPoint endOfLink) {
		this.endOfLink = endOfLink;
	}
	
	public StopPoint getEndOfLink() {
		return endOfLink;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		
		stb.append("xxx\n");
		stb.append("yyy\n");
		//..............
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		return stb.toString();
	}
}
