--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.11
-- Dumped by pg_dump version 9.1.11
-- Started on 2014-02-27 11:15:39 CET

-- authentification 127.0.0.1 trust
-- USAGE : psql -h 127.0.0.1 -U chouette -v SCH=chouette_gui  -d chouette_test -f chouette_test.sql'

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


SET default_tablespace = '';

SET default_with_oids = false;

CREATE SCHEMA IF NOT EXISTS shared_extensions;
CREATE EXTENSION IF NOT EXISTS postgis SCHEMA shared_extensions;

DROP SCHEMA IF EXISTS chouette_gui CASCADE;

CREATE SCHEMA chouette_gui ;

SET search_path = chouette_gui, pg_catalog;

--
-- TOC entry 174 (class 1259 OID 938851)
-- Name: access_links; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE access_links (
    id bigint NOT NULL,
    access_point_id bigint,
    stop_area_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    link_distance numeric(19,2),
    lift_availability boolean,
    mobility_restricted_suitability boolean,
    stairs_availability boolean,
    default_duration time without time zone,
    frequent_traveller_duration time without time zone,
    occasional_traveller_duration time without time zone,
    mobility_restricted_traveller_duration time without time zone,
    link_type character varying(255),
    int_user_needs integer,
    link_orientation character varying(255)
);


ALTER TABLE chouette_gui.access_links OWNER TO chouette;

--
-- TOC entry 175 (class 1259 OID 938857)
-- Name: access_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE access_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.access_links_id_seq OWNER TO chouette;

--
-- TOC entry 4252 (class 0 OID 0)
-- Dependencies: 175
-- Name: access_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE access_links_id_seq OWNED BY access_links.id;


--
-- TOC entry 176 (class 1259 OID 938859)
-- Name: access_points; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE access_points (
    id bigint NOT NULL,
    objectid character varying(255),
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    longitude numeric(19,16),
    latitude numeric(19,16),
    long_lat_type character varying(255),
    country_code character varying(255),
    street_name character varying(255),
    contained_in character varying(255),
    openning_time time without time zone,
    closing_time time without time zone,
    access_type character varying(255),
    lift_availability boolean,
    mobility_restricted_suitability boolean,
    stairs_availability boolean,
    stop_area_id bigint,
    zip_code character varying(255),
    city_name character varying(255)
);


ALTER TABLE chouette_gui.access_points OWNER TO chouette;

--
-- TOC entry 177 (class 1259 OID 938865)
-- Name: access_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE access_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.access_points_id_seq OWNER TO chouette;

--
-- TOC entry 4253 (class 0 OID 0)
-- Dependencies: 177
-- Name: access_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE access_points_id_seq OWNED BY access_points.id;



--
-- TOC entry 180 (class 1259 OID 938875)
-- Name: companies; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE companies (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    short_name character varying(255),
    organizational_unit character varying(255),
    operating_department_name character varying(255),
    code character varying(255),
    phone character varying(255),
    fax character varying(255),
    email character varying(255),
    registration_number character varying(255),
    url character varying(255),
    time_zone character varying(255)
);


ALTER TABLE chouette_gui.companies OWNER TO chouette;

--
-- TOC entry 181 (class 1259 OID 938881)
-- Name: companies_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE companies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.companies_id_seq OWNER TO chouette;

--
-- TOC entry 4255 (class 0 OID 0)
-- Dependencies: 181
-- Name: companies_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE companies_id_seq OWNED BY companies.id;


--
-- TOC entry 182 (class 1259 OID 938883)
-- Name: connection_links; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE connection_links (
    id bigint NOT NULL,
    departure_id bigint,
    arrival_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    link_distance numeric(19,2),
    link_type character varying(255),
    default_duration time without time zone,
    frequent_traveller_duration time without time zone,
    occasional_traveller_duration time without time zone,
    mobility_restricted_traveller_duration time without time zone,
    mobility_restricted_suitability boolean,
    stairs_availability boolean,
    lift_availability boolean,
    int_user_needs integer
);


ALTER TABLE chouette_gui.connection_links OWNER TO chouette;

--
-- TOC entry 183 (class 1259 OID 938889)
-- Name: connection_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE connection_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.connection_links_id_seq OWNER TO chouette;

--
-- TOC entry 4256 (class 0 OID 0)
-- Dependencies: 183
-- Name: connection_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE connection_links_id_seq OWNED BY connection_links.id;



--
-- TOC entry 188 (class 1259 OID 938909)
-- Name: facilities; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE facilities (
    id bigint NOT NULL,
    stop_area_id bigint,
    line_id bigint,
    connection_link_id bigint,
    stop_point_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    description character varying(255),
    free_access boolean,
    longitude numeric(19,16),
    latitude numeric(19,16),
    long_lat_type character varying(255),
    x numeric(19,2),
    y numeric(19,2),
    projection_type character varying(255),
    country_code character varying(255),
    street_name character varying(255),
    contained_in character varying(255)
);


ALTER TABLE chouette_gui.facilities OWNER TO chouette;

