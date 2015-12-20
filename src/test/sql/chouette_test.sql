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

DROP SCHEMA IF EXISTS chouette_gui CASCADE;

CREATE SCHEMA chouette_gui ;

SET search_path = chouette_gui, pg_catalog;

--
-- TOC entry 201 (class 1259 OID 480177)
-- Dependencies: 5
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
-- TOC entry 200 (class 1259 OID 480175)
-- Dependencies: 5 201
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
-- TOC entry 2695 (class 0 OID 0)
-- Dependencies: 200
-- Name: access_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE access_links_id_seq OWNED BY access_links.id;


--
-- TOC entry 199 (class 1259 OID 480165)
-- Dependencies: 5
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
-- TOC entry 198 (class 1259 OID 480163)
-- Dependencies: 5 199
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
-- TOC entry 2696 (class 0 OID 0)
-- Dependencies: 198
-- Name: access_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE access_points_id_seq OWNED BY access_points.id;


--
-- TOC entry 169 (class 1259 OID 479970)
-- Dependencies: 5
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
-- TOC entry 168 (class 1259 OID 479968)
-- Dependencies: 169 5
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
-- TOC entry 2698 (class 0 OID 0)
-- Dependencies: 168
-- Name: companies_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE companies_id_seq OWNED BY companies.id;



--
-- TOC entry 185 (class 1259 OID 480071)
-- Dependencies: 5
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
-- TOC entry 184 (class 1259 OID 480069)
-- Dependencies: 5 185
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
-- TOC entry 2701 (class 0 OID 0)
-- Dependencies: 184
-- Name: connection_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE connection_links_id_seq OWNED BY connection_links.id;





--
-- TOC entry 203 (class 1259 OID 480189)
-- Dependencies: 5
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
-- TOC entry 204 (class 1259 OID 480199)
-- Dependencies: 5
-- Name: facilities_features; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE facilities_features (
    facility_id bigint,
    choice_code integer
);


ALTER TABLE chouette_gui.facilities_features OWNER TO chouette;

--
-- TOC entry 202 (class 1259 OID 480187)
-- Dependencies: 203 5
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
-- TOC entry 2705 (class 0 OID 0)
-- Dependencies: 202
-- Name: facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE facilities_id_seq OWNED BY facilities.id;


CREATE TABLE footnotes
(
  id bigserial NOT NULL,
  line_id bigint,
  code character varying(255),
  label character varying(255),
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL,
  CONSTRAINT footnotes_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE footnotes
  OWNER TO chouette;

CREATE TABLE footnotes_vehicle_journeys
(
  vehicle_journey_id bigint,
  footnote_id bigint
)
WITH (
  OIDS=FALSE
);
ALTER TABLE footnotes_vehicle_journeys
  OWNER TO chouette;

--
-- TOC entry 206 (class 1259 OID 480204)
-- Dependencies: 5
-- Name: group_of_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE group_of_lines (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    registration_number character varying(255),
    comment character varying(255)
);


ALTER TABLE chouette_gui.group_of_lines OWNER TO chouette;

--
-- TOC entry 205 (class 1259 OID 480202)
-- Dependencies: 5 206
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
-- TOC entry 2706 (class 0 OID 0)
-- Dependencies: 205
-- Name: group_of_lines_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE group_of_lines_id_seq OWNED BY group_of_lines.id;


--
-- TOC entry 207 (class 1259 OID 480214)
-- Dependencies: 5
-- Name: group_of_lines_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE group_of_lines_lines (
    group_of_line_id bigint,
    line_id bigint
);


ALTER TABLE chouette_gui.group_of_lines_lines OWNER TO chouette;


--
-- TOC entry 189 (class 1259 OID 480106)
-- Dependencies: 5
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
    arrival_stop_point_id bigint
);


ALTER TABLE chouette_gui.journey_patterns OWNER TO chouette;

--
-- TOC entry 188 (class 1259 OID 480104)
-- Dependencies: 189 5
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
-- TOC entry 2708 (class 0 OID 0)
-- Dependencies: 188
-- Name: journey_patterns_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE journey_patterns_id_seq OWNED BY journey_patterns.id;


--
-- TOC entry 190 (class 1259 OID 480116)
-- Dependencies: 5
-- Name: journey_patterns_stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE journey_patterns_stop_points (
    journey_pattern_id bigint,
    stop_point_id bigint
);


