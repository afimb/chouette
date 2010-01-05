package fr.certu.chouette.service.validation;

public class ITL {
	
	private ChouetteLineDescription chouetteLineDescription;
	private String name;
	private String areaId;
	private StopArea area;
	private String lineIdShortcut;
	
	public void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	public ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	
	public String getAreaId() {
		return areaId;
	}
	
	public void setArea(StopArea area) {
		this.area = area;
	}
	
	public StopArea getArea() {
		return area;
	}
	
	public void setLineIdShortcut(String lineIdShortcut) {
		this.lineIdShortcut = lineIdShortcut;
	}
	
	public String getLineIdShortcut() {
		return lineIdShortcut;
	}
	
	public String toString() {
		StringBuffer stb = new StringBuffer();
		stb.append("<ITL>\n");
		stb.append("<Name>"+name+"</Name>\n");
		stb.append("<AreaId>"+areaId+"</AreaId>\n");
		if (lineIdShortcut != null)
			stb.append("<LineIdShortcut>"+lineIdShortcut+"</LineIdShortcut>\n");
		stb.append("</ITL>\n");
		return stb.toString();
	}
	
	public String toString(int indent, int indentSize) {
		StringBuffer stb = new StringBuffer();
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<ITL>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<Name>"+name+"</Name>\n");
		for (int i = 0; i < indent+1; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("<AreaId>"+areaId+"</AreaId>\n");
		if (lineIdShortcut != null) {
			for (int i = 0; i < indent+1; i++)
				for (int j = 0; j < indentSize; j++)
					stb.append(" ");
			stb.append("<LineIdShortcut>"+lineIdShortcut+"</LineIdShortcut>\n");
		}
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < indentSize; j++)
				stb.append(" ");
		stb.append("</ITL>\n");
		return stb.toString();
	}
}