--
-- TOC entry 189 (class 1259 OID 938915)
-- Name: facilities_features; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE facilities_features (
    facility_id bigint,
    choice_code integer
);


ALTER TABLE chouette_gui.facilities_features OWNER TO chouette;

--
-- TOC entry 190 (class 1259 OID 938918)
-- Name: facilities_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.facilities_id_seq OWNER TO chouette;

--
-- TOC entry 4259 (class 0 OID 0)
-- Dependencies: 190
-- Name: facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE facilities_id_seq OWNED BY facilities.id;


--
-- TOC entry 191 (class 1259 OID 938920)
-- Name: footnotes; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE footnotes (
    id bigint NOT NULL,
    line_id bigint,
    code character varying(255),
    label character varying(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.footnotes OWNER TO chouette;

--
-- TOC entry 192 (class 1259 OID 938926)
-- Name: footnotes_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE footnotes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.footnotes_id_seq OWNER TO chouette;

--
-- TOC entry 4260 (class 0 OID 0)
-- Dependencies: 192
-- Name: footnotes_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE footnotes_id_seq OWNED BY footnotes.id;


--
-- TOC entry 193 (class 1259 OID 938928)
-- Name: footnotes_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE footnotes_vehicle_journeys (
    vehicle_journey_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_vehicle_journeys OWNER TO chouette;

--
-- TOC entry 194 (class 1259 OID 938931)
-- Name: group_of_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE group_of_lines (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    registration_number character varying(255)
);


ALTER TABLE chouette_gui.group_of_lines OWNER TO chouette;

--
-- TOC entry 195 (class 1259 OID 938938)
-- Name: group_of_lines_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE group_of_lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.group_of_lines_id_seq OWNER TO chouette;

--
-- TOC entry 4261 (class 0 OID 0)
-- Dependencies: 195
-- Name: group_of_lines_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE group_of_lines_id_seq OWNED BY group_of_lines.id;


--
-- TOC entry 196 (class 1259 OID 938940)
-- Name: group_of_lines_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE group_of_lines_lines (
    group_of_line_id bigint,
    line_id bigint
);


ALTER TABLE chouette_gui.group_of_lines_lines OWNER TO chouette;

--
-- TOC entry 378 (class 1259 OID 942346)
-- Name: journey_frequencies; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE journey_frequencies (
    id bigint NOT NULL,
    vehicle_journey_id integer,
    scheduled_headway_interval time without time zone NOT NULL,
    first_departure_time time without time zone NOT NULL,
    last_departure_time time without time zone,
    exact_time boolean DEFAULT false,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    timeband_id integer
);


ALTER TABLE chouette_gui.journey_frequencies OWNER TO chouette;

--
-- TOC entry 377 (class 1259 OID 942344)
-- Name: journey_frequencies_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE journey_frequencies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_frequencies_id_seq OWNER TO chouette;

--
-- TOC entry 4262 (class 0 OID 0)
-- Dependencies: 377
-- Name: journey_frequencies_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE journey_frequencies_id_seq OWNED BY journey_frequencies.id;


--
-- TOC entry 382 (class 1259 OID 942378)
-- Name: journey_pattern_sections; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE journey_pattern_sections (
    id bigint NOT NULL,
    journey_pattern_id integer NOT NULL,
    route_section_id integer NOT NULL,
    rank integer NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.journey_pattern_sections OWNER TO chouette;

--
-- TOC entry 381 (class 1259 OID 942376)
-- Name: journey_pattern_sections_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE journey_pattern_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_pattern_sections_id_seq OWNER TO chouette;

--
-- TOC entry 4263 (class 0 OID 0)
-- Dependencies: 381
-- Name: journey_pattern_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE journey_pattern_sections_id_seq OWNED BY journey_pattern_sections.id;


--
-- TOC entry 197 (class 1259 OID 938943)
-- Name: journey_patterns; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE journey_patterns (
    id bigint NOT NULL,
    route_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    registration_number character varying(255),
    published_name character varying(255),
    departure_stop_point_id bigint,
    arrival_stop_point_id bigint,
    section_status integer DEFAULT 0 NOT NULL
);


ALTER TABLE chouette_gui.journey_patterns OWNER TO chouette;

--
-- TOC entry 198 (class 1259 OID 938949)
-- Name: journey_patterns_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE journey_patterns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_patterns_id_seq OWNER TO chouette;

--
-- TOC entry 4264 (class 0 OID 0)
-- Dependencies: 198
-- Name: journey_patterns_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE journey_patterns_id_seq OWNED BY journey_patterns.id;


--
-- TOC entry 199 (class 1259 OID 938951)
-- Name: journey_patterns_stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE journey_patterns_stop_points (
    journey_pattern_id bigint,
    stop_point_id bigint
);


ALTER TABLE chouette_gui.journey_patterns_stop_points OWNER TO chouette;

--
-- TOC entry 200 (class 1259 OID 938954)
-- Name: lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE lines (
    id bigint NOT NULL,
    network_id bigint,
    company_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    number character varying(255),
    published_name character varying(255),
    transport_mode_name character varying(255),
    registration_number character varying(255),
    comment character varying(255),
    mobility_restricted_suitability boolean,
    int_user_needs integer,
    flexible_service boolean,
    url character varying(255),
    color character varying(6),
    text_color character varying(6),
    stable_id character varying(255)
);


ALTER TABLE chouette_gui.lines OWNER TO chouette;

--
-- TOC entry 201 (class 1259 OID 938960)
-- Name: lines_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.lines_id_seq OWNER TO chouette;

--
-- TOC entry 4265 (class 0 OID 0)
-- Dependencies: 201
-- Name: lines_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE lines_id_seq OWNED BY lines.id;


--
-- TOC entry 202 (class 1259 OID 938962)
-- Name: networks; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE networks (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    version_date date,
    description character varying(255),
    name character varying(255),
    registration_number character varying(255),
    source_name character varying(255),
    source_type character varying(255),
    source_identifier character varying(255),
    comment character varying(255)
);


ALTER TABLE chouette_gui.networks OWNER TO chouette;

--
-- TOC entry 203 (class 1259 OID 938968)
-- Name: networks_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE networks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.networks_id_seq OWNER TO chouette;

--
-- TOC entry 4266 (class 0 OID 0)
-- Dependencies: 203
-- Name: networks_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE networks_id_seq OWNED BY networks.id;



--
-- TOC entry 206 (class 1259 OID 938979)
-- Name: pt_links; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE pt_links (
    id bigint NOT NULL,
    start_of_link_id bigint,
    end_of_link_id bigint,
    route_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    link_distance numeric(19,2)
);


ALTER TABLE chouette_gui.pt_links OWNER TO chouette;

--
-- TOC entry 207 (class 1259 OID 938985)
-- Name: pt_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE pt_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.pt_links_id_seq OWNER TO chouette;

--
-- TOC entry 4268 (class 0 OID 0)
-- Dependencies: 207
-- Name: pt_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE pt_links_id_seq OWNED BY pt_links.id;


--
-- TOC entry 208 (class 1259 OID 938987)
-- Name: referentials; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE referentials (
    id bigint NOT NULL,
    name character varying(255),
    slug character varying(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    prefix character varying(255),
    projection_type character varying(255),
    time_zone character varying(255),
    bounds character varying(255),
    organisation_id bigint,
    geographical_bounds text,
    user_id bigint,
    user_name character varying(255),
    data_format character varying(255)
);


--
-- TOC entry 376 (class 1259 OID 942335)
-- Name: route_sections; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE route_sections (
    id bigint NOT NULL,
    departure_id integer,
    arrival_id integer,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    input_geometry shared_extensions.geometry(LineString,4326),
    processed_geometry shared_extensions.geometry(LineString,4326),
    distance double precision,
    no_processing boolean
);


ALTER TABLE chouette_gui.route_sections OWNER TO chouette;

--
-- TOC entry 375 (class 1259 OID 942333)
-- Name: route_sections_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE route_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.route_sections_id_seq OWNER TO chouette;

--
-- TOC entry 4270 (class 0 OID 0)
-- Dependencies: 375
-- Name: route_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE route_sections_id_seq OWNED BY route_sections.id;


--
-- TOC entry 210 (class 1259 OID 938995)
-- Name: routes; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE routes (
    id bigint NOT NULL,
    line_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    opposite_route_id bigint,
    published_name character varying(255),
    number character varying(255),
    direction character varying(255),
    wayback character varying(255)
);


ALTER TABLE chouette_gui.routes OWNER TO chouette;

--
-- TOC entry 211 (class 1259 OID 939001)
-- Name: routes_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE routes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.routes_id_seq OWNER TO chouette;

--
-- TOC entry 4271 (class 0 OID 0)
-- Dependencies: 211
-- Name: routes_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE routes_id_seq OWNED BY routes.id;


--
-- TOC entry 212 (class 1259 OID 939003)
-- Name: routing_constraints_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE routing_constraints_lines (
    stop_area_id bigint,
    line_id bigint
);


ALTER TABLE chouette_gui.routing_constraints_lines OWNER TO chouette;


--
-- TOC entry 216 (class 1259 OID 939017)
-- Name: stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE stop_areas (
    id bigint NOT NULL,
    parent_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    area_type character varying(255),
    registration_number character varying(255),
    nearest_topic_name character varying(255),
    fare_code integer,
    longitude numeric(19,16),
    latitude numeric(19,16),
    long_lat_type character varying(255),
    country_code character varying(255),
    street_name character varying(255),
    mobility_restricted_suitability boolean,
    stairs_availability boolean,
    lift_availability boolean,
    int_user_needs integer,
    zip_code character varying(255),
    city_name character varying(255),
    url character varying(255),
    time_zone character varying(255)
);


ALTER TABLE chouette_gui.stop_areas OWNER TO chouette;

--
-- TOC entry 217 (class 1259 OID 939023)
-- Name: stop_areas_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE stop_areas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.stop_areas_id_seq OWNER TO chouette;

--
-- TOC entry 4273 (class 0 OID 0)
-- Dependencies: 217
-- Name: stop_areas_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE stop_areas_id_seq OWNED BY stop_areas.id;


--
-- TOC entry 218 (class 1259 OID 939025)
-- Name: stop_areas_stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE stop_areas_stop_areas (
    child_id bigint,
    parent_id bigint
);


ALTER TABLE chouette_gui.stop_areas_stop_areas OWNER TO chouette;

--
-- TOC entry 219 (class 1259 OID 939028)
-- Name: stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE stop_points (
    id bigint NOT NULL,
    route_id bigint,
    stop_area_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    "position" integer,
    for_boarding character varying(255),
    for_alighting character varying(255)
);


ALTER TABLE chouette_gui.stop_points OWNER TO chouette;

--
-- TOC entry 220 (class 1259 OID 939034)
-- Name: stop_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.stop_points_id_seq OWNER TO chouette;

--
-- TOC entry 4274 (class 0 OID 0)
-- Dependencies: 220
-- Name: stop_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE stop_points_id_seq OWNED BY stop_points.id;


--
-- TOC entry 225 (class 1259 OID 939058)
-- Name: time_table_dates; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_table_dates (
    time_table_id bigint NOT NULL,
    date date,
    "position" integer NOT NULL,
    id bigint NOT NULL,
    in_out boolean
);


ALTER TABLE chouette_gui.time_table_dates OWNER TO chouette;

--
-- TOC entry 226 (class 1259 OID 939061)
-- Name: time_table_dates_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE time_table_dates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_table_dates_id_seq OWNER TO chouette;

--
-- TOC entry 4277 (class 0 OID 0)
-- Dependencies: 226
-- Name: time_table_dates_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_table_dates_id_seq OWNED BY time_table_dates.id;


--
-- TOC entry 227 (class 1259 OID 939063)
-- Name: time_table_periods; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_table_periods (
    time_table_id bigint NOT NULL,
    period_start date,
    period_end date,
    "position" integer NOT NULL,
    id bigint NOT NULL
);


ALTER TABLE chouette_gui.time_table_periods OWNER TO chouette;

--
-- TOC entry 228 (class 1259 OID 939066)
-- Name: time_table_periods_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE time_table_periods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_table_periods_id_seq OWNER TO chouette;

--
-- TOC entry 4278 (class 0 OID 0)
-- Dependencies: 228
-- Name: time_table_periods_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_table_periods_id_seq OWNED BY time_table_periods.id;


--
-- TOC entry 229 (class 1259 OID 939068)
-- Name: time_tables; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_tables (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer DEFAULT 1,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    version character varying(255),
    comment character varying(255),
    int_day_types integer DEFAULT 0,
    start_date date,
    end_date date
);


ALTER TABLE chouette_gui.time_tables OWNER TO chouette;

--
-- TOC entry 230 (class 1259 OID 939076)
-- Name: time_tables_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE time_tables_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_tables_id_seq OWNER TO chouette;

--
-- TOC entry 4279 (class 0 OID 0)
-- Dependencies: 230
-- Name: time_tables_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_tables_id_seq OWNED BY time_tables.id;


--
-- TOC entry 231 (class 1259 OID 939078)
-- Name: time_tables_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_tables_vehicle_journeys (
    time_table_id bigint,
    vehicle_journey_id bigint
);


ALTER TABLE chouette_gui.time_tables_vehicle_journeys OWNER TO chouette;

--
-- TOC entry 380 (class 1259 OID 942366)
-- Name: timebands; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE timebands (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.timebands OWNER TO chouette;

--
-- TOC entry 379 (class 1259 OID 942364)
-- Name: timebands_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE timebands_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.timebands_id_seq OWNER TO chouette;

--
-- TOC entry 4280 (class 0 OID 0)
-- Dependencies: 379
-- Name: timebands_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE timebands_id_seq OWNED BY timebands.id;


--
-- TOC entry 234 (class 1259 OID 939093)
-- Name: vehicle_journey_at_stops; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE vehicle_journey_at_stops (
    id bigint NOT NULL,
    vehicle_journey_id bigint,
    stop_point_id bigint,
    connecting_service_id character varying(255),
    boarding_alighting_possibility character varying(255),
    arrival_time time without time zone,
    departure_time time without time zone,
    for_boarding character varying(255),
    for_alighting character varying(255),
    departure_day_offset int not null default 0,
    arrival_day_offset int not null default 0
);


ALTER TABLE chouette_gui.vehicle_journey_at_stops OWNER TO chouette;

--
-- TOC entry 235 (class 1259 OID 939099)
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE vehicle_journey_at_stops_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.vehicle_journey_at_stops_id_seq OWNER TO chouette;

--
-- TOC entry 4282 (class 0 OID 0)
-- Dependencies: 235
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE vehicle_journey_at_stops_id_seq OWNED BY vehicle_journey_at_stops.id;


--
-- TOC entry 236 (class 1259 OID 939101)
-- Name: vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE vehicle_journeys (
    id bigint NOT NULL,
    route_id bigint,
    journey_pattern_id bigint,
    company_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    comment character varying(255),
    status_value character varying(255),
    transport_mode character varying(255),
    published_journey_name character varying(255),
    published_journey_identifier character varying(255),
    facility character varying(255),
    vehicle_type_identifier character varying(255),
    number bigint,
    mobility_restricted_suitability boolean,
    flexible_service boolean,
    journey_category integer DEFAULT 0 NOT NULL
);


ALTER TABLE chouette_gui.vehicle_journeys OWNER TO chouette;

--
-- TOC entry 237 (class 1259 OID 939107)
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE vehicle_journeys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.vehicle_journeys_id_seq OWNER TO chouette;

--
-- TOC entry 4283 (class 0 OID 0)
-- Dependencies: 237
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE vehicle_journeys_id_seq OWNED BY vehicle_journeys.id;


--
-- TOC entry 3948 (class 2604 OID 939607)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links ALTER COLUMN id SET DEFAULT nextval('access_links_id_seq'::regclass);


--
-- TOC entry 3949 (class 2604 OID 939608)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_points ALTER COLUMN id SET DEFAULT nextval('access_points_id_seq'::regclass);



--
-- TOC entry 3951 (class 2604 OID 939610)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY companies ALTER COLUMN id SET DEFAULT nextval('companies_id_seq'::regclass);


--
-- TOC entry 3952 (class 2604 OID 939611)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links ALTER COLUMN id SET DEFAULT nextval('connection_links_id_seq'::regclass);


--
-- TOC entry 3957 (class 2604 OID 939614)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY facilities ALTER COLUMN id SET DEFAULT nextval('facilities_id_seq'::regclass);


--
-- TOC entry 3958 (class 2604 OID 939615)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY footnotes ALTER COLUMN id SET DEFAULT nextval('footnotes_id_seq'::regclass);


--
-- TOC entry 3959 (class 2604 OID 939616)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines ALTER COLUMN id SET DEFAULT nextval('group_of_lines_id_seq'::regclass);


--
-- TOC entry 3989 (class 2604 OID 942349)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_frequencies ALTER COLUMN id SET DEFAULT nextval('journey_frequencies_id_seq'::regclass);


--
-- TOC entry 3992 (class 2604 OID 942381)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_pattern_sections ALTER COLUMN id SET DEFAULT nextval('journey_pattern_sections_id_seq'::regclass);


--
-- TOC entry 3960 (class 2604 OID 939617)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns ALTER COLUMN id SET DEFAULT nextval('journey_patterns_id_seq'::regclass);


--
-- TOC entry 3962 (class 2604 OID 939618)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines ALTER COLUMN id SET DEFAULT nextval('lines_id_seq'::regclass);


--
-- TOC entry 3963 (class 2604 OID 939619)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY networks ALTER COLUMN id SET DEFAULT nextval('networks_id_seq'::regclass);


--
-- TOC entry 3966 (class 2604 OID 939621)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY pt_links ALTER COLUMN id SET DEFAULT nextval('pt_links_id_seq'::regclass);



--
-- TOC entry 3988 (class 2604 OID 942338)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY route_sections ALTER COLUMN id SET DEFAULT nextval('route_sections_id_seq'::regclass);


--
-- TOC entry 3968 (class 2604 OID 939623)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routes ALTER COLUMN id SET DEFAULT nextval('routes_id_seq'::regclass);



--
-- TOC entry 3970 (class 2604 OID 939625)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas ALTER COLUMN id SET DEFAULT nextval('stop_areas_id_seq'::regclass);


--
-- TOC entry 3971 (class 2604 OID 939626)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points ALTER COLUMN id SET DEFAULT nextval('stop_points_id_seq'::regclass);



--
-- TOC entry 3975 (class 2604 OID 939630)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_dates ALTER COLUMN id SET DEFAULT nextval('time_table_dates_id_seq'::regclass);


--
-- TOC entry 3976 (class 2604 OID 939631)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_periods ALTER COLUMN id SET DEFAULT nextval('time_table_periods_id_seq'::regclass);


--
-- TOC entry 3979 (class 2604 OID 939632)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables ALTER COLUMN id SET DEFAULT nextval('time_tables_id_seq'::regclass);


--
-- TOC entry 3991 (class 2604 OID 942369)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY timebands ALTER COLUMN id SET DEFAULT nextval('timebands_id_seq'::regclass);


--
-- TOC entry 3985 (class 2604 OID 939634)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops ALTER COLUMN id SET DEFAULT nextval('vehicle_journey_at_stops_id_seq'::regclass);


--
-- TOC entry 3986 (class 2604 OID 939635)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys ALTER COLUMN id SET DEFAULT nextval('vehicle_journeys_id_seq'::regclass);


--
-- TOC entry 3995 (class 2606 OID 939693)
-- Name: access_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT access_links_pkey PRIMARY KEY (id);


--
-- TOC entry 3998 (class 2606 OID 939695)
-- Name: access_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY access_points
    ADD CONSTRAINT access_points_pkey PRIMARY KEY (id);



--
-- TOC entry 4003 (class 2606 OID 939699)
-- Name: companies_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (id);


--
-- TOC entry 4007 (class 2606 OID 939701)
-- Name: connection_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT connection_links_pkey PRIMARY KEY (id);


--
-- TOC entry 4016 (class 2606 OID 939707)
-- Name: facilities_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- TOC entry 4018 (class 2606 OID 939709)
-- Name: footnotes_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY footnotes
    ADD CONSTRAINT footnotes_pkey PRIMARY KEY (id);


--
-- TOC entry 4021 (class 2606 OID 939711)
-- Name: group_of_lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY group_of_lines
    ADD CONSTRAINT group_of_lines_pkey PRIMARY KEY (id);


--
-- TOC entry 4090 (class 2606 OID 942352)
-- Name: journey_frequencies_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY journey_frequencies
    ADD CONSTRAINT journey_frequencies_pkey PRIMARY KEY (id);


--
-- TOC entry 4097 (class 2606 OID 942383)
-- Name: journey_pattern_sections_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY journey_pattern_sections
    ADD CONSTRAINT journey_pattern_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 4024 (class 2606 OID 939713)
-- Name: journey_patterns_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT journey_patterns_pkey PRIMARY KEY (id);


--
-- TOC entry 4028 (class 2606 OID 939715)
-- Name: lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT lines_pkey PRIMARY KEY (id);


--
-- TOC entry 4032 (class 2606 OID 939717)
-- Name: networks_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY networks
    ADD CONSTRAINT networks_pkey PRIMARY KEY (id);



--
-- TOC entry 4038 (class 2606 OID 939721)
-- Name: pt_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY pt_links
    ADD CONSTRAINT pt_links_pkey PRIMARY KEY (id);



--
-- TOC entry 4086 (class 2606 OID 942343)
-- Name: route_sections_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY route_sections
    ADD CONSTRAINT route_sections_pkey PRIMARY KEY (id);


--
-- TOC entry 4043 (class 2606 OID 939725)
-- Name: routes_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (id);


--
-- TOC entry 4050 (class 2606 OID 939729)
-- Name: stop_areas_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY stop_areas
    ADD CONSTRAINT stop_areas_pkey PRIMARY KEY (id);


--
-- TOC entry 4053 (class 2606 OID 939731)
-- Name: stop_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stop_points_pkey PRIMARY KEY (id);



--
-- TOC entry 4063 (class 2606 OID 939739)
-- Name: time_table_dates_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_table_dates
    ADD CONSTRAINT time_table_dates_pkey PRIMARY KEY (id);


--
-- TOC entry 4066 (class 2606 OID 939741)
-- Name: time_table_periods_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_table_periods
    ADD CONSTRAINT time_table_periods_pkey PRIMARY KEY (id);


--
-- TOC entry 4069 (class 2606 OID 939743)
-- Name: time_tables_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_tables
    ADD CONSTRAINT time_tables_pkey PRIMARY KEY (id);


--
-- TOC entry 4092 (class 2606 OID 942374)
-- Name: timebands_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY timebands
    ADD CONSTRAINT timebands_pkey PRIMARY KEY (id);


--
-- TOC entry 4080 (class 2606 OID 939747)
-- Name: vehicle_journey_at_stops_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vehicle_journey_at_stops_pkey PRIMARY KEY (id);


--
-- TOC entry 4084 (class 2606 OID 939749)
-- Name: vehicle_journeys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_pkey PRIMARY KEY (id);


--
-- TOC entry 3993 (class 1259 OID 939862)
-- Name: access_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX access_links_objectid_key ON access_links USING btree (objectid);


--
-- TOC entry 3996 (class 1259 OID 939863)
-- Name: access_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX access_points_objectid_key ON access_points USING btree (objectid);


--
-- TOC entry 4001 (class 1259 OID 939864)
-- Name: companies_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX companies_objectid_key ON companies USING btree (objectid);


--
-- TOC entry 4004 (class 1259 OID 939865)
-- Name: companies_registration_number_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX companies_registration_number_key ON companies USING btree (registration_number);


--
-- TOC entry 4005 (class 1259 OID 939866)
-- Name: connection_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX connection_links_objectid_key ON connection_links USING btree (objectid);



--
-- TOC entry 4014 (class 1259 OID 939868)
-- Name: facilities_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX facilities_objectid_key ON facilities USING btree (objectid);


--
-- TOC entry 4019 (class 1259 OID 939869)
-- Name: group_of_lines_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX group_of_lines_objectid_key ON group_of_lines USING btree (objectid);



--
-- TOC entry 4087 (class 1259 OID 942375)
-- Name: index_journey_frequencies_on_timeband_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_frequencies_on_timeband_id ON journey_frequencies USING btree (timeband_id);


--
-- TOC entry 4088 (class 1259 OID 942353)
-- Name: index_journey_frequencies_on_vehicle_journey_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_frequencies_on_vehicle_journey_id ON journey_frequencies USING btree (vehicle_journey_id);


--
-- TOC entry 4025 (class 1259 OID 939871)
-- Name: index_journey_pattern_id_on_journey_patterns_stop_points; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_pattern_id_on_journey_patterns_stop_points ON journey_patterns_stop_points USING btree (journey_pattern_id);


--
-- TOC entry 4093 (class 1259 OID 942384)
-- Name: index_journey_pattern_sections_on_journey_pattern_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_pattern_sections_on_journey_pattern_id ON journey_pattern_sections USING btree (journey_pattern_id);


--
-- TOC entry 4094 (class 1259 OID 942385)
-- Name: index_journey_pattern_sections_on_route_section_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_pattern_sections_on_route_section_id ON journey_pattern_sections USING btree (route_section_id);


--
-- TOC entry 4095 (class 1259 OID 942396)
-- Name: index_jps_on_journey_pattern_id_and_route_section_id_and_rank; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX index_jps_on_journey_pattern_id_and_route_section_id_and_rank ON journey_pattern_sections USING btree (journey_pattern_id, route_section_id, rank);


--
-- TOC entry 4047 (class 1259 OID 939872)
-- Name: index_stop_areas_on_parent_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_stop_areas_on_parent_id ON stop_areas USING btree (parent_id);



--
-- TOC entry 4061 (class 1259 OID 939875)
-- Name: index_time_table_dates_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_table_dates_on_time_table_id ON time_table_dates USING btree (time_table_id);


--
-- TOC entry 4064 (class 1259 OID 939876)
-- Name: index_time_table_periods_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_table_periods_on_time_table_id ON time_table_periods USING btree (time_table_id);


--
-- TOC entry 4070 (class 1259 OID 939877)
-- Name: index_time_tables_vehicle_journeys_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_tables_vehicle_journeys_on_time_table_id ON time_tables_vehicle_journeys USING btree (time_table_id);


--
-- TOC entry 4071 (class 1259 OID 939878)
-- Name: index_time_tables_vehicle_journeys_on_vehicle_journey_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_tables_vehicle_journeys_on_vehicle_journey_id ON time_tables_vehicle_journeys USING btree (vehicle_journey_id);


--
-- TOC entry 4077 (class 1259 OID 939882)
-- Name: index_vehicle_journey_at_stops_on_stop_pointid; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journey_at_stops_on_stop_pointid ON vehicle_journey_at_stops USING btree (stop_point_id);


--
-- TOC entry 4078 (class 1259 OID 939883)
-- Name: index_vehicle_journey_at_stops_on_vehicle_journey_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journey_at_stops_on_vehicle_journey_id ON vehicle_journey_at_stops USING btree (vehicle_journey_id);


--
-- TOC entry 4081 (class 1259 OID 939884)
-- Name: index_vehicle_journeys_on_route_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journeys_on_route_id ON vehicle_journeys USING btree (route_id);


--
-- TOC entry 4022 (class 1259 OID 939885)
-- Name: journey_patterns_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX journey_patterns_objectid_key ON journey_patterns USING btree (objectid);


--
-- TOC entry 4026 (class 1259 OID 939886)
-- Name: lines_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX lines_objectid_key ON lines USING btree (objectid);


--
-- TOC entry 4029 (class 1259 OID 939887)
-- Name: lines_registration_number_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX lines_registration_number_key ON lines USING btree (registration_number);


--
-- TOC entry 4030 (class 1259 OID 939888)
-- Name: networks_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX networks_objectid_key ON networks USING btree (objectid);


--
-- TOC entry 4033 (class 1259 OID 939889)
-- Name: networks_registration_number_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX networks_registration_number_key ON networks USING btree (registration_number);


--
-- TOC entry 4036 (class 1259 OID 939890)
-- Name: pt_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX pt_links_objectid_key ON pt_links USING btree (objectid);


--
-- TOC entry 4041 (class 1259 OID 939891)
-- Name: routes_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX routes_objectid_key ON routes USING btree (objectid);


--
-- TOC entry 4048 (class 1259 OID 939892)
-- Name: stop_areas_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX stop_areas_objectid_key ON stop_areas USING btree (objectid);


--
-- TOC entry 4051 (class 1259 OID 939893)
-- Name: stop_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX stop_points_objectid_key ON stop_points USING btree (objectid);


--
-- TOC entry 4067 (class 1259 OID 939896)
-- Name: time_tables_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX time_tables_objectid_key ON time_tables USING btree (objectid);



--
-- TOC entry 4082 (class 1259 OID 939898)
-- Name: vehicle_journeys_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX vehicle_journeys_objectid_key ON vehicle_journeys USING btree (objectid);


--
-- TOC entry 4100 (class 2606 OID 939971)
-- Name: access_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_points
    ADD CONSTRAINT access_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4099 (class 2606 OID 939976)
-- Name: aclk_acpt_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (access_point_id) REFERENCES access_points(id) ON DELETE CASCADE;


--
-- TOC entry 4098 (class 2606 OID 939981)
-- Name: aclk_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4116 (class 2606 OID 939986)
-- Name: area_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas
    ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parent_id) REFERENCES stop_areas(id) ON DELETE SET NULL;


--
-- TOC entry 4107 (class 2606 OID 939991)
-- Name: arrival_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id) REFERENCES stop_points(id) ON DELETE SET NULL;


--
-- TOC entry 4102 (class 2606 OID 939996)
-- Name: colk_endarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrival_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4101 (class 2606 OID 940001)
-- Name: colk_startarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departure_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4106 (class 2606 OID 940006)
-- Name: departure_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id) REFERENCES stop_points(id) ON DELETE SET NULL;


--
-- TOC entry 4104 (class 2606 OID 940011)
-- Name: groupofline_group_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines_lines
    ADD CONSTRAINT groupofline_group_fkey FOREIGN KEY (group_of_line_id) REFERENCES group_of_lines(id) ON DELETE CASCADE;


--
-- TOC entry 4103 (class 2606 OID 940016)
-- Name: groupofline_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines_lines
    ADD CONSTRAINT groupofline_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 4131 (class 2606 OID 942386)
-- Name: journey_pattern_sections_journey_pattern_id_fk; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_pattern_sections
    ADD CONSTRAINT journey_pattern_sections_journey_pattern_id_fk FOREIGN KEY (journey_pattern_id) REFERENCES journey_patterns(id) ON DELETE CASCADE;


--
-- TOC entry 4130 (class 2606 OID 942391)
-- Name: journey_pattern_sections_route_section_id_fk; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_pattern_sections
    ADD CONSTRAINT journey_pattern_sections_route_section_id_fk FOREIGN KEY (route_section_id) REFERENCES route_sections(id) ON DELETE CASCADE;


--
-- TOC entry 4105 (class 2606 OID 940021)
-- Name: jp_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT jp_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 4109 (class 2606 OID 940026)
-- Name: jpsp_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns_stop_points
    ADD CONSTRAINT jpsp_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES journey_patterns(id) ON DELETE CASCADE;


--
-- TOC entry 4108 (class 2606 OID 940031)
-- Name: jpsp_stoppoint_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns_stop_points
    ADD CONSTRAINT jpsp_stoppoint_fkey FOREIGN KEY (stop_point_id) REFERENCES stop_points(id) ON DELETE CASCADE;


--
-- TOC entry 4111 (class 2606 OID 940036)
-- Name: line_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_company_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;


--
-- TOC entry 4110 (class 2606 OID 940041)
-- Name: line_ptnetwork_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (network_id) REFERENCES networks(id) ON DELETE SET NULL;


--
-- TOC entry 4113 (class 2606 OID 940046)
-- Name: route_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routes
    ADD CONSTRAINT route_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 4112 (class 2606 OID 940051)
-- Name: route_opposite_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routes
    ADD CONSTRAINT route_opposite_route_fkey FOREIGN KEY (opposite_route_id) REFERENCES routes(id) ON DELETE SET NULL;


--
-- TOC entry 4115 (class 2606 OID 940056)
-- Name: routingconstraint_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routing_constraints_lines
    ADD CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 4114 (class 2606 OID 940061)
-- Name: routingconstraint_stoparea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routing_constraints_lines
    ADD CONSTRAINT routingconstraint_stoparea_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4118 (class 2606 OID 940066)
-- Name: stoparea_child_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas_stop_areas
    ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (child_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4117 (class 2606 OID 940071)
-- Name: stoparea_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas_stop_areas
    ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parent_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4120 (class 2606 OID 940076)
-- Name: stoppoint_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id);


--
-- TOC entry 4119 (class 2606 OID 940081)
-- Name: stoppoint_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 4121 (class 2606 OID 940086)
-- Name: tm_date_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_dates
    ADD CONSTRAINT tm_date_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 4122 (class 2606 OID 940091)
-- Name: tm_period_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_periods
    ADD CONSTRAINT tm_period_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 4129 (class 2606 OID 940096)
-- Name: vj_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_company_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;


--
-- TOC entry 4128 (class 2606 OID 940101)
-- Name: vj_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES journey_patterns(id) ON DELETE CASCADE;


--
-- TOC entry 4127 (class 2606 OID 940106)
-- Name: vj_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 4126 (class 2606 OID 940111)
-- Name: vjas_sp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stop_point_id) REFERENCES stop_points(id) ON DELETE CASCADE;


--
-- TOC entry 4125 (class 2606 OID 940116)
-- Name: vjas_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES vehicle_journeys(id) ON DELETE CASCADE;


--
-- TOC entry 4124 (class 2606 OID 940121)
-- Name: vjtm_tm_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_tm_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 4123 (class 2606 OID 940126)
-- Name: vjtm_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES vehicle_journeys(id) ON DELETE CASCADE;


--
-- TOC entry 4251 (class 0 OID 0)
-- Dependencies: 8
-- Name: chouette_gui; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA chouette_gui TO chouette;
GRANT ALL ON SCHEMA chouette_gui TO PUBLIC;


-- Completed on 2016-01-04 11:09:57 CET

--
-- PostgreSQL database dump complete
--


