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

import com.vividsolutions.jts.geom.Coordinate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class ValidationParameters 
{
	@Getter @Setter private float test3_1_MinimalDistance;
	@Getter @Setter private float test3_2_MinimalDistance;
	@Getter @Setter private List<Coordinate> test3_2_Polygon;
	@Getter @Setter private float test3_7_MinimalDistance;
	@Getter @Setter private float test3_7_MaximalDistance;
	@Getter @Setter private float test3_8a_MinimalSpeed;
	@Getter @Setter private float test3_8a_MaximalSpeed;
	@Getter @Setter private float test3_8b_MinimalSpeed;
	@Getter @Setter private float test3_8b_MaximalSpeed;
	@Getter @Setter private float test3_8c_MinimalSpeed;
	@Getter @Setter private float test3_8c_MaximalSpeed;
	@Getter @Setter private float test3_8d_MinimalSpeed;
	@Getter @Setter private float test3_8d_MaximalSpeed;
	@Getter @Setter private float test3_9_MinimalSpeed;
	@Getter @Setter private float test3_9_MaximalSpeed;
	@Getter @Setter private float test3_10_MinimalDistance;
	@Getter @Setter private long test3_15_MinimalTime;
	@Getter @Setter private long test3_16c_MinimalTime;
	@Getter @Setter private long test3_16c_MaximalTime;
	@Getter @Setter private long test3_16_3a_MinimalTime;
	@Getter @Setter private long test3_16_3b_MinimalTime;
	@Getter @Setter private float test3_21a_MinimalSpeed;
	@Getter @Setter private float test3_21a_MaximalSpeed;
	@Getter @Setter private float test3_21b_MinimalSpeed;
	@Getter @Setter private float test3_21b_MaximalSpeed;
	@Getter @Setter private float test3_21c_MinimalSpeed;
	@Getter @Setter private float test3_21c_MaximalSpeed;
	@Getter @Setter private float test3_21d_MinimalSpeed;
	@Getter @Setter private float test3_21d_MaximalSpeed;
	@Getter @Setter private String projection_reference;

	public void addTest3_2_PolygonPoint(Coordinate coordinate)
	{
		if (test3_2_Polygon == null) test3_2_Polygon = new ArrayList<Coordinate>();
		test3_2_Polygon.add(coordinate);
	}

	public void setTest3_2_PolygonPoints(String pointAsString)
	{
		String[] points = pointAsString.split(" ");
		for (String point : points) 
		{
			String[] coord = point.split(",");
			if (coord.length == 2)
			{
				double x = Double.parseDouble(coord[0]);
				double y = Double.parseDouble(coord[1]);
				addTest3_2_PolygonPoint(new Coordinate(x,y));
			}
		}
	}
}
