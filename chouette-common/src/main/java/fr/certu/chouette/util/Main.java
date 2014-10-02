package fr.certu.chouette.util;

import org.apache.log4j.BasicConfigurator;
import org.geotools.referencing.CRS;
// import org.geotools.referencing.operation.projection.LambertConformal1SP;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class Main
{

   public static void main(String[] args) throws Exception
   {
      BasicConfigurator.configure();

      CRS.decode("EPSG:4326");
      CRS.decode("EPSG:27591");

      CRS.decode("EPSG:9801");

      //
      // Coordinate p = new Coordinate(new BigDecimal(2.373), new
      // BigDecimal(48.79));
      // Coordinate r = CoordinateUtil.transform("EPSG:4326", "EPSG:9801", p
      // );
      //
      // System.out.println("[DSU]" + r );

   }

}
