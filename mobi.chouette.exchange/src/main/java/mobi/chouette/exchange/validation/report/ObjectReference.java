package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.ChouetteIdGenerator;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class ObjectReference extends AbstractReport implements Constant{

	public enum TYPE {
		network("Network"), company("Company"), group_of_line("GroupOfLine"), stop_area("StopArea"), stop_point(
				"StopPoint"), connection_link("ConnectionLink"), access_point("AccessPoint"), access_link("AccessLink"), time_table(
				"Timetable"), line("Line"), route("Route"), journey_pattern("JourneyPattern"), vehicle_journey(
				"VehicleJourney");

		private java.lang.String value;

		private TYPE(final java.lang.String value) {
			this.value = value;
		}

		public static TYPE fromValue(final java.lang.String value) {
			for (TYPE c : TYPE.values()) {
				if (c.value.equals(value)) {
					return c;
				}
			}
			throw new IllegalArgumentException(value);
		}
	};

	private TYPE type;

	private Long id;

	private String objectId;

	public ObjectReference(Context context, Network object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.network;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);

	}

	public ObjectReference(Context context, Company object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.company;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, GroupOfLine object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.group_of_line;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, StopArea object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.stop_area;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, ConnectionLink object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.connection_link;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, AccessPoint object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.access_point;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, AccessLink object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.access_link;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, Timetable object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.time_table;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, Line object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.line;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, Route object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.route;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, JourneyPattern object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.journey_pattern;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}

	public ObjectReference(Context context, VehicleJourney object) {
		ChouetteIdGenerator chouetteIdGenerator = (ChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		AbstractParameter parameters = (AbstractParameter) context.get(PARAMETERS_FILE);
		
		this.type = TYPE.vehicle_journey;
		this.id = object.getId();
		if (id == null)
			this.objectId = chouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
	}
	
	public ObjectReference(StopPoint object)
	{
		this.type = TYPE.stop_point;
		this.id = object.getId();
	}

	public ObjectReference(String className, String id) {
		this.type = TYPE.fromValue(className);
		this.objectId = id;
	}

	@Override
	public void print(PrintStream out, StringBuilder ret, int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level + 1, "type", type, true));
		if (id != null)
			out.print(toJsonString(ret, level + 1, "id", id, false));
		else
			out.print(toJsonString(ret, level + 1, "objectId", objectId, false));

		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}

	public static boolean isEligible(String className, String objectId) {
		if (objectId == null || objectId.isEmpty())
			return false;
		try {
			TYPE.fromValue(className);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

}