ALTER TABLE chouette_gui.journey_patterns_stop_points OWNER TO chouette;

--
-- TOC entry 167 (class 1259 OID 479957)
-- Dependencies: 5
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
    flexible_service boolean,
    int_user_needs integer,
    url character varying(255),
    color character varying(6),
    text_color character varying(6),
    stable_id character varying(255)
);


ALTER TABLE chouette_gui.lines OWNER TO chouette;

--
-- TOC entry 166 (class 1259 OID 479955)
-- Dependencies: 5 167
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
-- TOC entry 2709 (class 0 OID 0)
-- Dependencies: 166
-- Name: lines_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE lines_id_seq OWNED BY lines.id;


--
-- TOC entry 171 (class 1259 OID 479983)
-- Dependencies: 5
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
-- TOC entry 170 (class 1259 OID 479981)
-- Dependencies: 5 171
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
-- TOC entry 2710 (class 0 OID 0)
-- Dependencies: 170
-- Name: networks_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE networks_id_seq OWNED BY networks.id;



--
-- TOC entry 211 (class 1259 OID 480244)
-- Dependencies: 5
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
-- TOC entry 210 (class 1259 OID 480242)
-- Dependencies: 5 211
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
-- TOC entry 2712 (class 0 OID 0)
-- Dependencies: 210
-- Name: pt_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE pt_links_id_seq OWNED BY pt_links.id;



--
-- TOC entry 181 (class 1259 OID 480047)
-- Dependencies: 5
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
-- TOC entry 180 (class 1259 OID 480045)
-- Dependencies: 5 181
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
-- TOC entry 2714 (class 0 OID 0)
-- Dependencies: 180
-- Name: routes_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE routes_id_seq OWNED BY routes.id;


--
-- TOC entry 208 (class 1259 OID 480217)
-- Dependencies: 5
-- Name: routing_constraints_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE routing_constraints_lines (
    stop_area_id bigint,
    line_id bigint
);


ALTER TABLE chouette_gui.routing_constraints_lines OWNER TO chouette;


--
-- TOC entry 175 (class 1259 OID 480012)
-- Dependencies: 5
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
-- TOC entry 174 (class 1259 OID 480010)
-- Dependencies: 175 5
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
-- TOC entry 2716 (class 0 OID 0)
-- Dependencies: 174
-- Name: stop_areas_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE stop_areas_id_seq OWNED BY stop_areas.id;


--
-- TOC entry 209 (class 1259 OID 480220)
-- Dependencies: 5
-- Name: stop_areas_stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE stop_areas_stop_areas (
    child_id bigint,
    parent_id bigint
);


ALTER TABLE chouette_gui.stop_areas_stop_areas OWNER TO chouette;

--
-- TOC entry 183 (class 1259 OID 480059)
-- Dependencies: 5
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
-- TOC entry 182 (class 1259 OID 480057)
-- Dependencies: 5 183
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
-- TOC entry 2717 (class 0 OID 0)
-- Dependencies: 182
-- Name: stop_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE stop_points_id_seq OWNED BY stop_points.id;


--
-- TOC entry 192 (class 1259 OID 480122)
-- Dependencies: 5
-- Name: time_slots; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_slots (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    beginning_slot_time time without time zone,
    end_slot_time time without time zone,
    first_departure_time_in_slot time without time zone,
    last_departure_time_in_slot time without time zone
);


ALTER TABLE chouette_gui.time_slots OWNER TO chouette;

--
-- TOC entry 191 (class 1259 OID 480120)
-- Dependencies: 192 5
-- Name: time_slots_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE time_slots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_slots_id_seq OWNER TO chouette;

--
-- TOC entry 2718 (class 0 OID 0)
-- Dependencies: 191
-- Name: time_slots_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_slots_id_seq OWNED BY time_slots.id;


--
-- TOC entry 178 (class 1259 OID 480037)
-- Dependencies: 5
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
-- TOC entry 218 (class 1259 OID 480337)
-- Dependencies: 178 5
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
-- TOC entry 2719 (class 0 OID 0)
-- Dependencies: 218
-- Name: time_table_dates_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_table_dates_id_seq OWNED BY time_table_dates.id;


--
-- TOC entry 179 (class 1259 OID 480041)
-- Dependencies: 5
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
-- TOC entry 219 (class 1259 OID 480346)
-- Dependencies: 5 179
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
-- TOC entry 2720 (class 0 OID 0)
-- Dependencies: 219
-- Name: time_table_periods_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_table_periods_id_seq OWNED BY time_table_periods.id;


