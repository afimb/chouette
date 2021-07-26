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

DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public ;

DROP EXTENSION IF EXISTS postgis CASCADE;
CREATE SCHEMA IF NOT EXISTS shared_extensions;
CREATE EXTENSION postgis SCHEMA shared_extensions;

SET search_path = chouette_gui, pg_catalog;

--
-- TOC entry 174 (class 1259 OID 938851)
-- Name: access_links; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE public.access_links (
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


ALTER TABLE public.access_links OWNER TO chouette;

--
-- TOC entry 175 (class 1259 OID 938857)
-- Name: access_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE public.access_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_links_id_seq OWNER TO chouette;

--
-- TOC entry 4252 (class 0 OID 0)
-- Dependencies: 175
-- Name: access_links_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.access_links_id_seq OWNED BY public.access_links.id;


--
-- TOC entry 176 (class 1259 OID 938859)
-- Name: access_points; Type: TABLE; Schema: public; Owner: chouette; Tablespace:
--

CREATE TABLE public.access_points (
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


ALTER TABLE public.access_points OWNER TO chouette;

--
-- TOC entry 177 (class 1259 OID 938865)
-- Name: access_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE public.access_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_points_id_seq OWNER TO chouette;

--
-- TOC entry 4253 (class 0 OID 0)
-- Dependencies: 177
-- Name: access_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE public.access_points_id_seq OWNED BY public.access_points.id;

--
-- Name: codespaces; Type: TABLE; Schema: :SCH; Owner: chouette; Tablespace:
--

CREATE TABLE codespaces (
    id bigint NOT NULL,
    xmlns character(3),
    xmlns_url character varying(255),
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.codespaces OWNER TO chouette;


--
-- Name: codespaces_id_seq; Type: SEQUENCE; Schema: :SCH; Owner: chouette
--

CREATE SEQUENCE codespaces_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.codespaces_id_seq OWNER TO chouette;


--
-- Name: codespaces_id_seq; Type: SEQUENCE OWNED BY; Schema: :SCH; Owner: chouette
--

ALTER SEQUENCE codespaces_id_seq OWNED BY codespaces.id;


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
    legal_name character varying(255),
    organisation_type character varying(255),
    organizational_unit character varying(255),
    operating_department_name character varying(255),
    code character varying(255),
    phone character varying(255),
    fax character varying(255),
    email character varying(255),
    public_email character varying(255),
    public_url character varying(255),
    public_phone character varying(255),
    registration_number character varying(255),
    url character varying(255),
    time_zone character varying(255),
    branding_id bigint
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

CREATE TABLE public.connection_links (
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


ALTER TABLE public.connection_links OWNER TO chouette;

--
-- TOC entry 183 (class 1259 OID 938889)
-- Name: connection_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE public.connection_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.connection_links_id_seq OWNER TO chouette;

--
-- TOC entry 4256 (class 0 OID 0)
-- Dependencies: 183
-- Name: connection_links_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE public.connection_links_id_seq OWNED BY public.connection_links.id;


--
-- Name: destination_displays; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE destination_displays (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    side_text character varying(255),
    front_text character varying(255)
);


ALTER TABLE chouette_gui.destination_displays OWNER TO chouette;


CREATE SEQUENCE destination_displays_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE chouette_gui.destination_displays_id_seq OWNER TO chouette;


ALTER SEQUENCE destination_displays_id_seq OWNED BY destination_displays.id;


CREATE TABLE destination_display_via
(
    destination_display_id bigint NOT NULL,
    via_id bigint NOT NULL,
    "position" bigint,
    created_at timestamp without time zone,
    updated_at timestamp without time zone
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE destination_display_via OWNER to chouette;

-- Index: index_destination_display_id_on_destination_display_via

-- DROP INDEX bra.index_destination_display_id_on_destination_display_via;

CREATE INDEX index_destination_display_id_on_destination_display_via
    ON destination_display_via USING btree
    (destination_display_id)
    TABLESPACE pg_default;



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
    code character varying(255),
    label character varying(255),
    creation_time timestamp without time zone,
    objectid character varying COLLATE pg_catalog."default" NOT NULL,
    object_version integer,
    creator_id character varying COLLATE pg_catalog."default"
    );


ALTER TABLE chouette_gui.footnotes OWNER TO chouette;

CREATE UNIQUE INDEX footnotes_objectid_idx
    ON chouette_gui.footnotes USING btree
    (objectid COLLATE pg_catalog."default")
    TABLESPACE pg_default;

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



create table if not exists chouette_gui.footnote_alternative_texts
(
    id bigserial not null
        constraint footnote_alternative_texts_pkey
            primary key,
    objectid varchar not null,
    object_version integer,
    creation_time timestamp,
    creator_id varchar(255),
    footnote_id integer not null,
    text varchar,
    language varchar
);

alter table chouette_gui.footnote_alternative_texts owner to chouette;

create unique index if not exists footnote_alternative_texts_objectid_key
    on chouette_gui.footnote_alternative_texts (objectid);

alter sequence chouette_gui.footnote_alternative_texts_id_seq owner to chouette;


--
-- TOC entry 193 (class 1259 OID 938928)
-- Name: footnotes_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE footnotes_vehicle_journeys (
    vehicle_journey_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_vehicle_journeys OWNER TO chouette;


CREATE TABLE footnotes_lines (
    line_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_lines OWNER TO chouette;

CREATE TABLE footnotes_journey_patterns (
    journey_pattern_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_journey_patterns OWNER TO chouette;

CREATE TABLE footnotes_stop_points (
    stop_point_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_stop_points OWNER TO chouette;

CREATE TABLE footnotes_vehicle_journey_at_stops (
    vehicle_journey_at_stop_id bigint,
    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_vehicle_journey_at_stops OWNER TO chouette;

CREATE TABLE booking_arrangements_buy_when (
    booking_arrangement_id bigint NOT NULL,
    buy_when character varying(255)
    );


ALTER TABLE chouette_gui.booking_arrangements_buy_when OWNER TO chouette;

CREATE TABLE booking_arrangements_booking_methods (
    booking_arrangement_id bigint NOT NULL,
    booking_method character varying(255)
    );


ALTER TABLE chouette_gui.booking_arrangements_booking_methods OWNER TO chouette;

CREATE TABLE lines_key_values (
    line_id bigint NOT NULL,
    type_of_key character varying,
    key character varying,
    value character varying
    );


ALTER TABLE chouette_gui.lines_key_values OWNER TO chouette;


CREATE TABLE vehicle_journeys_key_values (
    vehicle_journey_id bigint NOT NULL,
    type_of_key character varying,
    key character varying,
    value character varying
    );


ALTER TABLE chouette_gui.vehicle_journeys_key_values OWNER TO chouette;



CREATE TABLE brandings (
    id bigint NOT NULL,
    name character varying,
    description character varying,
    url character varying,
    image character varying,
    creation_time timestamp without time zone,
    objectid character varying COLLATE pg_catalog."default" NOT NULL,
    object_version integer,
    creator_id character varying COLLATE pg_catalog."default"
    );


ALTER TABLE chouette_gui.brandings OWNER TO chouette;

CREATE UNIQUE INDEX brandings_objectid_idx
    ON chouette_gui.brandings USING btree
    (objectid COLLATE pg_catalog."default")
    TABLESPACE pg_default;

--
-- TOC entry 192 (class 1259 OID 938926)
-- Name: brandings_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE brandings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.brandings_id_seq OWNER TO chouette;

--
-- TOC entry 4260 (class 0 OID 0)
-- Dependencies: 192
-- Name: brandings_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE brandings_id_seq OWNED BY brandings.id;

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



-- Table: interchanges

-- DROP TABLE interchanges;

CREATE SEQUENCE interchanges_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE interchanges_id_seq
    OWNER TO chouette;

CREATE TABLE interchanges
(
    id bigint NOT NULL DEFAULT nextval('interchanges_id_seq'::regclass),
    objectid character varying COLLATE pg_catalog."default" NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying COLLATE pg_catalog."default",
    name character varying COLLATE pg_catalog."default",
    priority integer,
    planned boolean,
    guaranteed boolean,
    advertised boolean,
    maximum_wait_time time without time zone,
    from_point character varying COLLATE pg_catalog."default" ,
    to_point character varying COLLATE pg_catalog."default" ,
    from_vehicle_journey character varying COLLATE pg_catalog."default" ,
    to_vehicle_journey character varying COLLATE pg_catalog."default" ,
    stay_seated boolean,
    minimum_transfer_time time without time zone,
    from_visit_number integer,
    to_visit_number integer,

    CONSTRAINT interchanges_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE interchanges
    OWNER to chouette;

-- Index: interchanges_from_point_key

-- DROP INDEX fin.interchanges_from_point_key;

CREATE INDEX interchanges_from_point_key
    ON interchanges USING btree
    (from_point COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: interchanges_from_vehicle_journey_key

-- DROP INDEX fin.interchanges_from_vehicle_journey_key;

CREATE INDEX interchanges_from_vehicle_journey_key
    ON interchanges USING btree
    (from_vehicle_journey COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: interchanges_objectid_key

-- DROP INDEX fin.interchanges_objectid_key;

CREATE UNIQUE INDEX interchanges_objectid_key
    ON interchanges USING btree
    (objectid COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: interchanges_to_poinnt_key

-- DROP INDEX fin.interchanges_to_poinnt_key;

CREATE INDEX interchanges_to_poinnt_key
    ON interchanges USING btree
    (to_point COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: interchanges_to_vehicle_journey_key

-- DROP INDEX fin.interchanges_to_vehicle_journey_key;

CREATE INDEX interchanges_to_vehicle_journey_key
    ON interchanges USING btree
    (objectid COLLATE pg_catalog."default")
    TABLESPACE pg_default;



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



CREATE TABLE contact_structures (
    id bigint NOT NULL,
    contact_person CHARACTER VARYING,
    email CHARACTER VARYING,
    phone CHARACTER VARYING,
    fax CHARACTER VARYING,
    url CHARACTER VARYING,
    further_details CHARACTER VARYING
);

ALTER TABLE chouette_gui.contact_structures OWNER TO chouette;


CREATE SEQUENCE contact_structures_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.contact_structures_id_seq OWNER TO chouette;

ALTER SEQUENCE contact_structures_id_seq OWNED BY contact_structures.id;

CREATE TABLE booking_arrangements (
    id bigint NOT NULL,
    booking_contact_id bigint,
    booking_note character varying,
    booking_access character varying(255),
    book_when character varying(255),
    latest_booking_time  time without time zone,
    minimum_booking_period  time without time zone
);


ALTER TABLE chouette_gui.booking_arrangements OWNER TO chouette;

CREATE SEQUENCE booking_arrangements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.booking_arrangements_id_seq OWNER TO chouette;

--
-- TOC entry 4265 (class 0 OID 0)
-- Dependencies: 201
-- Name: lines_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE booking_arrangements_id_seq OWNED BY booking_arrangements.id;

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
    transport_submode_name character varying(255),
    registration_number character varying(255),
    comment character varying(255),
    mobility_restricted_suitability boolean,
    int_user_needs integer,
    flexible_service boolean,
    url character varying(255),
    color character varying(6),
    text_color character varying(6),
    stable_id character varying(255),
    flexible_line_type character varying,
    booking_arrangement_id bigint
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
    company_id bigint,
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

CREATE TABLE public.referentials (
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

insert into public.referentials (id,name,slug) values (1,'Test referential','chouette_gui');

--
-- TOC entry 376 (class 1259 OID 942335)
-- Name: route_sections; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE route_sections (
    id bigint NOT NULL,
    from_scheduled_stop_point_id integer,
    to_scheduled_stop_point_id integer,
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

CREATE TABLE public.stop_areas (
    id bigint NOT NULL,
    parent_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    name character varying(255),
    comment character varying(255),
    area_type character varying(255),
    stop_place_type character varying(255),
    transport_mode character varying(255),
    transport_sub_mode character varying(255),
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
    time_zone character varying(255),
    compass_bearing integer
);


ALTER TABLE public.stop_areas OWNER TO chouette;

--
-- TOC entry 217 (class 1259 OID 939023)
-- Name: stop_areas_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE public.stop_areas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stop_areas_id_seq OWNER TO chouette;

--
-- TOC entry 4273 (class 0 OID 0)
-- Dependencies: 217
-- Name: stop_areas_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE public.stop_areas_id_seq OWNED BY public.stop_areas.id;


--
-- TOC entry 218 (class 1259 OID 939025)
-- Name: stop_areas_stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE public.stop_areas_stop_areas (
    child_id bigint,
    parent_id bigint
);


ALTER TABLE public.stop_areas_stop_areas OWNER TO chouette;



CREATE TABLE scheduled_stop_points (
    id bigint NOT NULL,
    name character varying(255),
    stop_area_objectid_key CHARACTER VARYING(256),
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    timing_point_status varchar
);


ALTER TABLE chouette_gui.scheduled_stop_points OWNER TO chouette;

CREATE SEQUENCE scheduled_stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.scheduled_stop_points_id_seq OWNER TO chouette;

--
-- TOC entry 219 (class 1259 OID 939028)
-- Name: stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE TABLE stop_points (
    id bigint NOT NULL,
    route_id bigint,
    destination_display_id bigint,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    "position" integer,
    for_boarding character varying(255),
    for_alighting character varying(255),
    scheduled_stop_point_id bigint,
    booking_arrangement_id bigint
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

-- Route points

CREATE TABLE route_points (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    scheduled_stop_point_id bigint,
    "name" character varying(255),
    "boarder_crossing" boolean
);


ALTER TABLE chouette_gui.route_points OWNER TO chouette;

CREATE SEQUENCE route_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.route_points_id_seq OWNER TO chouette;

ALTER SEQUENCE route_points_id_seq OWNED BY route_points.id;

CREATE TABLE routes_route_points (
    route_id bigint,
    route_point_id bigint,
    POSITION integer NOT NULL
);


ALTER TABLE chouette_gui.routes_route_points OWNER TO chouette;


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
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
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

CREATE TABLE flexible_service_properties (
    id bigint NOT NULL,
    objectid character varying(255) NOT NULL,
    object_version integer,
    creation_time timestamp without time zone,
    creator_id character varying(255),
    flexible_service_type character varying(255),
    booking_arrangement_id bigint,
    cancellation_possible boolean,
    change_of_time_possible boolean
    );

ALTER TABLE chouette_gui.flexible_service_properties OWNER TO chouette;

CREATE SEQUENCE flexible_service_properties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.flexible_service_properties_id_seq OWNER TO chouette;

ALTER SEQUENCE flexible_service_properties_id_seq OWNED BY flexible_service_properties.id;


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
    transport_submode_name character varying(255),
    published_journey_name character varying(255),
    published_journey_identifier character varying(255),
    facility character varying(255),
    vehicle_type_identifier character varying(255),
    number bigint,
    mobility_restricted_suitability boolean,
    flexible_service boolean,
    journey_category integer DEFAULT 0 NOT NULL,
    private_code character varying(255),
    service_alteration character varying(255),
    flexible_service_properties_id bigint
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

ALTER TABLE ONLY public.access_links ALTER COLUMN id SET DEFAULT nextval('public.access_links_id_seq'::regclass);


--
-- TOC entry 3949 (class 2604 OID 939608)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.access_points ALTER COLUMN id SET DEFAULT nextval('public.access_points_id_seq'::regclass);



--
-- TOC entry 3951 (class 2604 OID 939610)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY companies ALTER COLUMN id SET DEFAULT nextval('companies_id_seq'::regclass);


--
-- TOC entry 3952 (class 2604 OID 939611)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.connection_links ALTER COLUMN id SET DEFAULT nextval('public.connection_links_id_seq'::regclass);


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

ALTER TABLE ONLY public.stop_areas ALTER COLUMN id SET DEFAULT nextval('public.stop_areas_id_seq'::regclass);


--
-- TOC entry 3971 (class 2604 OID 939626)
-- Name: id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points ALTER COLUMN id SET DEFAULT nextval('stop_points_id_seq'::regclass);


ALTER TABLE ONLY route_points ALTER COLUMN id SET DEFAULT nextval('route_points_id_seq'::regclass);


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

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT access_links_pkey PRIMARY KEY (id);


--
-- TOC entry 3998 (class 2606 OID 939695)
-- Name: access_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace:
--

ALTER TABLE ONLY public.access_points
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

ALTER TABLE ONLY public.connection_links
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


ALTER TABLE ONLY contact_structures
    ADD CONSTRAINT contact_structures_pkey PRIMARY KEY (id);

ALTER TABLE ONLY booking_arrangements
    ADD CONSTRAINT booking_arrangements_pkey PRIMARY KEY (id);

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

ALTER TABLE ONLY public.stop_areas
    ADD CONSTRAINT stop_areas_pkey PRIMARY KEY (id);


--
-- TOC entry 4053 (class 2606 OID 939731)
-- Name: stop_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette; Tablespace:
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stop_points_pkey PRIMARY KEY (id);

ALTER TABLE ONLY route_points
    ADD CONSTRAINT route_points_pkey PRIMARY KEY (id);


ALTER TABLE ONLY scheduled_stop_points
    ADD CONSTRAINT scheduled_stop_points_pkey PRIMARY KEY (id);


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

ALTER TABLE ONLY flexible_service_properties
    ADD CONSTRAINT flexible_service_properties_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_pkey PRIMARY KEY (id);



ALTER TABLE ONLY brandings
    ADD CONSTRAINT brandings_pkey PRIMARY KEY (id);

--
-- TOC entry 3993 (class 1259 OID 939862)
-- Name: access_links_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE UNIQUE INDEX access_links_objectid_key ON public.access_links USING btree (objectid);


--
-- TOC entry 3996 (class 1259 OID 939863)
-- Name: access_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE UNIQUE INDEX access_points_objectid_key ON public.access_points USING btree (objectid);


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

CREATE UNIQUE INDEX connection_links_objectid_key ON public.connection_links USING btree (objectid);



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

CREATE INDEX index_route_id_on_routes_route_points ON routes_route_points USING btree (route_id);

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

CREATE INDEX index_stop_areas_on_parent_id ON public.stop_areas USING btree (parent_id);



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

CREATE UNIQUE INDEX stop_areas_objectid_key ON public.stop_areas USING btree (objectid);


--
-- TOC entry 4051 (class 1259 OID 939893)
-- Name: stop_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE UNIQUE INDEX stop_points_objectid_key ON stop_points USING btree (objectid);

CREATE UNIQUE INDEX route_points_objectid_key ON route_points USING btree (objectid);

CREATE UNIQUE INDEX scheduled_stop_points_objectid_key ON scheduled_stop_points USING btree (objectid);

--
-- TOC entry 4067 (class 1259 OID 939896)
-- Name: time_tables_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE UNIQUE INDEX time_tables_objectid_key ON time_tables USING btree (objectid);


CREATE UNIQUE INDEX brandings_objectid_key ON brandings USING btree (objectid);
--
-- TOC entry 4082 (class 1259 OID 939898)
-- Name: vehicle_journeys_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette; Tablespace:
--

CREATE UNIQUE INDEX vehicle_journeys_objectid_key ON vehicle_journeys USING btree (objectid);


--
-- TOC entry 4100 (class 2606 OID 939971)
-- Name: access_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.access_points
    ADD CONSTRAINT access_area_fkey FOREIGN KEY (stop_area_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4099 (class 2606 OID 939976)
-- Name: aclk_acpt_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (access_point_id) REFERENCES public.access_points(id) ON DELETE CASCADE;


--
-- TOC entry 4098 (class 2606 OID 939981)
-- Name: aclk_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stop_area_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4116 (class 2606 OID 939986)
-- Name: area_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas
    ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parent_id) REFERENCES public.stop_areas(id) ON DELETE SET NULL;


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

ALTER TABLE ONLY public.connection_links
    ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrival_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4101 (class 2606 OID 940001)
-- Name: colk_startarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.connection_links
    ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departure_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


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


ALTER TABLE ONLY routes_route_points
    ADD CONSTRAINT rrp_routepoint_fkey FOREIGN KEY (route_point_id) REFERENCES route_points(id) ON DELETE CASCADE;


ALTER TABLE ONLY routes_route_points
    ADD CONSTRAINT rrp_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;

--
-- TOC entry 4111 (class 2606 OID 940036)
-- Name: line_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY booking_arrangements
    ADD CONSTRAINT booking_arrangement_booking_contact_fkey FOREIGN KEY (booking_contact_id) REFERENCES contact_structures(id) ON DELETE SET NULL;

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_company_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;

ALTER TABLE ONLY lines
    ADD CONSTRAINT line_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES booking_arrangements(id) ON DELETE SET NULL;

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
-- TOC entry 4118 (class 2606 OID 940066)
-- Name: stoparea_child_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (child_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- TOC entry 4117 (class 2606 OID 940071)
-- Name: stoparea_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parent_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;



--
-- TOC entry 4119 (class 2606 OID 940081)
-- Name: stoppoint_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES booking_arrangements(id);

ALTER TABLE ONLY stop_points
    ADD CONSTRAINT stoppoint_scheduled_stop_fkey FOREIGN KEY (scheduled_stop_point_id) REFERENCES scheduled_stop_points(id);

ALTER TABLE ONLY route_points
    ADD CONSTRAINT routepoint_scheduled_stop_fkey FOREIGN KEY (scheduled_stop_point_id) REFERENCES scheduled_stop_points(id);


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

ALTER TABLE ONLY flexible_service_properties
    ADD CONSTRAINT fsp_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES booking_arrangements(id);

ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_route_fkey FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE;


ALTER TABLE ONLY vehicle_journeys
    ADD CONSTRAINT vj_fsp_fkey FOREIGN KEY (flexible_service_properties_id) REFERENCES flexible_service_properties(id);


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


ALTER TABLE ONLY chouette_gui.booking_arrangements_buy_when
    ADD CONSTRAINT booking_arrangement_buy_when FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id) ON DELETE CASCADE;


ALTER TABLE ONLY chouette_gui.booking_arrangements_booking_methods
    ADD CONSTRAINT booking_arrangements_booking_methods FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id) ON DELETE CASCADE;


ALTER TABLE ONLY chouette_gui.lines_key_values
    ADD CONSTRAINT lines_key_values_line_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;

ALTER TABLE ONLY chouette_gui.vehicle_journeys_key_values
    ADD CONSTRAINT vehicle_journeys_key_values_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id) ON DELETE CASCADE;


ALTER TABLE ONLY chouette_gui.companies
    ADD CONSTRAINT companies_brandings_fkey FOREIGN KEY (branding_id) REFERENCES chouette_gui.brandings(id) ON DELETE CASCADE;



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

create table referential_last_update
(
    id                    bigserial not null
        constraint referential_last_update_pkey
            primary key,
    last_update_timestamp timestamp
);
insert into referential_last_update(last_update_timestamp) values(current_timestamp);

create table if not exists dated_service_journeys
(
    id bigserial not null
        constraint dated_service_journeys_pkey
            primary key,
    objectid varchar not null,
    object_version integer,
    creation_time timestamp,
    creator_id varchar(255),
    operating_day date not null,
    vehicle_journey_id integer not null
        constraint dated_service_journeys_vehicle_journey_id_fkey
            references  vehicle_journeys,
    service_alteration varchar
);

alter table dated_service_journeys owner to chouette;

create unique index if not exists dated_service_journeys_objectid_key
    on dated_service_journeys (objectid);

alter sequence dated_service_journeys_id_seq owner to chouette;

create table if not exists  dated_service_journey_refs
(
    original_dsj_id integer
        constraint  dated_service_journey_refs_original_dsj_id_fkey
            references  dated_service_journeys,
    derived_dsj_id integer
        constraint  dated_service_journey_refs_derived_dsj_id_fkey
            references  dated_service_journeys
);

alter table  dated_service_journey_refs owner to chouette;

create unique index  dated_service_journey_refs_original_dsj_id_derived_dsj_id_key
    on   dated_service_journey_refs (original_dsj_id, derived_dsj_id);

create index  dated_service_journey_refs_derived_dsj_id_idx
    on   dated_service_journey_refs (derived_dsj_id);

create table blocks
(
    id bigserial not null
        constraint blocks_pkey
            primary key,
    objectid varchar not null,
    object_version integer,
    creation_time timestamp,
    creator_id varchar(255),
    private_code varchar,
    name varchar,
    description varchar,
    start_time time,
    end_time time,
    end_time_day_offset integer,
    start_point_id integer
        constraint blocks_scheduled_stop_points_start_point_id_fkey
            references scheduled_stop_points,
    end_point_id integer
        constraint blocks_scheduled_stop_points_end_point_id_fkey
            references scheduled_stop_points
);

alter table blocks owner to chouette;

create unique index blocks_objectid_key
    on blocks (objectid);

create index blocks_start_point_id_key
    on blocks (start_point_id);

create index blocks_end_point_id_key
    on blocks (end_point_id);


create table blocks_vehicle_journeys
(
    block_id integer
        constraint blocks_vehicle_journeys_block_id_fkey
            references blocks,
    vehicle_journey_id integer
        constraint blocks_vehicle_journeys_vehicle_journey_id_fkey
            references vehicle_journeys,
    position integer
);

alter table blocks_vehicle_journeys owner to chouette;

create unique index blocks_vehicle_journeys_block_id_vehicle_journey_id_key
    on blocks_vehicle_journeys (block_id, vehicle_journey_id);

create index blocks_vehicle_journeys_vehicle_journey_id_idx
    on blocks_vehicle_journeys (vehicle_journey_id);

create table time_tables_blocks
(
    block_id integer
        constraint time_tables_blocks_block_id_fkey
            references blocks,
    time_table_id integer
        constraint time_tables_blocks_time_table_id_fkey
            references time_tables
);

alter table time_tables_blocks owner to chouette;

create unique index time_tables_blocks_block_id_time_table_id_key
    on time_tables_blocks (time_table_id, block_id);

create index time_tables_blocks_block_id_idx
    on time_tables_blocks (block_id);

