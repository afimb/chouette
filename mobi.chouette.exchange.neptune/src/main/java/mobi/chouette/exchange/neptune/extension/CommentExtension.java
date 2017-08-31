package mobi.chouette.exchange.neptune.extension;

import java.net.URL;
import java.util.TimeZone;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

@Log4j
public class CommentExtension implements JsonExtension {

	public void parseJsonComment(Line line) {
		String comment = line.getComment();
		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
			try {
				// parse json comment
				JSONObject json = new JSONObject(comment);
				line.setComment(json.optString(COMMENT, null));
				if (json.has(FOOTNOTES)) {
					// scan footnotes
					JSONArray footNotes = json.getJSONArray(FOOTNOTES);
					for (int i = 0; i < footNotes.length(); i++) {
						JSONObject footNote = footNotes.getJSONObject(i);
						String key = footNote.optString(KEY, null);
						String code = footNote.optString(CODE, null);
						String label = footNote.optString(LABEL, null);
						if (key != null && code != null && label != null) {
							Footnote note = new Footnote();
							note.setLine(line);
							note.setLabel(label);
							note.setCode(code);
							note.setKey(key);
							line.getFootnotes().add(note);
						}

					}
				}
				if (json.has(FLEXIBLE_SERVICE)) {
					line.setFlexibleService(Boolean.valueOf(json.getBoolean(FLEXIBLE_SERVICE)));
				}
				if (json.has(TEXT_COLOR)) {
					try {
						java.awt.Color.decode("0x" + json.getString(TEXT_COLOR));
						line.setTextColor(json.getString(TEXT_COLOR));
					} catch (Exception e) {
						log.error("Line extension : cannot parse text color " + json.getString(TEXT_COLOR), e);
					}
				}
				if (json.has(LINE_COLOR)) {
					try {
						java.awt.Color.decode("0x" + json.getString(LINE_COLOR));
						line.setColor(json.getString(LINE_COLOR));
					} catch (Exception e) {
						log.error("Line extension : cannot parse color " + json.getString(LINE_COLOR), e);
					}
				}
				if (json.has(URL_REF)) {
					try {
						new URL(json.getString(URL_REF));
						line.setUrl(json.getString(URL_REF));
					} catch (Exception e) {
						log.error("Line extension : cannot parse url " + json.getString(URL_REF), e);
					}
				}
			} catch (Exception e1) {
				log.warn("Line extension : unparsable json : " + comment);
				line.setComment(comment);
			}

		}

	}

	public void parseJsonComment(StopArea area) {
		String comment = area.getComment();
		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
			try {
				// parse json comment
				JSONObject json = new JSONObject(comment);
				area.setComment(json.optString(COMMENT, null));
				if (json.has(URL_REF)) {
					try {
						new URL(json.getString(URL_REF));
						area.setUrl(json.getString(URL_REF));
					} catch (Exception e) {
						log.error("StopArea extension : cannot parse url " + json.getString(URL_REF), e);
					}
				}
				if (json.has(TIME_ZONE)) {
					try {
						TimeZone.getTimeZone(json.getString(TIME_ZONE));
						area.setTimeZone(json.getString(TIME_ZONE));
					} catch (Exception e) {
						log.error("StopArea extension : cannot parse time_zone " + json.getString(TIME_ZONE), e);
					}
				}
				if (json.has(ZIP_CODE)) {
					area.setZipCode(json.getString(ZIP_CODE));
				}
				if (json.has(CITY_NAME)) {
					area.setCityName(json.getString(CITY_NAME));
				}
			} catch (Exception e1) {
				log.warn("StopArea extension : unparsable json : " + comment);
				area.setComment(comment);
			}
		}
	}

	public void parseJsonComment(GroupOfLine groupOfLine) {
		String comment = groupOfLine.getComment();
		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
			try {
				// parse json comment
				JSONObject json = new JSONObject(comment);
				groupOfLine.setComment(json.optString(COMMENT, null));
				if (json.has(REGISTRATION_NUMBER)) {
					groupOfLine.setRegistrationNumber(json.getString(REGISTRATION_NUMBER));
				}
			} catch (Exception e1) {
				log.warn("GroupOfLine extension : unparsable json : " + comment);
				groupOfLine.setComment(comment);
			}
		}
	}

	public void parseJsonComment(StopPoint point) {
		String comment = point.getComment();
		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
			try {
				point.setComment(null);
				// parse json comment
				JSONObject json = new JSONObject(comment);
				if (json.has(ROUTING_CONSTRAINTS)) {
					JSONObject rc = json.getJSONObject(ROUTING_CONSTRAINTS);
					if (rc.has(BOARDING)) {
						try {
							BoardingPossibilityEnum forBoarding = BoardingPossibilityEnum.valueOf(rc
									.getString(BOARDING));
							point.setForBoarding(forBoarding);
						} catch (IllegalArgumentException e) {
							log.error("StopPoint extension : unknown value " + rc.getString(BOARDING) + " for boarding");
						}
					}
					if (rc.has(ALIGHTING)) {
						try {
							AlightingPossibilityEnum forAlighting = AlightingPossibilityEnum.valueOf(rc
									.getString(ALIGHTING));
							point.setForAlighting(forAlighting);
						} catch (IllegalArgumentException e) {
							log.error("StopPoint extension : unknown value " + rc.getString(ALIGHTING)
									+ " for alighting");
						}
					}
				}
			} catch (Exception e1) {
				log.warn("StopPoint extension : unparsable json : " + comment);
			}
		}
	}

	public void parseJsonComment(VehicleJourney vj) {
		String comment = vj.getComment();
		if (comment != null && comment.trim().startsWith("{") && comment.trim().endsWith("}")) {
			// parse json comment
			try {
				Line line = vj.getRoute().getLine();
				JSONObject json = new JSONObject(comment);
				vj.setComment(json.optString(COMMENT, null));

				if (json.has(FOOTNOTE_REFS)) {
					JSONArray keys = json.getJSONArray(FOOTNOTE_REFS);
					for (int i = 0; i < keys.length(); i++) {
						String key = keys.getString(i);
						for (Footnote footnote : line.getFootnotes()) {
							if (footnote.getKey().equals(key)) {
								vj.getFootnotes().add(footnote);
							}
						}
					}
				}
				if (json.has(FLEXIBLE_SERVICE)) {
					vj.setFlexibleService(json.getBoolean(FLEXIBLE_SERVICE));
				}
				if (json.has(MOBILITY_RESTRICTION)) {
					vj.setMobilityRestrictedSuitability(json.getBoolean(MOBILITY_RESTRICTION));
				}
			} catch (Exception e) {
				log.warn("VehicleJourney extension : unparsable json : " + comment+ ", reason "+e.getMessage());
				vj.setComment(comment);
			}
		}

	}

}
