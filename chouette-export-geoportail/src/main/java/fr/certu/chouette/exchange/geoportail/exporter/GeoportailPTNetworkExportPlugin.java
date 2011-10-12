package fr.certu.chouette.exchange.geoportail.exporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

/**
 * export stopareas and access points from a Network for IGN's Geoportail
 */
public class GeoportailPTNetworkExportPlugin implements IExportPlugin<PTNetwork>
{
   private static final Logger logger       = Logger.getLogger(GeoportailPTNetworkExportPlugin.class);
   private static final String GEOPORTAIL_CHARSET = "UTF-8";
   private static final char FIELD_SEPARATOR = ',';
   private static final char LINE_SEPARATOR = '\n';
   private static final List<String> outputFileExtensions = new ArrayList<String>();
   private static final String QUAY_PICTONAME = "quai-50.png";
   private static final String BOARDINGPOSITION_PICTONAME = "point-embarquement-50.png";
   private static final String COMMERCIALSTOP_PICTONAME = "zonecommerciale-50.png";
   private static final String STOPPLACE_PICTONAME = "pole-echange-50.png";
   private static final String ACCESSPOINT_PICTONAME = "point-acces-50.png";

   /**
    * describe plugin API
    */
   private FormatDescription   description;
   private static final String tcPointHeader = "objectid,geom_longitude,geom_latitude,registrationnumber,name,tcpointtype,fullname,comment,countrycode,creationtime,creatorid,longlattype,longitude,latitude,projectiontype,x,y,objectversion,streetname";
   private static final String aotHeader = "source,logo,url,urllegalinformation,legalinformation";
   private static final String metadataHeader = "layer,name,address,email,telephone,datestamp,lastupdatestamp,usernote,minlongitude,minlatitude,maxlongitude,maxlatitude";
   private static final String pictosHeader = "pointtype,picto,minscale,maxscale";
   private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

