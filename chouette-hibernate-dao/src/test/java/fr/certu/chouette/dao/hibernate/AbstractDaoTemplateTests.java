/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao.hibernate;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

/**
 * @author michel
 *
 */
@ContextConfiguration(locations={"classpath:testContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=true)

@SuppressWarnings("unchecked")
public abstract class AbstractDaoTemplateTests<T extends NeptuneIdentifiedObject> extends AbstractTransactionalTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(AbstractDaoTemplateTests.class);
   protected HibernateDaoTemplate<T> daoTemplate;

   protected String beanName;
   protected T bean;

   public abstract void createDaoTemplate();

   public abstract void refreshBean();

   private static long nextObjectId = 1;

   private static long getNextObjectId()
   {
      return nextObjectId++;
   }

   public void initDaoTemplate(String beanName, String daoName)
   {
      daoTemplate = (HibernateDaoTemplate<T>) applicationContext.getBean(daoName);
      this.beanName = beanName;
   }


   @Test (groups = {"hibernate"}, description = "daoTemplate should save a bean" )
   public void verifySave() 
   {
      refreshBean(); // refreshBean call save ! 
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean should have id different of zero");
      logger.info("bean " + beanName + " created with id = "+ bean.getId());
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should return a bean" )
   public void verifyGet() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean should have id different of zero");
      Long id = bean.getId();
      T newBean = daoTemplate.get(id );
      Assert.assertFalse(newBean.getId().equals(Long.valueOf(0)),"found Bean should have id different of zero");
      Assert.assertTrue(newBean.getId().equals(id),"found Bean should have asked id="+id+"");
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should return count of objects" )
   public void verifyCount() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean should have id different of zero");
      Long id = bean.getId();
      long count = daoTemplate.count(null);
      Assert.assertTrue(count > 0,"count Bean should not be 0");
      Filter filter = Filter.getNewEqualsFilter("id", id);
      count = daoTemplate.count(filter);
      Assert.assertTrue(count == 1,"count Bean should be 1");
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should check id existance" )
   public void verifyExistsFromId() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Long id = bean.getId();
      boolean ret = daoTemplate.exists(id);
      Assert.assertTrue(ret,"asked id="+id+" should exists");
      id = Long.valueOf(-23);
      ret = daoTemplate.exists(id);
      Assert.assertFalse(ret,"asked id="+id+" should not exists");
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should check objectid existance" )
   public void verifyExistsFromObjectId() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      String id = bean.getObjectId();
      boolean ret = daoTemplate.exists(id);
      Assert.assertTrue(ret,"asked id="+id+" should exists");
      id = "Dummy";
      ret = daoTemplate.exists(id);
      Assert.assertFalse(ret,"asked id="+id+" should not exists");
   }


   @Test (groups = {"hibernate"}, description = "daoTemplate should return all occurences of bean" )
   public void verifyGetAll() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      List<T> beans = daoTemplate.getAll();
      Assert.assertTrue(beans.size() > 0,"size of returned list shouldn't be zero");
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should return maximum 2 occurences of bean" )
   public void verifySelect() 
   {
      // TODO : see how to put prepared objects in test database
      Filter filter = Filter.getNewEmptyFilter();
      filter.addLimit(2);
      List<T> beans = daoTemplate.select(filter);
      Assert.assertTrue(beans.size() <= 2,"size of returned list("+beans.size()+") should be less than 2");
   }

   @Test (groups = {"hibernate","select"}, description = "daoTemplate should return maximum 2 occurences of bean with a filter" )
   public void verifySelectFilter() 
   {
      // TODO : see how to put prepared objects in test database
      Filter filter = getSelectFilter();
      filter.addLimit(2);
      List<T> beans = daoTemplate.select(filter);
      for (T t : beans) {
         System.err.println(t.toString(" ", 0));
      }
      Assert.assertTrue(beans.size() <= 2,"size of returned list("+beans.size()+") should be less than 2");

   }

//   @Test (groups = {"hibernate","hql"}, description = "daoTemplate should return maximum 2 occurences of bean with a filter" )
//   public void verifySelectHql() 
//   {
//      refreshBean();
//      // bean.setId(Long.valueOf(0));
//      System.out.println(bean.toString(" ", 2));
//      if (bean.getId() == null) daoTemplate.save(bean);
//      // TODO : see how to put prepared objects in test database
//      String hql = getHQLFilter();
//      List<Object> values = getHqlValues();
//      List<T> beans = daoTemplate.select(hql,values);
//      for (T t : beans) {
//         Reporter.log(t.toString(" ", 0));
//      }
//      Assert.assertTrue(beans.size() > 0,"size of returned list("+beans.size()+") should be greater than 0");
//
//   }




   @Test (groups = {"hibernate"}, description = "daoTemplate should remove bean" )
   public void verifyRemove() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean for remove should have id different of zero");
      daoTemplate.remove(bean.getId());
   }

   @Test (groups = {"hibernate"}, description = "daoTemplate should update bean" )
   public void verifyUpdate() 
   {
      refreshBean();
      // bean.setId(Long.valueOf(0));
      if (bean.getId() == null) daoTemplate.save(bean);
      Assert.assertFalse(bean.getId().equals(Long.valueOf(0)),"created Bean for update should have id different of zero");
      bean.setName("newname");
      daoTemplate.update(bean);
   }

   // specific filter for model
   protected  Filter getSelectFilter() 
   {
      return Filter.getNewEmptyFilter();
   }

   protected List<Object> getHqlValues()
   {
      List<Object> values = new ArrayList<Object>();
      values.add(Long.valueOf(0));
      return values;
   }

   protected String getHQLFilter()
   {
      return "from "+beanName+" where id > ?";
   }


   // Test model for Dao
   protected PTNetwork createPTNetwork()
   {
      PTNetwork network = new PTNetwork();
      long objectId = getNextObjectId();
      // network.setId(Long.valueOf(0));
      network.setObjectId("Test:PTNetwork:"+objectId);
      network.setCreationTime(new Date());
      network.setCreatorId("TESTNG");
      network.setName("TestNG Network");
      network.setObjectVersion(1);
      network.setPTNetworkSourceType(PTNetworkSourceTypeEnum.PUBLICTRANSPORT);
      network.setRegistrationNumber("TESTNG_"+objectId);
      network.setDescription("Fake Network for Test purpose");
      HibernateDaoTemplate<PTNetwork> networkTemplate = (HibernateDaoTemplate<PTNetwork>) applicationContext.getBean("networkDao");
      networkTemplate.save(network);
      return network;
   }
   protected Company createCompany()
   {
      Company company = new Company();
      long objectId = getNextObjectId();
      // company.setId(Long.valueOf(0));
      company.setObjectId("Test:Company:"+objectId);
      company.setCreationTime(new Date());
      company.setCreatorId("TESTNG");
      company.setName("TestNG Company");
      company.setObjectVersion(1);
      company.setRegistrationNumber("TESTNG_"+objectId);
      HibernateDaoTemplate<Company> companyTemplate = (HibernateDaoTemplate<Company>) applicationContext.getBean("companyDao");
      companyTemplate.save(company);
      return company;
   }

   protected Line createLine()
   {
      Line line = createBasicLine();
      HibernateDaoTemplate<Line> lineTemplate = (HibernateDaoTemplate<Line>) applicationContext.getBean("lineDao");
      lineTemplate.save(line);
      // Add children
      Route route = createBasicRoute();
      line.addRoute(route);
      route.setLine(line);
      HibernateDaoTemplate<Route> routeTemplate = (HibernateDaoTemplate<Route>) applicationContext.getBean("routeDao");
      routeTemplate.save(route);

      return line;

   }

   protected Line createBasicLine() {
      Line line = new Line();
      long objectId = getNextObjectId();
      // line.setId(Long.valueOf(0));
      line.setObjectId("Test:Line:"+objectId);
      line.setCreationTime(new Date());
      line.setCreatorId("TESTNG");
      line.setName("TestNG Line");
      line.setObjectVersion(1);
      line.setRegistrationNumber("TESTNG_"+objectId);
      // must create dependent parent objects 
      PTNetwork network = createPTNetwork();
      line.setPtNetwork(network);
      logger.info("created network with id = "+network.getId());

      Company company = createCompany();
      line.setCompany(company);
      logger.info("created company with id = "+company.getId());
      return line;
   }

   protected Route createBasicRoute()
   {
      Route route = new Route();
      long objectId = getNextObjectId();
      route.setCreationTime(new Date());
      route.setCreatorId("TESTNG");
      route.setObjectId("Test:Route:"+objectId);
      route.setObjectVersion(1);
      route.setWayBack("A");
      return route;
   }

   protected Route createRoute()
   {
      Route route = createBasicRoute();

      // must create dependent parent objects 
      Line line = createBasicLine();
      HibernateDaoTemplate<Line> lineTemplate = (HibernateDaoTemplate<Line>) applicationContext.getBean("lineDao");
      lineTemplate.save(line);
      route.setLine(line);
      logger.info("created line with id = "+line.getId());
      HibernateDaoTemplate<Route> routeTemplate = (HibernateDaoTemplate<Route>) applicationContext.getBean("routeDao");
      routeTemplate.save(route);

      return route;
   }

   protected StopArea createStopArea()
   {
      StopArea stoparea = new StopArea();
      long objectId = getNextObjectId();
      stoparea.setCreationTime(new Date());
      stoparea.setCreatorId("TESTNG");
      stoparea.setObjectId("Test:StopArea:"+objectId);
      stoparea.setObjectVersion(1);
      stoparea.setAreaType(ChouetteAreaEnum.BOARDINGPOSITION);
      AreaCentroid centroid = new AreaCentroid();
      BigDecimal latitude = new BigDecimal(46.5220796582747800);
      BigDecimal longitude = new BigDecimal(5.6110095977783200);

      centroid.setLatitude(latitude);
      centroid.setLongitude(longitude);
      centroid.setLongLatType(LongLatTypeEnum.WGS84);
      Address address = new Address();
      address.setCountryCode("39397");
      centroid.setAddress(address);
      stoparea.setAreaCentroid(centroid);
      HibernateDaoTemplate<StopArea> template = (HibernateDaoTemplate<StopArea>) applicationContext.getBean("stopAreaDao");
      template.save(stoparea);

      return stoparea;
   }

   protected StopPoint createStopPoint()
   {
      StopPoint stopPoint = new StopPoint();
      long objectId = getNextObjectId();
      stopPoint.setCreatorId("TESTNG");
      stopPoint.setObjectId("Test:StopPoint:"+objectId);
      stopPoint.setContainedInStopArea(createStopArea());
      Address address = new Address();
      address.setCountryCode("39397");
      stopPoint.setAddress(address);
      stopPoint.setRoute(createRoute());
      HibernateDaoTemplate<StopPoint> template = (HibernateDaoTemplate<StopPoint>) applicationContext.getBean("stopPointDao");
      template.save(stopPoint);

      return stopPoint;
   }

   protected JourneyPattern createJourneyPattern()
   {
      JourneyPattern journeyPattern = new JourneyPattern();
      journeyPattern.setObjectId("Test:JourneyPattern:"+getNextObjectId());
      Route route = createRoute();
      journeyPattern.setRoute(route);
      journeyPattern.setPublishedName("JourneyPattern");
      journeyPattern.setCreatorId("TESTNG");
      HibernateDaoTemplate<JourneyPattern> template = (HibernateDaoTemplate<JourneyPattern>) applicationContext.getBean("journeyPatternDao");
      template.save(journeyPattern);

      return journeyPattern;
   }


   protected ConnectionLink createConnectionLink() 
   {
      ConnectionLink connectionLink = new ConnectionLink();
      connectionLink.setObjectId("ConnectionLink:"+getNextObjectId());
      connectionLink.setStairsAvailable(true);
      connectionLink.setStartOfLink(createStopArea());
      connectionLink.setEndOfLink(createStopArea());
      connectionLink.setDefaultDuration(new Time(new Date().getTime()));
      return connectionLink;
   }

   protected PTLink createPtLink() 
   {
      PTLink ptLink = new PTLink();
      ptLink.setObjectId("PTLink:"+getNextObjectId());
      ptLink.setComment("ptlink comment");
      ptLink.setRoute(createBasicRoute());
      ptLink.setStartOfLink(createStopPoint());
      ptLink.setEndOfLink(createStopPoint());

      return ptLink;
   }

   protected TimeSlot createTimeSlot() 
   {
      TimeSlot timeSlot = new TimeSlot();
      timeSlot.setObjectId("TimeSlot:"+getNextObjectId());
      timeSlot.setCreatorId("TESTNG");
      long begin = 8*3600000; // 08:00
      timeSlot.setBeginningSlotTime(new java.sql.Time(begin));

      timeSlot.setFirstDepartureTimeInSlot(new java.sql.Time(begin));
      begin += 3600000; // 09:00
      timeSlot.setEndSlotTime(new java.sql.Time(begin));
      timeSlot.setLastDepartureTimeInSlot(new java.sql.Time(begin));

      return timeSlot;
   }

   protected VehicleJourney createVehicleJourney() 
   {
      HibernateDaoTemplate<TimeSlot> timeSlotTemplate = (HibernateDaoTemplate<TimeSlot>) applicationContext.getBean("timeSlotDao");
      HibernateDaoTemplate<JourneyPattern> journeyPatternTemplate = (HibernateDaoTemplate<JourneyPattern>) applicationContext.getBean("journeyPatternDao");
      HibernateDaoTemplate<Line> lineTemplate = (HibernateDaoTemplate<Line>) applicationContext.getBean("lineDao");
      HibernateDaoTemplate<Route> routeTemplate = (HibernateDaoTemplate<Route>) applicationContext.getBean("routeDao");

      VehicleJourney vehicleJourney = new VehicleJourney();
      vehicleJourney.setObjectId("VehicleJourney:"+getNextObjectId());

      JourneyPattern journeyPattern = createJourneyPattern();
      journeyPatternTemplate.save(journeyPattern);
      vehicleJourney.setJourneyPattern(journeyPattern);

      Route route = createBasicRoute();
      routeTemplate.save(route);
      vehicleJourney.setRoute(route);

      Line line=createBasicLine();
      lineTemplate.save(line);
      vehicleJourney.setLine(line);

      TimeSlot timeSlot = createTimeSlot();
      timeSlotTemplate.save(timeSlot);
      vehicleJourney.setTimeSlot(timeSlot);

      return vehicleJourney;
   }

   protected AccessPoint createAccessPoint()
   {
      AccessPoint accessPoint = new AccessPoint();
      accessPoint.setObjectId("Test:AccessPoint:"+getNextObjectId());
      accessPoint.setCreationTime(new Date());
      BigDecimal latitude = new BigDecimal(46.5220796582747800);
      BigDecimal longitude = new BigDecimal(5.6110095977783200);
      accessPoint.setLatitude(latitude);
      accessPoint.setLongitude(longitude);
      accessPoint.setName("AccessPoint TEST");

      return accessPoint;
   }

   protected AccessLink createAccessLink()
   {
      AccessLink accessLink = new AccessLink();
      accessLink.setAccessPoint(createAccessPoint());
      accessLink.setObjectId("AccessLink:"+getNextObjectId());
      accessLink.setCreationTime(new Date());
      accessLink.setStopArea(createStopArea());
      accessLink.addUserNeed(UserNeedEnum.ALLERGIC);
      accessLink.addUserNeed(UserNeedEnum.ASSISTEDWHEELCHAIR);
      accessLink.setDefaultDuration(new Time(new Date().getTime()));

      return accessLink;
   }

   protected Timetable createTimetable()
   {
      Timetable timetable = new Timetable();
      
      timetable.setObjectId("Test:Timetable:"+getNextObjectId());
      timetable.setCreationTime(new Date());
      timetable.setName("TestNG Timetable");
      
      timetable.addCalendarDay(toDate("01/10/2011") );
      timetable.addPeriod(new Period(toDate("01/11/2011"),toDate("01/11/2012")));

      return timetable;
   }

   protected static java.sql.Date toDate(String sDate)
   {
      SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
      Date d = new Date();
      try
      {
          d = format.parse(sDate);
      }
      catch (ParseException e)
      {
         
      }
      return new java.sql.Date(d.getTime());
      
   }
}
