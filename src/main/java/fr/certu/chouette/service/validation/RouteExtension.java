package fr.certu.chouette.service.validation;

public class RouteExtension {
	
	private ChouetteRoute 	chouetteRoute;
	private String 			wayBack;
	
	public void setChouetteRoute(ChouetteRoute chouetteRoute) {
		this.chouetteRoute = chouetteRoute;
	}
	
	public ChouetteRoute getChouetteRoute() {
		return chouetteRoute;
	}
	
	public void setWayBack(String wayBack) {
		this.wayBack = wayBack;
	}
	
	public String getWayBack() {
		return wayBack;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<RouteExtension>\n");
		stb.append("<WayBack>"+wayBack+"</WayBack>\n");
		stb.append("</RouteExtension>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<RouteExtension>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<WayBack>"+wayBack+"</WayBack>\n");
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</RouteExtension>\n");
		return stb.toString();
	}
}