--
-- TOC entry 177 (class 1259 OID 480025)
-- Dependencies: 2432 2433 5
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
-- TOC entry 176 (class 1259 OID 480023)
-- Dependencies: 177 5
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
-- TOC entry 2721 (class 0 OID 0)
-- Dependencies: 176
-- Name: time_tables_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE time_tables_id_seq OWNED BY time_tables.id;


--
-- TOC entry 197 (class 1259 OID 480158)
-- Dependencies: 5
-- Name: time_tables_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE time_tables_vehicle_journeys (
    time_table_id bigint,
    vehicle_journey_id bigint
);


ALTER TABLE chouette_gui.time_tables_vehicle_journeys OWNER TO chouette;


--
-- TOC entry 196 (class 1259 OID 480147)
-- Dependencies: 5
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
    waiting_time time without time zone,
    elapse_duration time without time zone,
    headway_frequency time without time zone,
    for_boarding character varying(255),
    for_alighting character varying(255)
);


ALTER TABLE chouette_gui.vehicle_journey_at_stops OWNER TO chouette;

--
-- TOC entry 195 (class 1259 OID 480145)
-- Dependencies: 196 5
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
-- TOC entry 2723 (class 0 OID 0)
-- Dependencies: 195
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE vehicle_journey_at_stops_id_seq OWNED BY vehicle_journey_at_stops.id;


--
-- TOC entry 194 (class 1259 OID 480134)
-- Dependencies: 5
-- Name: vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE TABLE vehicle_journeys (
    id bigint NOT NULL,
    route_id bigint,
    journey_pattern_id bigint,
    time_slot_id bigint,
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
    flexible_service boolean
);


ALTER TABLE chouette_gui.vehicle_journeys OWNER TO chouette;

--
-- TOC entry 193 (class 1259 OID 480132)
-- Dependencies: 5 194
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
-- TOC entry 2724 (class 0 OID 0)
-- Dependencies: 193
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE vehicle_journeys_id_seq OWNED BY vehicle_journeys.id;


--
-- TOC entry 2447 (class 2604 OID 480180)
-- Dependencies: 200 201 201
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links ALTER COLUMN id SET DEFAULT nextval('access_links_id_seq'::regclass);


--
-- TOC entry 2446 (class 2604 OID 480168)
-- Dependencies: 198 199 199
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_points ALTER COLUMN id SET DEFAULT nextval('access_points_id_seq'::regclass);


--
-- TOC entry 2423 (class 2604 OID 479973)
-- Dependencies: 169 168 169
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY companies ALTER COLUMN id SET DEFAULT nextval('companies_id_seq'::regclass);



--
-- TOC entry 2438 (class 2604 OID 480074)
-- Dependencies: 184 185 185
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links ALTER COLUMN id SET DEFAULT nextval('connection_links_id_seq'::regclass);



--
-- TOC entry 2448 (class 2604 OID 480192)
-- Dependencies: 202 203 203
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY facilities ALTER COLUMN id SET DEFAULT nextval('facilities_id_seq'::regclass);


--
-- TOC entry 2449 (class 2604 OID 480207)
-- Dependencies: 206 205 206
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines ALTER COLUMN id SET DEFAULT nextval('group_of_lines_id_seq'::regclass);



--
-- TOC entry 2442 (class 2604 OID 480109)
-- Dependencies: 189 188 189
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns ALTER COLUMN id SET DEFAULT nextval('journey_patterns_id_seq'::regclass);


--
-- TOC entry 2422 (class 2604 OID 479960)
-- Dependencies: 167 166 167
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines ALTER COLUMN id SET DEFAULT nextval('lines_id_seq'::regclass);


--
-- TOC entry 2424 (class 2604 OID 479986)
-- Dependencies: 171 170 171
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY networks ALTER COLUMN id SET DEFAULT nextval('networks_id_seq'::regclass);


--
-- TOC entry 2450 (class 2604 OID 480247)
-- Dependencies: 210 211 211
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY pt_links ALTER COLUMN id SET DEFAULT nextval('pt_links_id_seq'::regclass);


--
-- TOC entry 2436 (class 2604 OID 480050)
-- Dependencies: 180 181 181
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routes ALTER COLUMN id SET DEFAULT nextval('routes_id_seq'::regclass);