   static
   {
      outputFileExtensions.addAll(Arrays.asList(new String[] { "gif","png","jpg","jpeg","tmp" }));
   }
   /**
    * build a GtfsLineExportPlugin and fill API description
    */
   public GeoportailPTNetworkExportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GEOPORTAIL");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false,
               true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("logoFile", ParameterDescription.TYPE.FILEPATH, false,
               true);
         param.setAllowedExtensions(outputFileExtensions);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("logoFileName", ParameterDescription.TYPE.FILENAME, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("aotURL", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("legalInformation", ParameterDescription.TYPE.STRING, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("legalInformationURL", ParameterDescription.TYPE.STRING, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("aotAddress", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("aotEmail", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("aotPhone", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("readMe", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("stopNote", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("accessNote", ParameterDescription.TYPE.STRING, false,
               true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("quayPicto", ParameterDescription.TYPE.FILEPATH, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("quayPictoMinScale", ParameterDescription.TYPE.INTEGER, false,
         "2000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("quayPictoMaxScale", ParameterDescription.TYPE.INTEGER, false,
         "4000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("boardingPositionPicto", ParameterDescription.TYPE.FILEPATH, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("boardingPositionPictoMinScale", ParameterDescription.TYPE.INTEGER, false,
         "2000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("boardingPositionPictoMaxScale", ParameterDescription.TYPE.INTEGER, false,
         "4000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("commercialStopPointPicto", ParameterDescription.TYPE.FILEPATH, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("commercialStopPointPictoMinScale", ParameterDescription.TYPE.INTEGER, false,
         "1600");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("commercialStopPointPictoMaxScale", ParameterDescription.TYPE.INTEGER, false,
         "4000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("stopPlacePicto", ParameterDescription.TYPE.FILEPATH, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("stopPlacePictoMinScale", ParameterDescription.TYPE.INTEGER, false,
         "1600");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("stopPlacePictoMaxScale", ParameterDescription.TYPE.INTEGER, false,
         "4000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("accessPointPicto", ParameterDescription.TYPE.FILEPATH, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("accessPointPictoMinScale", ParameterDescription.TYPE.INTEGER, false,
         "1000");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("accessPointPictoMaxScale", ParameterDescription.TYPE.INTEGER, false,
         "2000");
         params.add(param);
      }

      description.setParameterDescriptions(params);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List,
    * java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<PTNetwork> beans, List<ParameterValue> parameters, ReportHolder report)
   throws ChouetteException
   {

      if (beans == null || beans.isEmpty())
      {
         throw new IllegalArgumentException("no beans to export");
      }

      if (beans.size() > 1)
      {
         throw new IllegalArgumentException("only one PtNetwork can be exported at a time");
      }

      AOTData aotData = checkParameters(parameters);

      try
      {
         // Create the ZIP file
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(aotData.fileName));

         NeptuneData neptuneData = new NeptuneData();
         neptuneData.populate(beans.get(0));
         aotData.source=neptuneData.getSource();
         writeTcPointsFile(out,aotData,neptuneData);
         writeAOTFile(out,aotData,neptuneData);
         writeMetatdataFile(out,aotData,neptuneData);
         writePictosFile(out,aotData);
         writeReadMeFile(out,aotData);
         writeLogo(out,aotData);
         writePictos(out,aotData);

         // Complete the ZIP file
         out.close();
      }
      catch (IOException e)
      {
         logger.error("cannot create zip file", e);
         // throw new
         // ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE, e);
      }
   }

   /**
    * @param parameters
    * @param aotData
    */
   private AOTData checkParameters(List<ParameterValue> parameters)
   {
      AOTData aotData = new AOTData();
      String logoFileName = null;
      String quayFileName = null;
      String boardingPositionFileName = null;
      String commercialStopPointFileName = null;
      String stopPlaceFileName = null;
      String accessFileName = null;
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("outputFile"))
            {
               aotData.fileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("logoFile"))
            {
               logoFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("logoFileName"))
            {
               aotData.logoFileName = svalue.getFilenameValue();
            }
            else if (svalue.getName().equalsIgnoreCase("aotURL"))
            {
               aotData.url = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("legalInformation"))
            {
               aotData.legalInformation = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("legalInformationURL"))
            {
               aotData.legalInformationURL = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("aotAddress"))
            {
               aotData.address = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("aotEmail"))
            {
               aotData.email = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("aotPhone"))
            {
               aotData.phone = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("readMe"))
            {
               aotData.readMe = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("stopNote"))
            {
               aotData.stopNote = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("accessNote"))
            {
               aotData.accessNote = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("quayPicto"))
            {
               quayFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("quayPictoMinScale"))
            {
               aotData.quayMinScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("quayPictoMaxScale"))
            {
               aotData.quayMaxScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("boardingPositionPicto"))
            {
               boardingPositionFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("boardingPositionPictoMinScale"))
            {
               aotData.boardingPositionMinScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("boardingPositionPictoMaxScale"))
            {
               aotData.boardingPositionMaxScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("commercialStopPointPicto"))
            {
               commercialStopPointFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("commercialStopPointPictoMinScale"))
            {
               aotData.commercialStopPointMinScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("commercialStopPointPictoMaxScale"))
            {
               aotData.commercialStopPointMaxScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("stopPlacePicto"))
            {
               stopPlaceFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("stopPlacePictoMinScale"))
            {
               aotData.stopPlaceMinScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("stopPlacePictoMaxScale"))
            {
               aotData.stopPlaceMaxScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("accessPointPicto"))
            {
               accessFileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("accessPointPictoMinScale"))
            {
               aotData.accessMinScale = svalue.getIntegerValue();
            }
            else if (svalue.getName().equalsIgnoreCase("accessPointPictoMaxScale"))
            {
               aotData.accessMaxScale = svalue.getIntegerValue();
            }
            else 
            {
               throw new IllegalArgumentException("unknown parameter "+svalue.getName());
            }

         }
      }
      if (aotData.fileName == null)
      {
         throw new IllegalArgumentException("outputFile required");
      }
      if (logoFileName == null)
      {
         throw new IllegalArgumentException("logoFile required");
      }
      File logoFile = new File(logoFileName);
      if (!logoFile.exists())
      {
         throw new IllegalArgumentException("logoFile : file does not exists");
      }

      String ext = FilenameUtils.getExtension(logoFileName).toLowerCase();
      if (!outputFileExtensions.contains(ext))
      {
         throw new IllegalArgumentException("logoFile : unallowed extension ");
      }

      if (ext.equals("tmp") && aotData.logoFileName == null)
      {
         throw new IllegalArgumentException("logoFileName required for uploaded logoFile");
      }
      else
      {
         aotData.logoFileName = logoFile.getName();
      }

      try
      {
         aotData.logoFile = FileUtils.readFileToByteArray(logoFile);
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException("logoFile : cannot read file");
      }
      if (aotData.url == null)
      {
         throw new IllegalArgumentException("aotURL required");
      }
      if (aotData.address == null)
      {
         throw new IllegalArgumentException("aotAddress required");
      }
      if (aotData.email == null)
      {
         throw new IllegalArgumentException("aotEmail required");
      }
      if (aotData.phone == null)
      {
         throw new IllegalArgumentException("aotPhone required");
      }
      if (aotData.stopNote == null)
      {
         throw new IllegalArgumentException("stopNote required");
      }
      if (aotData.accessNote == null)
      {
         aotData.accessNote = aotData.stopNote;
      }
      if (quayFileName != null)
      {
         try
         {
            aotData.quayFile = FileUtils.readFileToByteArray(new File(quayFileName));
         }
         catch (IOException e)
         {
            throw new IllegalArgumentException("quayPicto : fail to read");
         }
      }
      else
      {
         aotData.quayFile = getFile(QUAY_PICTONAME);
      }
      if (boardingPositionFileName != null)
      {
         try
         {
            aotData.boardingPositionFile = FileUtils.readFileToByteArray(new File(boardingPositionFileName));
         }
         catch (IOException e)
         {
            throw new IllegalArgumentException("boardingPositionPicto : file does not exists");
         }
      }
      else
      {
         aotData.boardingPositionFile = getFile(BOARDINGPOSITION_PICTONAME);
      }
      if (commercialStopPointFileName != null)
      {
         try
         {
         aotData.commercialStopPointFile = FileUtils.readFileToByteArray(new File(commercialStopPointFileName));
         }
         catch (IOException e)
            {
            throw new IllegalArgumentException("commercialStopPointPicto : file does not exists");
         }
      }
      else
      {
         aotData.commercialStopPointFile = getFile(COMMERCIALSTOP_PICTONAME);
      }
      if (stopPlaceFileName != null)
      {
         try
         {
         aotData.stopPlaceFile = FileUtils.readFileToByteArray(new File(stopPlaceFileName));
         }
         catch (IOException e)
            {
            throw new IllegalArgumentException("stopPlacePicto : file does not exists");
         }
      }
      else
      {
         aotData.stopPlaceFile = getFile(STOPPLACE_PICTONAME);
      }
      if (accessFileName != null)
      {
         try
         {
         aotData.accessFile = FileUtils.readFileToByteArray(new File(accessFileName));
         }
         catch (IOException e)
            {
            throw new IllegalArgumentException("accessPointPicto : file does not exists");
         }
      }
      else
      {
         aotData.accessFile = getFile(ACCESSPOINT_PICTONAME);
      }
      return aotData;
   }

   private byte[] getFile(String fileName)
   {
      PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
      try
      {
         Resource[] re = test.getResources("classpath*:/"+fileName);
         for (Resource resource : re)
         {
            if (resource.getURL().toString().contains("geoportail"))
            {
               InputStream stream = resource.getInputStream();
               BufferedInputStream bs = new BufferedInputStream(stream, 8192);
               byte[] res = new byte[0];
               byte[] temp = new byte[8192];
               int count = bs.read(temp);
               while (count == 8192)
               {
                  int size = res.length;
                  res = Arrays.copyOf(res, res.length+count);
                  System.arraycopy(temp, 0, res, size, count);
                  count = bs.read(temp);
               }
               if (count >0)
               {
                  int size = res.length;
                  res = Arrays.copyOf(res, res.length+count);
                  System.arraycopy(temp, 0, res, size, count);
               }
               return res;
            }
         }
      } 
      catch (Exception e) 
      {
         throw new RuntimeException("missing file "+fileName+" in jar",e);
      }
      throw new RuntimeException("missing file "+fileName+" in jar");
   }

   private void writeTcPointsFile(ZipOutputStream out, AOTData aotData, NeptuneData neptuneData)
   {
      if (neptuneData.getStopAreas().isEmpty())
      {
         // TODO error
         logger.info("Stoparea is empty, not produced");
         return;
      }
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GEOPORTAIL_CHARSET);
         writer.write(tcPointHeader );
         writer.write(LINE_SEPARATOR);

         for (StopArea area : neptuneData.stopAreas)
         {
            writer.write(toCSV(area,aotData));
            writer.write(LINE_SEPARATOR);
         }
         for (AccessPoint access : neptuneData.accessPoints)
         {
            writer.write(toCSV(access,aotData));
            writer.write(LINE_SEPARATOR);
         }
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry("tc_points.csv");
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error("tc_points.csv failure "+e.getMessage(),e);
      }
   }

   private String toCSV(AccessPoint access, AOTData aotData)
   {
      aotData.insertCoords(access.getLongitude(),access.getLatitude());
      aotData.updateAccessCreationTime(access.getCreationTime());
      StringBuilder builder = new StringBuilder(); 
      builder.append(quoted(access.getObjectId())); // objectid
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getLongitude())); //geom_longitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getLatitude())); //geom_latitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(null)); // registrationnumber (not implemented
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getName())); // name
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted("AccessPoint")); // tcpointtype
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getName())); // fullname (no matching)
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getComment())); // comment
      builder.append(FIELD_SEPARATOR);
      if (access.getAddress() != null)
      {
         builder.append(quoted(access.getAddress().getCountryCode())); // countrycode
      }
      else
      {
         builder.append(quoted(null));
      }
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(sdf.format(access.getCreationTime()))); // creationtime
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getCreatorId())); // creatorid
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getLongLatType())); // longlattype
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getLongitude())); // longitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getLatitude())); // latitude
      builder.append(FIELD_SEPARATOR);
      if (access.getProjectedPoint() != null)
      {
         builder.append(quoted(access.getProjectedPoint().getProjectionType())); // projectiontype
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(access.getProjectedPoint().getX())); // x
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(access.getProjectedPoint().getY())); // y
      }
      else
      {
         builder.append(quoted(null));
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(null));
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(null));
      }
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(access.getObjectVersion())); // objectversion
      builder.append(FIELD_SEPARATOR);
      if (access.getAddress() != null)
      {
         builder.append(quoted(access.getAddress().getStreetName())); // streetname
      }
      else
      {
         builder.append(quoted(null));
      }
      return builder.toString();
   }

   private String toCSV(StopArea area, AOTData aotData)
   {
      aotData.insertCoords(area.getAreaCentroid().getLongitude(),area.getAreaCentroid().getLatitude());
      aotData.updateAccessCreationTime(area.getCreationTime());

      StringBuilder builder = new StringBuilder(); 
      builder.append(quoted(area.getObjectId())); // objectid
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaCentroid().getLongitude())); //geom_longitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaCentroid().getLatitude())); //geom_latitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getRegistrationNumber())); // registrationnumber
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getName())); // name
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaType())); // tcpointtype
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getName())); // fullname (no matching)
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getComment())); // comment
      builder.append(FIELD_SEPARATOR);
      if (area.getAreaCentroid().getAddress() != null)
      {
         builder.append(quoted(area.getAreaCentroid().getAddress().getCountryCode())); // countrycode
      }
      else
      {
         builder.append(quoted(null));
      }
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(sdf.format(area.getCreationTime()))); // creationtime
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getCreatorId())); // creatorid
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaCentroid().getLongLatType())); // longlattype
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaCentroid().getLongitude())); // longitude
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getAreaCentroid().getLatitude())); // latitude
      builder.append(FIELD_SEPARATOR);
      if (area.getAreaCentroid().getProjectedPoint() != null)
      {
         builder.append(quoted(area.getAreaCentroid().getProjectedPoint().getProjectionType())); // projectiontype
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(area.getAreaCentroid().getProjectedPoint().getX())); // x
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(area.getAreaCentroid().getProjectedPoint().getY())); // y
      }
      else
      {
         builder.append(quoted(null));
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(null));
         builder.append(FIELD_SEPARATOR);
         builder.append(quoted(null));
      }
      builder.append(FIELD_SEPARATOR);
      builder.append(quoted(area.getObjectVersion())); // objectversion
      builder.append(FIELD_SEPARATOR);
      if (area.getAreaCentroid().getAddress() != null)
      {
         builder.append(quoted(area.getAreaCentroid().getAddress().getStreetName())); // streetname
      }
      else
      {
         builder.append(quoted(null));
      }

      return builder.toString();
   }

   private String quoted(Object obj)
   {
      if (obj == null) return "\"\"";
      String text = obj.toString();
      String[] token = text.split("\"");
      String ret = "";
      for (String item : token)
      {
         ret += "\""+item+"\"";
      }
      return ret;
   }

   private void writeAOTFile(ZipOutputStream out, AOTData aotData, NeptuneData neptuneData)
   {
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GEOPORTAIL_CHARSET);
         writer.write(aotHeader );
         writer.write(LINE_SEPARATOR);
         writer.write(quoted(aotData.source));
         writer.write(FIELD_SEPARATOR);
         writer.write(quoted(aotData.logoFileName));
         writer.write(FIELD_SEPARATOR);
         writer.write(quoted(aotData.url));
         writer.write(FIELD_SEPARATOR);
         writer.write(quoted(aotData.legalInformationURL));
         writer.write(FIELD_SEPARATOR);
         writer.write(quoted(aotData.legalInformation));
         writer.write(LINE_SEPARATOR);
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry("aot.csv");
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error("aot.csv failure "+e.getMessage(),e);
      }


   }

   private void writeMetatdataFile(ZipOutputStream out, AOTData aotData, NeptuneData neptuneData)
   {
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GEOPORTAIL_CHARSET);
         writer.write(metadataHeader );
         writer.write(LINE_SEPARATOR);
         Calendar c = Calendar.getInstance();
         if (aotData.stopAreaCreationTime != null)
         {
            writeMetaData(writer,aotData,"stoparea",aotData.stopAreaCreationTime,c.getTime(),aotData.stopNote);
         }
         if (aotData.accessCreationTime != null)
         {
            writeMetaData(writer,aotData,"accesspoint",aotData.accessCreationTime,c.getTime(),aotData.accessNote);
         }
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry("chouette_metadata.csv");
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error("chouette_metadata.csv failure "+e.getMessage(),e);
      }

   }

   private void writeMetaData(OutputStreamWriter writer, AOTData aotData, String layer, Date creationTime, Date updateTime, String note) throws IOException
   {
      writer.write(quoted(layer));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.source));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.address));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.email));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.phone));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(sdf.format(creationTime)));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(sdf.format(updateTime)));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(note));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.minLongitude));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.minLatitude));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.maxLongitude));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(aotData.maxLatitude));
      writer.write(LINE_SEPARATOR);

   }

   private void writePictosFile(ZipOutputStream out, AOTData aotData)
   {
      // pointtype,picto,minscale,maxscale
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GEOPORTAIL_CHARSET);
         writer.write(pictosHeader );
         writer.write(LINE_SEPARATOR);

         writePictoInfo(writer, "Quay", QUAY_PICTONAME, aotData.quayMinScale, aotData.quayMaxScale);
         writePictoInfo(writer, "BoardingPosition", BOARDINGPOSITION_PICTONAME, aotData.boardingPositionMinScale, aotData.boardingPositionMaxScale);
         writePictoInfo(writer, "CommercialStopPoint", COMMERCIALSTOP_PICTONAME, aotData.commercialStopPointMinScale, aotData.commercialStopPointMaxScale);
         writePictoInfo(writer, "StopPlace", STOPPLACE_PICTONAME, aotData.stopPlaceMinScale, aotData.stopPlaceMaxScale);
         writePictoInfo(writer, "AccessPoint", ACCESSPOINT_PICTONAME, aotData.accessMinScale, aotData.accessMaxScale);
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry("pictos.csv");
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error("tc_points.csv failure "+e.getMessage(),e);
      }

   }

   private void writePictoInfo(OutputStreamWriter writer, String pictoType,String pictoName,Long minScale, Long maxScale) throws IOException
   {
      writer.write(quoted(pictoType));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(pictoName));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(minScale));
      writer.write(FIELD_SEPARATOR);
      writer.write(quoted(maxScale));
      writer.write(LINE_SEPARATOR);

   }

   private void writeReadMeFile(ZipOutputStream out, AOTData aotData)
   {
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GEOPORTAIL_CHARSET);
         writer.write(aotData.readMe );
         writer.write(LINE_SEPARATOR);
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry("Readme.txt");
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error("Readme.txt failure "+e.getMessage(),e);
      }

   }


   /**
    * @param out
    * @param aotData
    */
   private void writeLogo(ZipOutputStream out, AOTData aotData)
   {
      String filename = "Logos"+File.separator+aotData.logoFileName;
      writeFile(out, aotData.logoFile, filename);
   }

   /**
    * @param out
    * @param file
    * @param filename
    */
   private void writeFile(ZipOutputStream out, byte[] fileContent, String filename)
   {
      try
      {
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry(filename);
         out.putNextEntry(entry);

         out.write(fileContent);

         // Complete the entry
         out.closeEntry();
      }
      catch (IOException e)
      {
         logger.error(filename+" failure "+e.getMessage(),e);
      }
   }

   private void writePictos(ZipOutputStream out,AOTData aotData)
   {
      writeFile(out,aotData.quayFile,"Pictos"+File.separator+QUAY_PICTONAME);
      writeFile(out,aotData.boardingPositionFile,"Pictos"+File.separator+BOARDINGPOSITION_PICTONAME);
      writeFile(out,aotData.commercialStopPointFile,"Pictos"+File.separator+COMMERCIALSTOP_PICTONAME);
      writeFile(out,aotData.stopPlaceFile,"Pictos"+File.separator+STOPPLACE_PICTONAME);
      writeFile(out,aotData.accessFile,"Pictos"+File.separator+ACCESSPOINT_PICTONAME);
   }


   public class AOTData
   {
      String source = null;
      String logoFileName = null;
      String url = null;
      String legalInformation = null;
      String legalInformationURL = null;
      String address = null;
      String email = null;
      String phone = null;
      String readMe = null;
      String stopNote = null;
      String accessNote = null;
      String fileName = null;
      byte[] logoFile = null;
      BigDecimal minLatitude = null;
      BigDecimal maxLatitude = null;
      BigDecimal minLongitude = null;
      BigDecimal maxLongitude = null;
      Date stopAreaCreationTime = null;
      Date accessCreationTime = null;
      byte[] quayFile = null;
      Long quayMinScale = Long.valueOf(2000);
      Long quayMaxScale = Long.valueOf(4000);
      byte[] boardingPositionFile = null;
      Long boardingPositionMinScale = Long.valueOf(2000);
      Long boardingPositionMaxScale = Long.valueOf(4000);
      byte[]  commercialStopPointFile = null;
      Long commercialStopPointMinScale = Long.valueOf(1600);
      Long commercialStopPointMaxScale = Long.valueOf(4000);
      byte[]  stopPlaceFile = null;
      Long stopPlaceMinScale = Long.valueOf(1600);
      Long stopPlaceMaxScale = Long.valueOf(4000);
      byte[]  accessFile = null;
      Long accessMinScale = Long.valueOf(1000);
      Long accessMaxScale = Long.valueOf(2000);

      public void insertCoords(BigDecimal longitude, BigDecimal latitude)
      {
         if (minLatitude == null)
         {
            minLatitude = latitude;
            maxLatitude = latitude;
            minLongitude = longitude;
            maxLongitude = longitude;
         }
         else
         {
            if (latitude.compareTo(minLatitude) < 0) minLatitude = latitude;
            if (latitude.compareTo(maxLatitude) > 0) maxLatitude = latitude;
            if (longitude.compareTo(minLongitude) < 0) minLongitude = longitude;
            if (longitude.compareTo(maxLongitude) > 0) maxLongitude = longitude;
         }
      }
      public void updateAccessCreationTime(Date creationTime)
      {
         if (stopAreaCreationTime == null || stopAreaCreationTime.after(creationTime))
         {
            stopAreaCreationTime = creationTime;
         }
      }
      public void updateStopAreaCreationTime(Date creationTime)
      {
         if (accessCreationTime == null || accessCreationTime.after(creationTime))
         {
            accessCreationTime = creationTime;
         }
      }

   }
}
