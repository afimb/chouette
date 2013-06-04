/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import com.vividsolutions.jts.geom.Coordinate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class ValidationParameters 
{
	@Getter @Setter private float test31MinimalDistance ;
	@Getter @Setter private float test32MinimalDistance;
	@Getter @Setter private List<Coordinate> test32Polygon;
	@Getter private String test32PolygonPoints;
	@Getter @Setter private float test37MinimalDistance;
	@Getter @Setter private float test37MaximalDistance;
	@Getter @Setter private float test38aMinimalSpeed;
	@Getter @Setter private float test38aMaximalSpeed;
	@Getter @Setter private float test38bMinimalSpeed;
	@Getter @Setter private float test38bMaximalSpeed;
	@Getter @Setter private float test38cMinimalSpeed;
	@Getter @Setter private float test38cMaximalSpeed;
	@Getter @Setter private float test38dMinimalSpeed;
	@Getter @Setter private float test38dMaximalSpeed;
	@Getter @Setter private float test39MinimalSpeed;
	@Getter @Setter private float test39MaximalSpeed;
	@Getter @Setter private float test310MinimalDistance;
	@Getter @Setter private long test315MinimalTime;
	@Getter @Setter private long test3161MaximalTime;
	@Getter @Setter private long test3163aMaximalTime;
	@Getter @Setter private long test3163bMaximalTime;
	@Getter @Setter private float test321aMinimalSpeed;
	@Getter @Setter private float test321aMaximalSpeed;
	@Getter @Setter private float test321bMinimalSpeed;
	@Getter @Setter private float test321bMaximalSpeed;
	@Getter @Setter private float test321cMinimalSpeed;
	@Getter @Setter private float test321cMaximalSpeed;
	@Getter @Setter private float test321dMinimalSpeed;
	@Getter @Setter private float test321dMaximalSpeed;
	@Getter @Setter private String projectionReference;

	public void addTest32PolygonPoint(Coordinate coordinate)
	{
		if (test32Polygon == null) test32Polygon = new ArrayList<Coordinate>();
		test32Polygon.add(coordinate);
	}

	public void setTest32PolygonPoints(String pointAsString) throws PatternSyntaxException
	{
		test32PolygonPoints = pointAsString;
		test32Polygon=null;
		String[] points = pointAsString.split(" ");
		for (String point : points) 
		{
			String[] coord = point.split(",");
			if (coord.length == 2)
			{
				double x = Double.parseDouble(coord[0]);
				double y = Double.parseDouble(coord[1]);
				addTest32PolygonPoint(new Coordinate(x,y));
			}
		}
	}
	
	@Override 
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("ValidationParameter : ");
		buffer.append("\n   test31MinimalDistance  = ").append(test31MinimalDistance).append(" m ");
		buffer.append("\n   test32MinimalDistance  = ").append(test32MinimalDistance).append(" m ");
		buffer.append("\n   test32PolygonPoints    = ").append(test32PolygonPoints).append(" °longitude,°latitude ... ");
		buffer.append("\n   test37MinimalDistance  = ").append(test37MinimalDistance).append(" m ");
		buffer.append("\n   test37MaximalDistance  = ").append(test37MaximalDistance).append(" m ");
		buffer.append("\n   test38aMinimalSpeed    = ").append(test38aMinimalSpeed).append(" km/h ");
		buffer.append("\n   test38aMaximalSpeed    = ").append(test38aMaximalSpeed).append(" km/h ");
		buffer.append("\n   test38bMinimalSpeed    = ").append(test38bMinimalSpeed).append(" km/h ");
		buffer.append("\n   test38bMaximalSpeed    = ").append(test38bMaximalSpeed).append(" km/h ");
		buffer.append("\n   test38cMinimalSpeed    = ").append(test38cMinimalSpeed).append(" km/h ");
		buffer.append("\n   test38cMaximalSpeed    = ").append(test38cMaximalSpeed).append(" km/h ");
		buffer.append("\n   test38dMinimalSpeed    = ").append(test38dMinimalSpeed).append(" km/h ");
		buffer.append("\n   test38dMaximalSpeed    = ").append(test38dMaximalSpeed).append(" km/h ");
		buffer.append("\n   test39MinimalSpeed     = ").append(test39MinimalSpeed).append(" km/h ");
		buffer.append("\n   test39MaximalSpeed     = ").append(test39MaximalSpeed).append(" km/h ");
		buffer.append("\n   test310MinimalDistance = ").append(test310MinimalDistance).append(" m ");
		buffer.append("\n   test315MinimalTime     = ").append(test315MinimalTime).append(" s ");
		buffer.append("\n   test3161MaximalTime   = ").append(test3161MaximalTime).append(" s ");
		buffer.append("\n   test3163aMaximalTime  = ").append(test3163aMaximalTime).append(" s ");
		buffer.append("\n   test3163bMaximalTime  = ").append(test3163bMaximalTime).append(" s ");
		buffer.append("\n   test321aMinimalSpeed   = ").append(test321aMinimalSpeed).append(" km/h ");
		buffer.append("\n   test321aMaximalSpeed   = ").append(test321aMaximalSpeed).append(" km/h ");
		buffer.append("\n   test321bMinimalSpeed   = ").append(test321bMinimalSpeed).append(" km/h ");
		buffer.append("\n   test321bMaximalSpeed   = ").append(test321bMaximalSpeed).append(" km/h ");
		buffer.append("\n   test321cMinimalSpeed   = ").append(test321cMinimalSpeed).append(" km/h ");
		buffer.append("\n   test321cMaximalSpeed   = ").append(test321cMaximalSpeed).append(" km/h ");
		buffer.append("\n   test321dMinimalSpeed   = ").append(test321dMinimalSpeed).append(" km/h ");
		buffer.append("\n   test321dMaximalSpeed   = ").append(test321dMaximalSpeed).append(" km/h ");
		buffer.append("\n   projectionReference     = ").append(projectionReference);
		
		return buffer.toString();
	}
}
