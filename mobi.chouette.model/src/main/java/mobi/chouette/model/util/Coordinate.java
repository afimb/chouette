package mobi.chouette.model.util;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Coordinate
{

   public static final String WGS84 = "EPSG:4326";
   public static final String LAMBERT = "EPSG:27572";
   public static final String UTM_32N = "EPSG:32632";

   @Getter
   @Setter
   public BigDecimal x;

   @Getter
   @Setter
   public BigDecimal y;

}