--
-- TOC entry 2430 (class 2604 OID 480015)
-- Dependencies: 174 175 175
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas ALTER COLUMN id SET DEFAULT nextval('stop_areas_id_seq'::regclass);


--
-- TOC entry 2437 (class 2604 OID 480062)
-- Dependencies: 182 183 183
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points ALTER COLUMN id SET DEFAULT nextval('stop_points_id_seq'::regclass);


--
-- TOC entry 2443 (class 2604 OID 480125)
-- Dependencies: 191 192 192
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_slots ALTER COLUMN id SET DEFAULT nextval('time_slots_id_seq'::regclass);


--
-- TOC entry 2434 (class 2604 OID 480339)
-- Dependencies: 218 178
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_dates ALTER COLUMN id SET DEFAULT nextval('time_table_dates_id_seq'::regclass);


--
-- TOC entry 2435 (class 2604 OID 480348)
-- Dependencies: 219 179
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_periods ALTER COLUMN id SET DEFAULT nextval('time_table_periods_id_seq'::regclass);


--
-- TOC entry 2431 (class 2604 OID 480028)
-- Dependencies: 177 176 177
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables ALTER COLUMN id SET DEFAULT nextval('time_tables_id_seq'::regclass);


--
-- TOC entry 2445 (class 2604 OID 480150)
-- Dependencies: 195 196 196
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops ALTER COLUMN id SET DEFAULT nextval('vehicle_journey_at_stops_id_seq'::regclass);


--
-- TOC entry 2444 (class 2604 OID 480137)
-- Dependencies: 193 194 194
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys ALTER COLUMN id SET DEFAULT nextval('vehicle_journeys_id_seq'::regclass);


--
-- TOC entry 2525 (class 2606 OID 480185)
-- Dependencies: 201 201 2690
-- Name: access_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT access_links_pkey PRIMARY KEY (id);


--
-- TOC entry 2522 (class 2606 OID 480173)
-- Dependencies: 199 199 2690
-- Name: access_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY access_points
    ADD CONSTRAINT access_points_pkey PRIMARY KEY (id);


--
-- TOC entry 2468 (class 2606 OID 479978)
-- Dependencies: 169 169 2690
-- Name: companies_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (id);


--
-- TOC entry 2499 (class 2606 OID 480079)
-- Dependencies: 185 185 2690
-- Name: connection_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT connection_links_pkey PRIMARY KEY (id);



--
-- TOC entry 2528 (class 2606 OID 480197)
-- Dependencies: 203 203 2690
-- Name: facilities_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- TOC entry 2531 (class 2606 OID 480212)
-- Dependencies: 206 206 2690
-- Name: group_of_lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY group_of_lines
    ADD CONSTRAINT group_of_lines_pkey PRIMARY KEY (id);



--
-- TOC entry 2505 (class 2606 OID 480114)
-- Dependencies: 189 189 2690
-- Name: journey_patterns_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT journey_patterns_pkey PRIMARY KEY (id);


--
-- TOC entry 2464 (class 2606 OID 479965)
-- Dependencies: 167 167 2690
-- Name: lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT lines_pkey PRIMARY KEY (id);


--
-- TOC entry 2472 (class 2606 OID 479991)
-- Dependencies: 171 171 2690
-- Name: networks_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY networks
    ADD CONSTRAINT networks_pkey PRIMARY KEY (id);


--
-- TOC entry 2534 (class 2606 OID 480252)
-- Dependencies: 211 211 2690
-- Name: pt_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY pt_links
    ADD CONSTRAINT pt_links_pkey PRIMARY KEY (id);


--
-- TOC entry 2493 (class 2606 OID 480055)
-- Dependencies: 181 181 2690
-- Name: routes_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (id);


--
-- TOC entry 2481 (class 2606 OID 480020)
-- Dependencies: 175 175 2690
-- Name: stop_areas_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY stop_areas
    ADD CONSTRAINT stop_areas_pkey PRIMARY KEY (id);


--
-- TOC entry 2496 (class 2606 OID 480067)
-- Dependencies: 183 183 2690
-- Name: stop_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stop_points_pkey PRIMARY KEY (id);


--
-- TOC entry 2509 (class 2606 OID 480130)
-- Dependencies: 192 192 2690
-- Name: time_slots_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_slots
    ADD CONSTRAINT time_slots_pkey PRIMARY KEY (id);


