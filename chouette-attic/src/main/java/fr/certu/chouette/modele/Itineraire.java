package fr.certu.chouette.modele;

import java.util.Date;

import chouette.schema.ChouetteRoute;
import chouette.schema.RouteExtension;
import chouette.schema.types.PTDirectionType;

public class Itineraire extends BaseObjet {

    private ChouetteRoute route;
    private RouteExtension routeExtension;
    private Long idRetour;
    private Long idLigne;
    
    public Itineraire() {
        super();
        
        route = new ChouetteRoute();
        route.setRouteExtension(new RouteExtension());
        route.getRouteExtension().setWayBack("A");
    }
    
    public Long getIdRetour() {
        return idRetour;
    }
    
    public void setIdRetour(Long idRetour) {
        this.idRetour = idRetour;
    }
    
    public ChouetteRoute getChouetteRoute() {
        return route;
    }
    
    public void setChouetteRoute(final ChouetteRoute route) {
        if (this.route == null) {
            this.route = new ChouetteRoute();
            this.route.setRouteExtension(new RouteExtension());
        } else {
            this.route = route;
            if (route.getRouteExtension() == null) {
                this.route.setRouteExtension(new RouteExtension());
            }
        }
        if (this.route.getRouteExtension().getWayBack() == null) {
            this.route.getRouteExtension().setWayBack("A");
        }
    }
    
    public String getWayBack() {
        return this.route.getRouteExtension().getWayBack();
    }
    
    public void setWayBack(String wayBack) {
        this.route.getRouteExtension().setWayBack(wayBack);
    }
    
    public Long getIdLigne() {
        return idLigne;
    }
    
    public void setIdLigne(final Long idLigne) {
        this.idLigne = idLigne;
    }
    
    public String getComment() {
        return route.getComment();
    }
    
    public Date getCreationTime() {
        return route.getCreationTime();
    }
    
    public String getCreatorId() {
        return route.getCreatorId();
    }
    
    public PTDirectionType getDirection() {
        return route.getDirection();
    }
    
    public String getName() {
        return route.getName();
    }
    
    public String getNumber() {
        return route.getNumber();
    }
    
    public String getObjectId() {
        return route.getObjectId();
    }
    
    public int getObjectVersion() {
        setObjectVersion((int) route.getObjectVersion());
        return (int) route.getObjectVersion();
    }
    
    public String getPublishedName() {
        return route.getPublishedName();
    }
    
    public void setComment(String comment) {
        route.setComment(comment);
    }
    
    public void setCreationTime(Date creationTime) {
        route.setCreationTime(creationTime);
    }
    
    public void setCreatorId(String creatorId) {
        route.setCreatorId(creatorId);
    }
    
    public void setDirection(PTDirectionType direction) {
        route.setDirection(direction);
    }
    
    public void setName(String name) {
        route.setName(name);
    }
    
    public void setNumber(String number) {
        route.setNumber(number);
    }
    
    public void setObjectId(String objectId) {
        route.setObjectId(objectId);
    }
    
    public void setObjectVersion(int objectVersion) {
        if (objectVersion >= 1) {
            route.setObjectVersion(objectVersion);
        } else {
            route.setObjectVersion(1);
        }
    }
    
    public void setPublishedName(String publishedName) {
        route.setPublishedName(publishedName);
    }
}
