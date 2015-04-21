package mobi.chouette.service;

import java.util.List;

import mobi.chouette.model.api.Link;


public interface ExcludedJobMethods {

   List<Link> getLinks();
   void setLinks(List<Link> links);
   String getPath();
   void setPath(String path);
   
   
}