--
-- TOC entry 2487 (class 2606 OID 480345)
-- Dependencies: 178 178 2690
-- Name: time_table_dates_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_table_dates
    ADD CONSTRAINT time_table_dates_pkey PRIMARY KEY (id);


--
-- TOC entry 2490 (class 2606 OID 480354)
-- Dependencies: 179 179 2690
-- Name: time_table_periods_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_table_periods
    ADD CONSTRAINT time_table_periods_pkey PRIMARY KEY (id);


--
-- TOC entry 2484 (class 2606 OID 480035)
-- Dependencies: 177 177 2690
-- Name: time_tables_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY time_tables
    ADD CONSTRAINT time_tables_pkey PRIMARY KEY (id);



--
-- TOC entry 2517 (class 2606 OID 480155)
-- Dependencies: 196 196 2690
-- Name: vehicle_journey_at_stops_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vehicle_journey_at_stops_pkey PRIMARY KEY (id);


--
-- TOC entry 2513 (class 2606 OID 480142)
-- Dependencies: 194 194 2690
-- Name: vehicle_journeys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_pkey PRIMARY KEY (id);


--
-- TOC entry 2523 (class 1259 OID 480186)
-- Dependencies: 201 2690
-- Name: access_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX access_links_objectid_key ON access_links USING btree (objectid);


--
-- TOC entry 2520 (class 1259 OID 480174)
-- Dependencies: 199 2690
-- Name: access_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX access_points_objectid_key ON access_points USING btree (objectid);


--
-- TOC entry 2466 (class 1259 OID 479979)
-- Dependencies: 169 2690
-- Name: companies_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX companies_objectid_key ON companies USING btree (objectid);



--
-- TOC entry 2497 (class 1259 OID 480080)
-- Dependencies: 185 2690
-- Name: connection_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX connection_links_objectid_key ON connection_links USING btree (objectid);


--
-- TOC entry 2526 (class 1259 OID 480198)
-- Dependencies: 203 2690
-- Name: facilities_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX facilities_objectid_key ON facilities USING btree (objectid);


--
-- TOC entry 2529 (class 1259 OID 480213)
-- Dependencies: 206 2690
-- Name: group_of_lines_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX group_of_lines_objectid_key ON group_of_lines USING btree (objectid);


--
-- TOC entry 2506 (class 1259 OID 480119)
-- Dependencies: 190 2690
-- Name: index_journey_pattern_id_on_journey_patterns_stop_points; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_journey_pattern_id_on_journey_patterns_stop_points ON journey_patterns_stop_points USING btree (journey_pattern_id);


--
-- TOC entry 2478 (class 1259 OID 480022)
-- Dependencies: 175 2690
-- Name: index_stop_areas_on_parent_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_stop_areas_on_parent_id ON stop_areas USING btree (parent_id);


--
-- TOC entry 2485 (class 1259 OID 480040)
-- Dependencies: 178 2690
-- Name: index_time_table_dates_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_table_dates_on_time_table_id ON time_table_dates USING btree (time_table_id);


--
-- TOC entry 2488 (class 1259 OID 480044)
-- Dependencies: 179 2690
-- Name: index_time_table_periods_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_table_periods_on_time_table_id ON time_table_periods USING btree (time_table_id);


--
-- TOC entry 2518 (class 1259 OID 480161)
-- Dependencies: 197 2690
-- Name: index_time_tables_vehicle_journeys_on_time_table_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_tables_vehicle_journeys_on_time_table_id ON time_tables_vehicle_journeys USING btree (time_table_id);


--
-- TOC entry 2519 (class 1259 OID 480162)
-- Dependencies: 197 2690
-- Name: index_time_tables_vehicle_journeys_on_vehicle_journey_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_time_tables_vehicle_journeys_on_vehicle_journey_id ON time_tables_vehicle_journeys USING btree (vehicle_journey_id);


--
-- TOC entry 2514 (class 1259 OID 480157)
-- Dependencies: 196 2690
-- Name: index_vehicle_journey_at_stops_on_stop_pointid; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journey_at_stops_on_stop_pointid ON vehicle_journey_at_stops USING btree (stop_point_id);


--
-- TOC entry 2515 (class 1259 OID 480156)
-- Dependencies: 196 2690
-- Name: index_vehicle_journey_at_stops_on_vehicle_journey_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journey_at_stops_on_vehicle_journey_id ON vehicle_journey_at_stops USING btree (vehicle_journey_id);


