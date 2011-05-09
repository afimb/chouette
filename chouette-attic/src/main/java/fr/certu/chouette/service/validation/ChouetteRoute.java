package fr.certu.chouette.service.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChouetteRoute {
	
	private ChouetteLineDescription 	chouetteLineDescription;
	private String 						comment;
	private Date 						creationTime;
	private String 						creatorId;
	private PTDirectionType 			pTDirectionType;
	private String 						name;
	private String 						number;
	private String 						objectId;
	private boolean 					hasObjectVersion		= false;
	private int 						objectVersion;
	private String 						publishedName;
	private RouteExtension 				routeExtension;
	private String[] 					journeyPatternIds;
	private List<JourneyPattern> 		journeyPatterns 		= new ArrayList<JourneyPattern>();
	private String[] 					ptLinkIds;
	private List<PtLink> 				ptLinks 				= new ArrayList<PtLink>();
	private String 						wayBackRouteId;
	private ChouetteRoute 				wayBackRoute;
	private ChouetteRoute 				isWayBackRouteOf;
	
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
	
	public void setPTDirectionType(PTDirectionType pTDirectionType) {
		this.pTDirectionType = pTDirectionType;
	}
	
	public PTDirectionType getPTDirectionType() {
		return pTDirectionType;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return number;
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
	
	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}
	
	public String getPublishedName() {
		return publishedName;
	}
	
	public void setRouteExtension(RouteExtension routeExtension) {
		this.routeExtension = routeExtension;
	}
	
	public RouteExtension getRouteExtension() {
		return routeExtension;
	}
	
	public void setJourneyPatternIds(String[] journeyPatternIds) {
		this.journeyPatternIds = journeyPatternIds;
	}
	
	public String[] getJourneyPatternIds() {
		return journeyPatternIds;
	}
	
	public void addJourneyPattern(JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
		journeyPatterns.add(journeyPattern);
	}
	
	public void addJourneyPattern(int index, JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
		journeyPatterns.add(index, journeyPattern);
	}
	
	public void removeJourneyPattern(int index) throws IndexOutOfBoundsException {
		journeyPatterns.remove(index);
	}
	
	public void removeJourneyPattern(JourneyPattern journeyPattern) {
		journeyPatterns.remove(journeyPattern);
	}
	
	public void clearJourneyPatterns() {
		journeyPatterns.clear();
	}
	
	public void setJourneyPatterns(List<JourneyPattern> journeyPatterns) {
		this.journeyPatterns = journeyPatterns;
	}
	
	public List<JourneyPattern> getJourneyPatterns() {
		return journeyPatterns;
	}
	
	public JourneyPattern[] getJourneyPatternAsTable() {
		int size = journeyPatterns.size();
		JourneyPattern[] mArray = new JourneyPattern[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (JourneyPattern)journeyPatterns.get(index);
		return mArray;
	}
	
	public JourneyPattern getJourneyPattern(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > journeyPatterns.size()))
            throw new IndexOutOfBoundsException();
		return (JourneyPattern) journeyPatterns.get(index);
	}
	
	public int getJourneyPatternCount() {
        return journeyPatterns.size();
    }
	
	public void setJourneyPatterns(ArrayList<JourneyPattern> journeyPatterns) {
        this.journeyPatterns = journeyPatterns;
    }
	
    public void setJourneyPatterns(JourneyPattern[] arrayOfJourneyPatterns) {
    	journeyPatterns.clear();
        for (int i = 0; i < arrayOfJourneyPatterns.length; i++)
            journeyPatterns.add(arrayOfJourneyPatterns[i]);
    }
    
    public void setJourneyPattern(int index, JourneyPattern journeyPattern) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > journeyPatterns.size()))
    		throw new IndexOutOfBoundsException();
    	journeyPatterns.set(index, journeyPattern);
    }
	
	public void setPtLinkIds(String[] ptLinkIds) {
		this.ptLinkIds = ptLinkIds;
	}
	
	public String[] getPtLinkIds() {
		return ptLinkIds;
	}
	
	public void addPtLink(PtLink ptLink) throws IndexOutOfBoundsException {
		ptLinks.add(ptLink);
	}
	
	public void addPtLink(int index, PtLink ptLink) throws IndexOutOfBoundsException {
		ptLinks.add(index, ptLink);
	}
	
	public void removePtLink(int index) throws IndexOutOfBoundsException {
		ptLinks.remove(index);
	}
	
	public void removePtLink(PtLink ptLink) {
		ptLinks.remove(ptLink);
	}
	
	public void clearPtLinks() {
		ptLinks.clear();
	}
	
	public void setPtLinks(List<PtLink> ptLinks) {
		this.ptLinks = ptLinks;
	}
	
	public List<PtLink> getPtLinks() {
		return ptLinks;
	}
	
	public PtLink[] getPtLinkAsTable() {
		int size = ptLinks.size();
		PtLink[] mArray = new PtLink[size];
		for (int index = 0; index < size; index++)
			mArray[index] = (PtLink)ptLinks.get(index);
		return mArray;
	}
	
	public PtLink getPtLink(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > ptLinks.size()))
            throw new IndexOutOfBoundsException();
		return (PtLink) ptLinks.get(index);
	}
	
	public int getPtLinkCount() {
        return ptLinks.size();
    }
	
	public void setPtLinks(ArrayList<PtLink> ptLinks) {
        this.ptLinks = ptLinks;
    }
	
    public void setPtLinks(PtLink[] arrayOfPtLinks) {
    	ptLinks.clear();
        for (int i = 0; i < arrayOfPtLinks.length; i++)
            ptLinks.add(arrayOfPtLinks[i]);
    }
    
    public void setPtLink(int index, PtLink ptLink) throws IndexOutOfBoundsException {
    	if ((index < 0) || (index > ptLinks.size()))
    		throw new IndexOutOfBoundsException();
    	ptLinks.set(index, ptLink);
    }
	
	public void setWayBackRouteId(String wayBackRouteId) {
		this.wayBackRouteId = wayBackRouteId;
	}
	
	public String getWayBackRouteId() {
		return wayBackRouteId;
	}
	
	public void setWayBackRoute(ChouetteRoute wayBackRoute) {
		this.wayBackRoute = wayBackRoute;
	}
	
	public ChouetteRoute getWayBackRoute() {
		return wayBackRoute;
	}
	
	public void setIsWayBackRouteOf(ChouetteRoute isWayBackRouteOf) {
		this.isWayBackRouteOf = isWayBackRouteOf;
	}
	
	public ChouetteRoute getIsWayBackRouteOf() {
		return isWayBackRouteOf;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<ChouetteRoute>\n");
		if (routeExtension != null)
			stb.append(routeExtension.toString());
		if (name != null)
			stb.append("<Name>"+name+"</Name>\n");
		if (publishedName != null)
			stb.append("<PublishedName>"+publishedName+"</PublishedName>\n");
		if (number != null)
			stb.append("<Number>"+number+"</Number>\n");
		if (pTDirectionType != null)
			stb.append("<Direction>"+pTDirectionType.toString()+"</Direction>\n");
		for (int i = 0; i < ptLinkIds.length; i++)
			stb.append("<PtLink>"+ptLinkIds[i]+"</PtLink>\n");
		for (int i = 0; i < journeyPatternIds.length; i++)
			stb.append("<JourneyPattern>"+journeyPatternIds[i]+"</JourneyPattern>\n");
		if (wayBackRouteId != null)
			stb.append("<WayBackRoute>"+wayBackRouteId+"</WayBackRoute>\n");
		if (comment != null)
			stb.append("<Comment>"+comment+"</Comment>\n");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion)
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		if (creationTime != null)
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		if (creatorId != null)
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		stb.append("</ChouetteRoute>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ChouetteRoute>\n");
		if (routeExtension != null)
			stb.append(routeExtension.toString(indent+1, indentSize));
		if (name != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Name>"+name+"</Name>\n");
		}
		if (publishedName != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<PublishedName>"+publishedName+"</PublishedName>\n");
		}
		if (number != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Number>"+number+"</Number>\n");
		}
		if (pTDirectionType != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Direction>"+pTDirectionType.toString()+"</Direction>\n");
		}
		for (int i = 0; i < ptLinkIds.length; i++) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<PtLink>"+ptLinkIds[i]+"</PtLink>\n");
		}
		for (int i = 0; i < journeyPatternIds.length; i++) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<JourneyPattern>"+journeyPatternIds[i]+"</JourneyPattern>\n");
		}
		if (wayBackRouteId != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<WayBackRoute>"+wayBackRouteId+"</WayBackRoute>\n");
		}
		if (comment != null) {
			for (int j = 0; j < indent+1; j++)
				for (int k = 0; k < indentSize; k++)
					stb.append(" ");
			stb.append("<Comment>"+comment+"</Comment>\n");			
		}
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ObjectId>"+objectId+"</ObjectId>\n");
		if (hasObjectVersion) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<ObjectVersion>"+objectVersion+"</ObjectVersion>\n");		
		}
		if (creationTime != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreationTime>"+creationTime.toString()+"</CreationTime>\n");
		}
		if (creatorId != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<CreatorId>"+creatorId.toString()+"</CreatorId>\n");		
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</ChouetteRoute>\n");
		return stb.toString();
	}
}
