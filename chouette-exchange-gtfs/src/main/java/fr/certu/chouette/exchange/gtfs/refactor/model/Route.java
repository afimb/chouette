package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.awt.Color;
import java.io.Serializable;
import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Route extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String routeId;

   @Getter
   @Setter
   private String agencyId;

   @Getter
   @Setter
   private String routeShortName;

   @Getter
   @Setter
   private String routeLongName;

   @Getter
   @Setter
   private String routeDesc;

   @Getter
   @Setter
   private RouteType routeType;

   @Getter
   @Setter
   private URL routeUrl;

   @Getter
   @Setter
   private Color routeColor;

   @Getter
   @Setter
   private Color routeTextColor;

   public enum RouteType implements Serializable
   {
      Tram, Subway, Rail, Bus, Ferry, Cable, Gondola, Funicular;

   }
}