--
-- TOC entry 2510 (class 1259 OID 480144)
-- Dependencies: 194 2690
-- Name: index_vehicle_journeys_on_route_id; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE INDEX index_vehicle_journeys_on_route_id ON vehicle_journeys USING btree (route_id);


--
-- TOC entry 2503 (class 1259 OID 480115)
-- Dependencies: 189 2690
-- Name: journey_patterns_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX journey_patterns_objectid_key ON journey_patterns USING btree (objectid);


--
-- TOC entry 2462 (class 1259 OID 479966)
-- Dependencies: 167 2690
-- Name: lines_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX lines_objectid_key ON lines USING btree (objectid);


--
-- TOC entry 2470 (class 1259 OID 479992)
-- Dependencies: 171 2690
-- Name: networks_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX networks_objectid_key ON networks USING btree (objectid);


--
-- TOC entry 2532 (class 1259 OID 480253)
-- Dependencies: 211 2690
-- Name: pt_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX pt_links_objectid_key ON pt_links USING btree (objectid);


--
-- TOC entry 2491 (class 1259 OID 480056)
-- Dependencies: 181 2690
-- Name: routes_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX routes_objectid_key ON routes USING btree (objectid);


--
-- TOC entry 2479 (class 1259 OID 480021)
-- Dependencies: 175 2690
-- Name: stop_areas_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX stop_areas_objectid_key ON stop_areas USING btree (objectid);


--
-- TOC entry 2494 (class 1259 OID 480068)
-- Dependencies: 183 2690
-- Name: stop_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX stop_points_objectid_key ON stop_points USING btree (objectid);


--
-- TOC entry 2507 (class 1259 OID 480131)
-- Dependencies: 192 2690
-- Name: time_slots_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX time_slots_objectid_key ON time_slots USING btree (objectid);


--
-- TOC entry 2482 (class 1259 OID 480036)
-- Dependencies: 177 2690
-- Name: time_tables_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX time_tables_objectid_key ON time_tables USING btree (objectid);


--
-- TOC entry 2511 (class 1259 OID 480143)
-- Dependencies: 194 2690
-- Name: vehicle_journeys_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace: 
--

CREATE UNIQUE INDEX vehicle_journeys_objectid_key ON vehicle_journeys USING btree (objectid);


--
-- TOC entry 2575 (class 2606 OID 480392)
-- Dependencies: 175 2480 199 2690
-- Name: access_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_points
    ADD CONSTRAINT access_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2577 (class 2606 OID 480382)
-- Dependencies: 199 201 2521 2690
-- Name: aclk_acpt_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (access_point_id) REFERENCES access_points(id) ON DELETE CASCADE;


--
-- TOC entry 2576 (class 2606 OID 480387)
-- Dependencies: 175 201 2480 2690
-- Name: aclk_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY access_links
    ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2555 (class 2606 OID 480467)
-- Dependencies: 175 175 2480 2690
-- Name: area_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas
    ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parent_id) REFERENCES stop_areas(id) ON DELETE SET NULL;


--
-- TOC entry 2565 (class 2606 OID 480417)
-- Dependencies: 183 189 2495 2690
-- Name: arrival_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id) REFERENCES stop_points(id) ON DELETE SET NULL;


--
-- TOC entry 2562 (class 2606 OID 480397)
-- Dependencies: 175 2480 185 2690
-- Name: colk_endarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrival_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2561 (class 2606 OID 480402)
-- Dependencies: 2480 185 175 2690
-- Name: colk_startarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY connection_links
    ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departure_id) REFERENCES stop_areas(id) ON DELETE CASCADE;



--
-- TOC entry 2564 (class 2606 OID 480422)
-- Dependencies: 183 2495 189 2690
-- Name: departure_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id) REFERENCES stop_points(id) ON DELETE SET NULL;


--
-- TOC entry 2579 (class 2606 OID 480407)
-- Dependencies: 2530 206 207 2690
-- Name: groupofline_group_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines_lines
    ADD CONSTRAINT groupofline_group_fkey FOREIGN KEY (group_of_line_id) REFERENCES group_of_lines(id) ON DELETE CASCADE;


--
-- TOC entry 2578 (class 2606 OID 480412)
-- Dependencies: 207 2463 167 2690
-- Name: groupofline_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY group_of_lines_lines
    ADD CONSTRAINT groupofline_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 2563 (class 2606 OID 480427)
