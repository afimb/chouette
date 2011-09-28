/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

/**
 * @author michel
 *
 */
public class DaoInjector <T extends NeptuneIdentifiedObject>
{
	public static final DaoInjector<AccessLink> createAccessLinkDaoInjector()
	{
		return new DaoInjector<AccessLink>();
	}
	public static final DaoInjector<AccessPoint> createAccessPointDaoInjector()
	{
		return new DaoInjector<AccessPoint>();
	}
	public static final DaoInjector<Company> createCompanyDaoInjector()
	{
		return new DaoInjector<Company>();
	}
	public static DaoInjector<ConnectionLink> createConnectionLinkDaoInjector()
	{
		return new DaoInjector<ConnectionLink>();
	}
	public static DaoInjector<Facility> createFacilityDaoInjector()
	{
		return new DaoInjector<Facility>();
	}
	public static DaoInjector<GroupOfLine> createGroupOfLineDaoInjector()
	{
		return new DaoInjector<GroupOfLine>();
	}
	public static final DaoInjector<JourneyPattern> createJourneyPatternDaoInjector()
	{
		return new DaoInjector<JourneyPattern>();
	}
	public static final DaoInjector<Line> createLineDaoInjector()
	{
		return new DaoInjector<Line>();
	}
	public static final DaoInjector<PTLink>  createPTLinkDaoInjector()
	{
		return new DaoInjector<PTLink>();
	}
	public static final DaoInjector<PTNetwork> createPTNetworkDaoInjector()
	{
		return new DaoInjector<PTNetwork>();
	}
//	public static final DaoInjector<RestrictionConstraint> createRestrictionConstraintDaoInjector()
//	{
//		return new DaoInjector<RestrictionConstraint>();
//	}
	public static final DaoInjector<Route> createRouteDaoInjector()
	{
		return new DaoInjector<Route>();
	}
	public static DaoInjector<StopArea> createStopAreaDaoInjector()
	{
		return new DaoInjector<StopArea>();
	}
	public static final DaoInjector<StopPoint> createStopPointDaoInjector()
	{
		return new DaoInjector<StopPoint>();
	}
	public static DaoInjector<Timetable> createTimetableDaoInjector()
	{
		return new DaoInjector<Timetable>();
	}
	public static DaoInjector<TimeSlot> createTimeSlotDaoInjector()
	{
		return new DaoInjector<TimeSlot>();
	}
	public static DaoInjector<VehicleJourney> createVehicleJourneyDaoInjector()
	{
		return new DaoInjector<VehicleJourney>();
	}


	@Getter @Setter private INeptuneManager<T> manager;
	@Getter @Setter private IDaoTemplate<T> dao;
	@Getter @Setter private IDaoTemplate<T> jdbcDao;

	public void init()
	{
		if (dao != null)
			manager.setDao(dao);
		if (jdbcDao != null)
			manager.setJdbcDao(jdbcDao);
	}
}