-- Dependencies: 189 2492 181 2690
-- Name: jp_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns
    ADD CONSTRAINT jp_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 2567 (class 2606 OID 480432)
-- Dependencies: 189 190 2504 2690
-- Name: jpsp_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns_stop_points
    ADD CONSTRAINT jpsp_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES journey_patterns(id) ON DELETE CASCADE;


--
-- TOC entry 2566 (class 2606 OID 480437)
-- Dependencies: 2495 190 183 2690
-- Name: jpsp_stoppoint_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY journey_patterns_stop_points
    ADD CONSTRAINT jpsp_stoppoint_fkey FOREIGN KEY (stop_point_id) REFERENCES stop_points(id) ON DELETE CASCADE;


--
-- TOC entry 2554 (class 2606 OID 480442)
-- Dependencies: 169 167 2467 2690
-- Name: line_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_company_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;


--
-- TOC entry 2553 (class 2606 OID 480447)
-- Dependencies: 167 171 2471 2690
-- Name: line_ptnetwork_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (network_id) REFERENCES networks(id) ON DELETE SET NULL;


--
-- TOC entry 2558 (class 2606 OID 480452)
-- Dependencies: 2463 181 167 2690
-- Name: route_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routes
    ADD CONSTRAINT route_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 2581 (class 2606 OID 480457)
-- Dependencies: 167 208 2463 2690
-- Name: routingconstraint_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routing_constraints_lines
    ADD CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (line_id) REFERENCES lines(id) ON DELETE CASCADE;


--
-- TOC entry 2580 (class 2606 OID 480462)
-- Dependencies: 175 208 2480 2690
-- Name: routingconstraint_stoparea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY routing_constraints_lines
    ADD CONSTRAINT routingconstraint_stoparea_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2583 (class 2606 OID 480472)
-- Dependencies: 2480 209 175 2690
-- Name: stoparea_child_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas_stop_areas
    ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (child_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2582 (class 2606 OID 480477)
-- Dependencies: 209 2480 175 2690
-- Name: stoparea_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_areas_stop_areas
    ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parent_id) REFERENCES stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 2560 (class 2606 OID 480482)
-- Dependencies: 183 2480 175 2690
-- Name: stoppoint_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_area_fkey FOREIGN KEY (stop_area_id) REFERENCES stop_areas(id);


--
-- TOC entry 2559 (class 2606 OID 480487)
-- Dependencies: 181 183 2492 2690
-- Name: stoppoint_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 2556 (class 2606 OID 480492)
-- Dependencies: 178 177 2483 2690
-- Name: tm_date_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_dates
    ADD CONSTRAINT tm_date_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 2557 (class 2606 OID 480497)
-- Dependencies: 179 2483 177 2690
-- Name: tm_period_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_table_periods
    ADD CONSTRAINT tm_period_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 2570 (class 2606 OID 480522)
-- Dependencies: 194 2467 169 2690
-- Name: vj_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_company_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;


--
-- TOC entry 2569 (class 2606 OID 480527)
-- Dependencies: 2504 189 194 2690
-- Name: vj_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES journey_patterns(id) ON DELETE CASCADE;


--
-- TOC entry 2568 (class 2606 OID 480532)
-- Dependencies: 2492 194 181 2690
-- Name: vj_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


--
-- TOC entry 2572 (class 2606 OID 480512)
-- Dependencies: 196 2495 183 2690
-- Name: vjas_sp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stop_point_id) REFERENCES stop_points(id) ON DELETE CASCADE;


--
-- TOC entry 2571 (class 2606 OID 480517)
-- Dependencies: 2512 196 194 2690
-- Name: vjas_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY vehicle_journey_at_stops
    ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES vehicle_journeys(id) ON DELETE CASCADE;


--
-- TOC entry 2574 (class 2606 OID 480502)
-- Dependencies: 197 2483 177 2690
-- Name: vjtm_tm_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_tm_fkey FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE;


--
-- TOC entry 2573 (class 2606 OID 480507)
-- Dependencies: 2512 194 197 2690
-- Name: vjtm_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES vehicle_journeys(id) ON DELETE CASCADE;




--
-- TOC entry 2694 (class 0 OID 0)
-- Dependencies: 5
-- Name: chouette_gui; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA chouette_gui TO chouette;
GRANT ALL ON SCHEMA chouette_gui TO PUBLIC;



-- Completed on 2014-02-27 11:15:40 CET

--
-- PostgreSQL database dump complete
--

