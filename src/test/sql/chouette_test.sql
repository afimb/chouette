--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.22
-- Dumped by pg_dump version 9.6.23

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- SCRIPT CUSTOMIZATION ENTUR
DROP SCHEMA IF EXISTS chouette_gui CASCADE;
DROP SCHEMA IF EXISTS public CASCADE;
DROP EXTENSION IF EXISTS postgis CASCADE;

CREATE SCHEMA IF NOT EXISTS shared_extensions;
CREATE EXTENSION postgis SCHEMA shared_extensions;

CREATE SCHEMA chouette_gui;
ALTER SCHEMA chouette_gui OWNER TO chouette;

CREATE SCHEMA public;
ALTER SCHEMA public OWNER TO chouette;


SET search_path = chouette_gui, pg_catalog;
-- END SCRIPT CUSTOMIZATION ENTUR

--
-- Name: clone_schema(text, text); Type: FUNCTION; Schema: chouette_gui; Owner: chouette
--

CREATE FUNCTION chouette_gui.clone_schema(source_schema text, dest_schema text) RETURNS void
    LANGUAGE plpgsql
AS $$

DECLARE
    object text;
    buffer text;
    default_ text;
    column_ text;
    constraint_name_ text;
    constraint_def_ text;
BEGIN
    EXECUTE 'DROP SCHEMA IF EXISTS ' || dest_schema || ' CASCADE';
    EXECUTE 'CREATE SCHEMA ' || dest_schema ;
    SET search_path TO dest_schema;

    FOR object IN
        SELECT sequence_name::text FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
        LOOP
            EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || object;
        END LOOP;

    FOR object IN
        SELECT TABLE_NAME::text FROM information_schema.TABLES WHERE table_schema = source_schema
        LOOP
            buffer := dest_schema || '.' || object;
            EXECUTE 'CREATE TABLE ' || buffer || ' (LIKE ' || source_schema || '.' || object || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';

            FOR column_, default_ IN
                SELECT column_name::text, REPLACE(column_default::text, source_schema, dest_schema) FROM information_schema.COLUMNS WHERE table_schema = dest_schema AND TABLE_NAME = object AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
                LOOP
                    EXECUTE 'ALTER TABLE ' || buffer || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
                END LOOP;
        END LOOP;

    -- reiterate tables and create foreign keys
    FOR object IN
        SELECT table_name::text FROM information_schema.TABLES WHERE table_schema = source_schema
        LOOP
            buffer := dest_schema || '.' || object;

            -- create foreign keys
            FOR constraint_name_, constraint_def_ IN
                SELECT conname::text, REPLACE(pg_get_constraintdef(pg_constraint.oid), source_schema||'.', dest_schema||'.')  FROM pg_constraint INNER JOIN pg_class ON conrelid=pg_class.oid INNER JOIN pg_namespace ON pg_namespace.oid=pg_class.relnamespace WHERE contype='f' and relname=object and nspname=source_schema
                LOOP
                    --raise notice 'constraint "%" "%"', constraint_name_, constraint_def_;
                    EXECUTE 'ALTER TABLE '|| buffer ||' ADD CONSTRAINT '|| constraint_name_ ||' '|| constraint_def_;
                END LOOP;
        END LOOP;

    buffer := dest_schema || '.schema_migrations';

    EXECUTE 'INSERT INTO ' || buffer || ' SELECT * from public.schema_migrations';
    SET search_path TO public;

END;

$$;


ALTER FUNCTION chouette_gui.clone_schema(source_schema text, dest_schema text) OWNER TO chouette;

--
-- Name: create_provider_schema(text, text, text, text, text, text, text, text, text, text, text, text, text, text, text, text); Type: FUNCTION; Schema: chouette_gui; Owner: chouette
--

CREATE FUNCTION chouette_gui.create_provider_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, admin_user_email text, admin_user_encrypted_password text, user_name text, user_email text, user_initial_encrypted_password text, organisation_name text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) RETURNS integer
    LANGUAGE plpgsql
AS $_$

DECLARE
BEGIN
    raise notice 'create schema "%"', dest_schema;
    PERFORM public.clone_schema('public',dest_schema);
    raise notice 'Reassigning ownership from postgres to chouette. db: % user: % schema: %', current_database(), current_user, current_schema();
    set search_path to dest_schema;
    --REASSIGN OWNED BY postgres TO chouette;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING 'NSR', 'http://www.rutebanken.org/ns/nsr', current_timestamp;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING xmlns, xmlns_url, current_timestamp;
    set search_path to public;
    insert into public.organisations(name,created_at,updated_at,data_format) values (organisation_name,current_timestamp,current_timestamp,dataspace_format);
    insert into public.users(email, encrypted_password,organisation_id,name,confirmed_at,role) values (admin_user_email,admin_user_encrypted_password,  currval(pg_get_serial_sequence('organisations','id')),admin_user_name,current_timestamp,2);
    insert into public.users(email, encrypted_password,organisation_id,name,confirmed_at) values (user_email,user_initial_encrypted_password,  currval(pg_get_serial_sequence('organisations','id')),user_name,current_timestamp);
    insert into public.referentials(name,slug,created_at,updated_at,prefix,projection_type, time_zone, bounds,organisation_id , geographical_bounds , user_id ,user_name ,data_format ) values(dataspace_name,dest_schema, current_timestamp,current_timestamp,dataspace_prefix,dataspace_projection,dataspace_timezone,dataspace_bounds,currval(pg_get_serial_sequence('organisations','id')),null,currval(pg_get_serial_sequence('users','id')),admin_user_name,dataspace_format);
    RETURN 1;
END;

$_$;


ALTER FUNCTION chouette_gui.create_provider_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, admin_user_email text, admin_user_encrypted_password text, user_name text, user_email text, user_initial_encrypted_password text, organisation_name text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) OWNER TO chouette;

--
-- Name: create_rutebanken_schema(text, text, text, text, text, text, text, text, text, text, text, text); Type: FUNCTION; Schema: chouette_gui; Owner: chouette
--

CREATE FUNCTION chouette_gui.create_rutebanken_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, master_organisation_name text, master_user_email text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) RETURNS integer
    LANGUAGE plpgsql
AS $_$

DECLARE
    master_user_id   integer;
    master_organisation_id   integer;
BEGIN
    PERFORM public.clone_schema('public',dest_schema);

    select id into master_user_id from public.users where email = master_user_email;
    select id into master_organisation_id from public.organisations where name = master_organisation_name;


    -- REASSIGN OWNED BY postgres TO chouette;
    insert into public.referentials(name,slug,created_at,updated_at,prefix,projection_type, time_zone, bounds,organisation_id , geographical_bounds , user_id ,user_name ,data_format )
    values(dataspace_name,dest_schema, current_timestamp,current_timestamp,dataspace_prefix,dataspace_projection,dataspace_timezone,dataspace_bounds,master_organisation_id,null,master_user_id,admin_user_name,dataspace_format);

    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING 'NSR', 'http://www.rutebanken.org/ns/nsr', current_timestamp;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING xmlns, xmlns_url, current_timestamp;
    RETURN 1;
END;

$_$;


ALTER FUNCTION chouette_gui.create_rutebanken_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, master_organisation_name text, master_user_email text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) OWNER TO chouette;

--
-- Name: clone_schema(text, text); Type: FUNCTION; Schema: public; Owner: chouette
--

CREATE FUNCTION public.clone_schema(source_schema text, dest_schema text) RETURNS void
    LANGUAGE plpgsql
AS $$

DECLARE
    object text;
    buffer text;
    default_ text;
    column_ text;
    constraint_name_ text;
    constraint_def_ text;
BEGIN
    EXECUTE 'DROP SCHEMA IF EXISTS ' || dest_schema || ' CASCADE';
    EXECUTE 'CREATE SCHEMA ' || dest_schema ;
    SET search_path TO dest_schema;

    FOR object IN
        SELECT sequence_name::text FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
        LOOP
            EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || object;
        END LOOP;

    FOR object IN
        SELECT TABLE_NAME::text FROM information_schema.TABLES WHERE table_schema = source_schema
        LOOP
            buffer := dest_schema || '.' || object;
            EXECUTE 'CREATE TABLE ' || buffer || ' (LIKE ' || source_schema || '.' || object || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';

            FOR column_, default_ IN
                SELECT column_name::text, REPLACE(column_default::text, source_schema, dest_schema) FROM information_schema.COLUMNS WHERE table_schema = dest_schema AND TABLE_NAME = object AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
                LOOP
                    EXECUTE 'ALTER TABLE ' || buffer || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
                END LOOP;
        END LOOP;

    -- reiterate tables and create foreign keys
    FOR object IN
        SELECT table_name::text FROM information_schema.TABLES WHERE table_schema = source_schema
        LOOP
            buffer := dest_schema || '.' || object;

            -- create foreign keys
            FOR constraint_name_, constraint_def_ IN
                SELECT conname::text, REPLACE(pg_get_constraintdef(pg_constraint.oid), source_schema||'.', dest_schema||'.')  FROM pg_constraint INNER JOIN pg_class ON conrelid=pg_class.oid INNER JOIN pg_namespace ON pg_namespace.oid=pg_class.relnamespace WHERE contype='f' and relname=object and nspname=source_schema
                LOOP
                    --raise notice 'constraint "%" "%"', constraint_name_, constraint_def_;
                    EXECUTE 'ALTER TABLE '|| buffer ||' ADD CONSTRAINT '|| constraint_name_ ||' '|| constraint_def_;
                END LOOP;
        END LOOP;

    buffer := dest_schema || '.schema_migrations';

    EXECUTE 'INSERT INTO ' || buffer || ' SELECT * from public.schema_migrations';
    SET search_path TO public;

END;

$$;


ALTER FUNCTION public.clone_schema(source_schema text, dest_schema text) OWNER TO chouette;

--
-- Name: create_provider_schema(text, text, text, text, text, text, text, text, text, text, text, text, text, text, text, text); Type: FUNCTION; Schema: public; Owner: chouette
--

CREATE FUNCTION public.create_provider_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, admin_user_email text, admin_user_encrypted_password text, user_name text, user_email text, user_initial_encrypted_password text, organisation_name text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) RETURNS integer
    LANGUAGE plpgsql
AS $_$

DECLARE
BEGIN
    raise notice 'create schema "%"', dest_schema;
    PERFORM public.clone_schema('public',dest_schema);
    raise notice 'Reassigning ownership from postgres to chouette. db: % user: % schema: %', current_database(), current_user, current_schema();
    set search_path to dest_schema;
    --REASSIGN OWNED BY postgres TO chouette;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING 'NSR', 'http://www.rutebanken.org/ns/nsr', current_timestamp;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING xmlns, xmlns_url, current_timestamp;
    set search_path to public;
    insert into public.organisations(name,created_at,updated_at,data_format) values (organisation_name,current_timestamp,current_timestamp,dataspace_format);
    insert into public.users(email, encrypted_password,organisation_id,name,confirmed_at,role) values (admin_user_email,admin_user_encrypted_password,  currval(pg_get_serial_sequence('organisations','id')),admin_user_name,current_timestamp,2);
    insert into public.users(email, encrypted_password,organisation_id,name,confirmed_at) values (user_email,user_initial_encrypted_password,  currval(pg_get_serial_sequence('organisations','id')),user_name,current_timestamp);
    insert into public.referentials(name,slug,created_at,updated_at,prefix,projection_type, time_zone, bounds,organisation_id , geographical_bounds , user_id ,user_name ,data_format ) values(dataspace_name,dest_schema, current_timestamp,current_timestamp,dataspace_prefix,dataspace_projection,dataspace_timezone,dataspace_bounds,currval(pg_get_serial_sequence('organisations','id')),null,currval(pg_get_serial_sequence('users','id')),admin_user_name,dataspace_format);
    RETURN 1;
END;

$_$;


ALTER FUNCTION public.create_provider_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, admin_user_email text, admin_user_encrypted_password text, user_name text, user_email text, user_initial_encrypted_password text, organisation_name text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) OWNER TO chouette;

--
-- Name: create_rutebanken_schema(text, text, text, text, text, text, text, text, text, text, text, text); Type: FUNCTION; Schema: public; Owner: chouette
--

CREATE FUNCTION public.create_rutebanken_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, master_organisation_name text, master_user_email text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) RETURNS integer
    LANGUAGE plpgsql
AS $_$

DECLARE
    master_user_id   integer;
    master_organisation_id   integer;
BEGIN
    PERFORM public.clone_schema('public',dest_schema);

    select id into master_user_id from public.users where email = master_user_email;
    select id into master_organisation_id from public.organisations where name = master_organisation_name;


    -- REASSIGN OWNED BY postgres TO chouette;
    insert into public.referentials(name,slug,created_at,updated_at,prefix,projection_type, time_zone, bounds,organisation_id , geographical_bounds , user_id ,user_name ,data_format )
    values(dataspace_name,dest_schema, current_timestamp,current_timestamp,dataspace_prefix,dataspace_projection,dataspace_timezone,dataspace_bounds,master_organisation_id,null,master_user_id,admin_user_name,dataspace_format);

    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING 'NSR', 'http://www.rutebanken.org/ns/nsr', current_timestamp;
    execute 'insert into ' || dest_schema  || '.codespaces ( xmlns, xmlns_url, created_at ) values ( $1, $2, $3 )' USING xmlns, xmlns_url, current_timestamp;
    RETURN 1;
END;

$_$;


ALTER FUNCTION public.create_rutebanken_schema(dest_schema text, dataspace_name text, dataspace_format text, admin_user_name text, master_organisation_name text, master_user_email text, dataspace_prefix text, dataspace_projection text, dataspace_timezone text, dataspace_bounds text, xmlns text, xmlns_url text) OWNER TO chouette;

--
-- Name: get_variable(text); Type: FUNCTION; Schema: public; Owner: chouette
--

CREATE FUNCTION public.get_variable(p_var text) RETURNS bigint
    LANGUAGE plpgsql
AS $_$
DECLARE
    v_val TEXT;
BEGIN
    execute 'CREATE temp TABLE IF NOT exists sys_variables ( variable TEXT PRIMARY KEY, value bigint );';
    execute 'SELECT value FROM sys_variables WHERE variable = $1' INTO v_val USING p_var;
    RETURN v_val;
END;
$_$;


ALTER FUNCTION public.get_variable(p_var text) OWNER TO chouette;

--
-- Name: set_variable(text, bigint); Type: FUNCTION; Schema: public; Owner: chouette
--

CREATE FUNCTION public.set_variable(p_var text, p_val bigint) RETURNS void
    LANGUAGE plpgsql
AS $_$
DECLARE
    v_var TEXT;
BEGIN
    execute 'CREATE temp TABLE IF NOT exists sys_variables ( variable TEXT PRIMARY KEY, value bigint );';
    LOOP
        execute 'UPDATE sys_variables SET value = $1 WHERE variable = $2 returning variable' INTO v_var USING p_val, p_var;
        IF v_var IS NOT NULL THEN
            RETURN;
        END IF;
        BEGIN
            execute 'INSERT INTO sys_variables ( variable, value ) VALUES ( $1, $2 )' USING p_var, p_val;
            RETURN;
        EXCEPTION WHEN unique_violation THEN
        -- ignore, re-process the loop
        END;
    END LOOP;
END;
$_$;


ALTER FUNCTION public.set_variable(p_var text, p_val bigint) OWNER TO chouette;

--
-- Name: access_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.access_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.access_links_id_seq OWNER TO chouette;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: access_links; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.access_links (
                                           id bigint DEFAULT nextval('chouette_gui.access_links_id_seq'::regclass) NOT NULL,
                                           access_point_id bigint,
                                           stop_area_id bigint,
                                           objectid character varying NOT NULL,
                                           object_version integer,
                                           creation_time timestamp without time zone,
                                           creator_id character varying,
                                           name character varying,
                                           comment character varying,
                                           link_distance numeric(19,2),
                                           lift_availability boolean,
                                           mobility_restricted_suitability boolean,
                                           stairs_availability boolean,
                                           default_duration time without time zone,
                                           frequent_traveller_duration time without time zone,
                                           occasional_traveller_duration time without time zone,
                                           mobility_restricted_traveller_duration time without time zone,
                                           link_type character varying,
                                           int_user_needs integer,
                                           link_orientation character varying
);


ALTER TABLE chouette_gui.access_links OWNER TO chouette;

--
-- Name: access_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.access_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.access_points_id_seq OWNER TO chouette;

--
-- Name: access_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.access_points (
                                            id bigint DEFAULT nextval('chouette_gui.access_points_id_seq'::regclass) NOT NULL,
                                            objectid character varying,
                                            object_version integer,
                                            creation_time timestamp without time zone,
                                            creator_id character varying,
                                            name character varying,
                                            comment character varying,
                                            longitude numeric(19,16),
                                            latitude numeric(19,16),
                                            long_lat_type character varying,
                                            country_code character varying,
                                            street_name character varying,
                                            contained_in character varying,
                                            openning_time time without time zone,
                                            closing_time time without time zone,
                                            access_type character varying,
                                            lift_availability boolean,
                                            mobility_restricted_suitability boolean,
                                            stairs_availability boolean,
                                            stop_area_id bigint,
                                            zip_code character varying,
                                            city_name character varying
);


ALTER TABLE chouette_gui.access_points OWNER TO chouette;

--
-- Name: api_keys_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.api_keys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.api_keys_id_seq OWNER TO chouette;

--
-- Name: api_keys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.api_keys (
                                       id bigint DEFAULT nextval('chouette_gui.api_keys_id_seq'::regclass) NOT NULL,
                                       referential_id bigint,
                                       token character varying,
                                       name character varying,
                                       created_at timestamp without time zone,
                                       updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.api_keys OWNER TO chouette;

--
-- Name: blocks; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.blocks (
                                     id bigint NOT NULL,
                                     objectid character varying NOT NULL,
                                     object_version integer,
                                     creation_time timestamp without time zone,
                                     creator_id character varying(255),
                                     private_code character varying,
                                     name character varying,
                                     description character varying,
                                     start_time time without time zone,
                                     end_time time without time zone,
                                     end_time_day_offset integer,
                                     start_point_id integer,
                                     end_point_id integer
);


ALTER TABLE chouette_gui.blocks OWNER TO chouette;

--
-- Name: blocks_dead_runs; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.blocks_dead_runs (
                                               block_id integer NOT NULL,
                                               dead_run_id integer NOT NULL,
                                               "position" integer
);


ALTER TABLE chouette_gui.blocks_dead_runs OWNER TO chouette;

--
-- Name: blocks_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.blocks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.blocks_id_seq OWNER TO chouette;

--
-- Name: blocks_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.blocks_id_seq OWNED BY chouette_gui.blocks.id;


--
-- Name: blocks_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.blocks_vehicle_journeys (
                                                      block_id integer NOT NULL,
                                                      vehicle_journey_id integer NOT NULL,
                                                      "position" integer
);


ALTER TABLE chouette_gui.blocks_vehicle_journeys OWNER TO chouette;

--
-- Name: booking_arrangements; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.booking_arrangements (
                                                   id bigint NOT NULL,
                                                   booking_note character varying,
                                                   booking_access character varying,
                                                   book_when character varying,
                                                   latest_booking_time time without time zone,
                                                   minimum_booking_period time without time zone,
                                                   booking_contact_id bigint
);


ALTER TABLE chouette_gui.booking_arrangements OWNER TO chouette;

--
-- Name: booking_arrangements_booking_methods; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.booking_arrangements_booking_methods (
                                                                   booking_arrangement_id bigint,
                                                                   booking_method character varying
);


ALTER TABLE chouette_gui.booking_arrangements_booking_methods OWNER TO chouette;

--
-- Name: booking_arrangements_buy_when; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.booking_arrangements_buy_when (
                                                            booking_arrangement_id bigint,
                                                            buy_when character varying
);


ALTER TABLE chouette_gui.booking_arrangements_buy_when OWNER TO chouette;

--
-- Name: booking_arrangements_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.booking_arrangements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.booking_arrangements_id_seq OWNER TO chouette;

--
-- Name: booking_arrangements_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.booking_arrangements_id_seq OWNED BY chouette_gui.booking_arrangements.id;


--
-- Name: brandings; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.brandings (
                                        id bigint NOT NULL,
                                        objectid character varying NOT NULL,
                                        object_version integer,
                                        creation_time timestamp without time zone,
                                        creator_id character varying(255),
                                        name character varying,
                                        description character varying,
                                        url character varying,
                                        image character varying
);


ALTER TABLE chouette_gui.brandings OWNER TO chouette;

--
-- Name: brandings_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.brandings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.brandings_id_seq OWNER TO chouette;

--
-- Name: brandings_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.brandings_id_seq OWNED BY chouette_gui.brandings.id;


--
-- Name: codespaces_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.codespaces_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.codespaces_id_seq OWNER TO chouette;

--
-- Name: codespaces; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.codespaces (
                                         id bigint DEFAULT nextval('chouette_gui.codespaces_id_seq'::regclass) NOT NULL,
                                         xmlns character varying NOT NULL,
                                         xmlns_url character varying NOT NULL,
                                         created_at timestamp without time zone,
                                         updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.codespaces OWNER TO chouette;

--
-- Name: companies_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.companies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.companies_id_seq OWNER TO chouette;

--
-- Name: companies; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.companies (
                                        id bigint DEFAULT nextval('chouette_gui.companies_id_seq'::regclass) NOT NULL,
                                        objectid character varying NOT NULL,
                                        object_version integer,
                                        creation_time timestamp without time zone,
                                        creator_id character varying,
                                        name character varying,
                                        short_name character varying,
                                        organizational_unit character varying,
                                        operating_department_name character varying,
                                        code character varying,
                                        phone character varying,
                                        fax character varying,
                                        email character varying,
                                        registration_number character varying,
                                        url character varying,
                                        time_zone character varying,
                                        organisation_type character varying,
                                        legal_name character varying,
                                        public_email character varying,
                                        public_url character varying,
                                        public_phone character varying,
                                        branding_id bigint
);


ALTER TABLE chouette_gui.companies OWNER TO chouette;

--
-- Name: connection_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.connection_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.connection_links_id_seq OWNER TO chouette;

--
-- Name: connection_links; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.connection_links (
                                               id bigint DEFAULT nextval('chouette_gui.connection_links_id_seq'::regclass) NOT NULL,
                                               departure_id bigint,
                                               arrival_id bigint,
                                               objectid character varying NOT NULL,
                                               object_version integer,
                                               creation_time timestamp without time zone,
                                               creator_id character varying,
                                               name character varying,
                                               comment character varying,
                                               link_distance numeric(19,2),
                                               link_type character varying,
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
-- Name: contact_structures; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.contact_structures (
                                                 id bigint NOT NULL,
                                                 contact_person character varying,
                                                 email character varying,
                                                 phone character varying,
                                                 fax character varying,
                                                 url character varying,
                                                 further_details character varying
);


ALTER TABLE chouette_gui.contact_structures OWNER TO chouette;

--
-- Name: contact_structures_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.contact_structures_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.contact_structures_id_seq OWNER TO chouette;

--
-- Name: contact_structures_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.contact_structures_id_seq OWNED BY chouette_gui.contact_structures.id;


--
-- Name: dated_service_journey_refs; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.dated_service_journey_refs (
                                                         original_dsj_id integer,
                                                         derived_dsj_id integer
);


ALTER TABLE chouette_gui.dated_service_journey_refs OWNER TO chouette;

--
-- Name: dated_service_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.dated_service_journeys (
                                                     id bigint NOT NULL,
                                                     objectid character varying NOT NULL,
                                                     object_version integer,
                                                     creation_time timestamp without time zone,
                                                     creator_id character varying(255),
                                                     operating_day date NOT NULL,
                                                     vehicle_journey_id integer NOT NULL,
                                                     service_alteration character varying
);


ALTER TABLE chouette_gui.dated_service_journeys OWNER TO chouette;

--
-- Name: dated_service_journeys_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.dated_service_journeys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.dated_service_journeys_id_seq OWNER TO chouette;

--
-- Name: dated_service_journeys_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.dated_service_journeys_id_seq OWNED BY chouette_gui.dated_service_journeys.id;


--
-- Name: dead_run_at_stops; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.dead_run_at_stops (
                                                id bigint NOT NULL,
                                                objectid character varying NOT NULL,
                                                object_version integer,
                                                creation_time timestamp without time zone,
                                                creator_id character varying,
                                                dead_run_id bigint,
                                                stop_point_id bigint,
                                                arrival_time time without time zone,
                                                departure_time time without time zone,
                                                arrival_day_offset integer,
                                                departure_day_offset integer
);


ALTER TABLE chouette_gui.dead_run_at_stops OWNER TO chouette;

--
-- Name: dead_run_at_stops_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.dead_run_at_stops_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.dead_run_at_stops_id_seq OWNER TO chouette;

--
-- Name: dead_run_at_stops_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.dead_run_at_stops_id_seq OWNED BY chouette_gui.dead_run_at_stops.id;


--
-- Name: dead_runs; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.dead_runs (
                                        id bigint NOT NULL,
                                        journey_pattern_id bigint,
                                        objectid character varying NOT NULL,
                                        object_version integer,
                                        creation_time timestamp without time zone,
                                        creator_id character varying
);


ALTER TABLE chouette_gui.dead_runs OWNER TO chouette;

--
-- Name: dead_runs_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.dead_runs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.dead_runs_id_seq OWNER TO chouette;

--
-- Name: dead_runs_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.dead_runs_id_seq OWNED BY chouette_gui.dead_runs.id;


--
-- Name: delayed_jobs_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.delayed_jobs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.delayed_jobs_id_seq OWNER TO chouette;

--
-- Name: delayed_jobs; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.delayed_jobs (
                                           id bigint DEFAULT nextval('chouette_gui.delayed_jobs_id_seq'::regclass) NOT NULL,
                                           priority integer DEFAULT 0,
                                           attempts integer DEFAULT 0,
                                           handler text,
                                           last_error text,
                                           run_at timestamp without time zone,
                                           locked_at timestamp without time zone,
                                           failed_at timestamp without time zone,
                                           locked_by character varying,
                                           queue character varying,
                                           created_at timestamp without time zone,
                                           updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.delayed_jobs OWNER TO chouette;

--
-- Name: destination_display_via; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.destination_display_via (
                                                      destination_display_id bigint NOT NULL,
                                                      via_id bigint NOT NULL,
                                                      "position" bigint,
                                                      created_at timestamp without time zone,
                                                      updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.destination_display_via OWNER TO chouette;

--
-- Name: destination_displays; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.destination_displays (
                                                   id bigint NOT NULL,
                                                   name character varying,
                                                   side_text character varying,
                                                   front_text character varying NOT NULL,
                                                   objectid character varying NOT NULL,
                                                   object_version bigint,
                                                   creation_time timestamp without time zone,
                                                   creator_id character varying
);


ALTER TABLE chouette_gui.destination_displays OWNER TO chouette;

--
-- Name: destination_displays_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.destination_displays_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.destination_displays_id_seq OWNER TO chouette;

--
-- Name: destination_displays_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.destination_displays_id_seq OWNED BY chouette_gui.destination_displays.id;


--
-- Name: exports_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.exports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.exports_id_seq OWNER TO chouette;

--
-- Name: exports; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.exports (
                                      id bigint DEFAULT nextval('chouette_gui.exports_id_seq'::regclass) NOT NULL,
                                      referential_id bigint,
                                      status character varying,
                                      type character varying,
                                      options character varying,
                                      created_at timestamp without time zone,
                                      updated_at timestamp without time zone,
                                      references_type character varying,
                                      reference_ids character varying
);


ALTER TABLE chouette_gui.exports OWNER TO chouette;

--
-- Name: facilities_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.facilities_id_seq OWNER TO chouette;

--
-- Name: facilities; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.facilities (
                                         id bigint DEFAULT nextval('chouette_gui.facilities_id_seq'::regclass) NOT NULL,
                                         stop_area_id bigint,
                                         line_id bigint,
                                         connection_link_id bigint,
                                         stop_point_id bigint,
                                         objectid character varying NOT NULL,
                                         object_version integer,
                                         creation_time timestamp without time zone,
                                         creator_id character varying,
                                         name character varying,
                                         comment character varying,
                                         description character varying,
                                         free_access boolean,
                                         longitude numeric(19,16),
                                         latitude numeric(19,16),
                                         long_lat_type character varying,
                                         x numeric(19,2),
                                         y numeric(19,2),
                                         projection_type character varying,
                                         country_code character varying,
                                         street_name character varying,
                                         contained_in character varying
);


ALTER TABLE chouette_gui.facilities OWNER TO chouette;

--
-- Name: facilities_features; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.facilities_features (
                                                  facility_id bigint,
                                                  choice_code integer
);


ALTER TABLE chouette_gui.facilities_features OWNER TO chouette;

--
-- Name: flexible_service_properties; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.flexible_service_properties (
                                                          id bigint NOT NULL,
                                                          objectid character varying NOT NULL,
                                                          object_version integer,
                                                          creation_time timestamp without time zone,
                                                          creator_id character varying(255),
                                                          flexible_service_type character varying(255),
                                                          cancellation_possible boolean,
                                                          change_of_time_possible boolean,
                                                          booking_arrangement_id bigint
);


ALTER TABLE chouette_gui.flexible_service_properties OWNER TO chouette;

--
-- Name: flexible_service_properties_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.flexible_service_properties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.flexible_service_properties_id_seq OWNER TO chouette;

--
-- Name: flexible_service_properties_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.flexible_service_properties_id_seq OWNED BY chouette_gui.flexible_service_properties.id;


--
-- Name: footnote_alternative_texts; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnote_alternative_texts (
                                                         id bigint NOT NULL,
                                                         objectid character varying NOT NULL,
                                                         object_version integer,
                                                         creation_time timestamp without time zone,
                                                         creator_id character varying(255),
                                                         footnote_id integer NOT NULL,
                                                         text character varying,
                                                         language character varying
);


ALTER TABLE chouette_gui.footnote_alternative_texts OWNER TO chouette;

--
-- Name: footnote_alternative_texts_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.footnote_alternative_texts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.footnote_alternative_texts_id_seq OWNER TO chouette;

--
-- Name: footnote_alternative_texts_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.footnote_alternative_texts_id_seq OWNED BY chouette_gui.footnote_alternative_texts.id;


--
-- Name: footnotes_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.footnotes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.footnotes_id_seq OWNER TO chouette;

--
-- Name: footnotes; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes (
                                        id bigint DEFAULT nextval('chouette_gui.footnotes_id_seq'::regclass) NOT NULL,
                                        code character varying,
                                        label character varying,
                                        creation_time timestamp without time zone,
                                        objectid character varying NOT NULL,
                                        object_version integer,
                                        creator_id character varying
);


ALTER TABLE chouette_gui.footnotes OWNER TO chouette;

--
-- Name: footnotes_journey_patterns; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes_journey_patterns (
                                                         journey_pattern_id bigint,
                                                         footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_journey_patterns OWNER TO chouette;

--
-- Name: footnotes_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes_lines (
                                              line_id bigint,
                                              footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_lines OWNER TO chouette;

--
-- Name: footnotes_stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes_stop_points (
                                                    stop_point_id bigint,
                                                    footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_stop_points OWNER TO chouette;

--
-- Name: footnotes_vehicle_journey_at_stops; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes_vehicle_journey_at_stops (
                                                                 vehicle_journey_at_stop_id bigint,
                                                                 footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_vehicle_journey_at_stops OWNER TO chouette;

--
-- Name: footnotes_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.footnotes_vehicle_journeys (
                                                         vehicle_journey_id bigint,
                                                         footnote_id bigint
);


ALTER TABLE chouette_gui.footnotes_vehicle_journeys OWNER TO chouette;

--
-- Name: group_of_lines_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.group_of_lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.group_of_lines_id_seq OWNER TO chouette;

--
-- Name: group_of_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.group_of_lines (
                                             id bigint DEFAULT nextval('chouette_gui.group_of_lines_id_seq'::regclass) NOT NULL,
                                             objectid character varying NOT NULL,
                                             object_version integer,
                                             creation_time timestamp without time zone,
                                             creator_id character varying,
                                             name character varying,
                                             comment character varying,
                                             registration_number character varying
);


ALTER TABLE chouette_gui.group_of_lines OWNER TO chouette;

--
-- Name: group_of_lines_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.group_of_lines_lines (
                                                   group_of_line_id bigint,
                                                   line_id bigint
);


ALTER TABLE chouette_gui.group_of_lines_lines OWNER TO chouette;

--
-- Name: interchanges; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.interchanges (
                                           id bigint NOT NULL,
                                           objectid character varying NOT NULL,
                                           object_version integer,
                                           creation_time timestamp without time zone,
                                           creator_id character varying,
                                           name character varying,
                                           priority integer,
                                           planned boolean,
                                           guaranteed boolean,
                                           advertised boolean,
                                           maximum_wait_time time without time zone,
                                           from_point character varying,
                                           to_point character varying,
                                           from_vehicle_journey character varying,
                                           to_vehicle_journey character varying,
                                           stay_seated boolean,
                                           minimum_transfer_time time without time zone,
                                           from_visit_number integer,
                                           to_visit_number integer
);


ALTER TABLE chouette_gui.interchanges OWNER TO chouette;

--
-- Name: interchanges_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.interchanges_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.interchanges_id_seq OWNER TO chouette;

--
-- Name: interchanges_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.interchanges_id_seq OWNED BY chouette_gui.interchanges.id;


--
-- Name: journey_frequencies_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.journey_frequencies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_frequencies_id_seq OWNER TO chouette;

--
-- Name: journey_frequencies; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.journey_frequencies (
                                                  id bigint DEFAULT nextval('chouette_gui.journey_frequencies_id_seq'::regclass) NOT NULL,
                                                  vehicle_journey_id bigint,
                                                  scheduled_headway_interval time without time zone NOT NULL,
                                                  first_departure_time time without time zone NOT NULL,
                                                  last_departure_time time without time zone,
                                                  exact_time boolean DEFAULT false,
                                                  created_at timestamp without time zone,
                                                  updated_at timestamp without time zone,
                                                  timeband_id bigint
);


ALTER TABLE chouette_gui.journey_frequencies OWNER TO chouette;

--
-- Name: journey_pattern_sections_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.journey_pattern_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_pattern_sections_id_seq OWNER TO chouette;

--
-- Name: journey_pattern_sections; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.journey_pattern_sections (
                                                       id bigint DEFAULT nextval('chouette_gui.journey_pattern_sections_id_seq'::regclass) NOT NULL,
                                                       journey_pattern_id bigint NOT NULL,
                                                       route_section_id bigint NOT NULL,
                                                       rank integer NOT NULL,
                                                       created_at timestamp without time zone,
                                                       updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.journey_pattern_sections OWNER TO chouette;

--
-- Name: journey_patterns_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.journey_patterns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.journey_patterns_id_seq OWNER TO chouette;

--
-- Name: journey_patterns; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.journey_patterns (
                                               id bigint DEFAULT nextval('chouette_gui.journey_patterns_id_seq'::regclass) NOT NULL,
                                               route_id bigint,
                                               objectid character varying NOT NULL,
                                               object_version integer,
                                               creation_time timestamp without time zone,
                                               creator_id character varying,
                                               name character varying,
                                               comment character varying,
                                               registration_number character varying,
                                               published_name character varying,
                                               departure_stop_point_id bigint,
                                               arrival_stop_point_id bigint,
                                               section_status integer DEFAULT 0 NOT NULL
);


ALTER TABLE chouette_gui.journey_patterns OWNER TO chouette;

--
-- Name: journey_patterns_stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.journey_patterns_stop_points (
                                                           journey_pattern_id bigint,
                                                           stop_point_id bigint
);


ALTER TABLE chouette_gui.journey_patterns_stop_points OWNER TO chouette;

--
-- Name: lines_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.lines_id_seq OWNER TO chouette;

--
-- Name: lines; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.lines (
                                    id bigint DEFAULT nextval('chouette_gui.lines_id_seq'::regclass) NOT NULL,
                                    network_id bigint,
                                    company_id bigint,
                                    objectid character varying NOT NULL,
                                    object_version integer,
                                    creation_time timestamp without time zone,
                                    creator_id character varying,
                                    name character varying,
                                    number character varying,
                                    published_name character varying,
                                    transport_mode_name character varying,
                                    registration_number character varying,
                                    comment character varying,
                                    mobility_restricted_suitability boolean,
                                    int_user_needs integer,
                                    flexible_service boolean,
                                    url character varying,
                                    color character varying(6),
                                    text_color character varying(6),
                                    stable_id character varying,
                                    transport_submode_name character varying,
                                    flexible_line_type character varying,
                                    booking_arrangement_id bigint
);


ALTER TABLE chouette_gui.lines OWNER TO chouette;

--
-- Name: lines_key_values; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.lines_key_values (
                                               line_id bigint,
                                               type_of_key character varying,
                                               key character varying,
                                               value character varying
);


ALTER TABLE chouette_gui.lines_key_values OWNER TO chouette;

--
-- Name: networks_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.networks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.networks_id_seq OWNER TO chouette;

--
-- Name: networks; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.networks (
                                       id bigint DEFAULT nextval('chouette_gui.networks_id_seq'::regclass) NOT NULL,
                                       objectid character varying NOT NULL,
                                       object_version integer,
                                       creation_time timestamp without time zone,
                                       creator_id character varying,
                                       version_date date,
                                       description character varying,
                                       name character varying,
                                       registration_number character varying,
                                       source_name character varying,
                                       source_type character varying,
                                       source_identifier character varying,
                                       comment character varying,
                                       company_id bigint
);


ALTER TABLE chouette_gui.networks OWNER TO chouette;

--
-- Name: organisations_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.organisations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.organisations_id_seq OWNER TO chouette;

--
-- Name: organisations; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.organisations (
                                            id bigint DEFAULT nextval('chouette_gui.organisations_id_seq'::regclass) NOT NULL,
                                            name character varying,
                                            created_at timestamp without time zone,
                                            updated_at timestamp without time zone,
                                            data_format character varying DEFAULT 'neptune'::character varying
);


ALTER TABLE chouette_gui.organisations OWNER TO chouette;

--
-- Name: pt_links_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.pt_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.pt_links_id_seq OWNER TO chouette;

--
-- Name: pt_links; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.pt_links (
                                       id bigint DEFAULT nextval('chouette_gui.pt_links_id_seq'::regclass) NOT NULL,
                                       start_of_link_id bigint,
                                       end_of_link_id bigint,
                                       route_id bigint,
                                       objectid character varying NOT NULL,
                                       object_version integer,
                                       creation_time timestamp without time zone,
                                       creator_id character varying,
                                       name character varying,
                                       comment character varying,
                                       link_distance numeric(19,2)
);


ALTER TABLE chouette_gui.pt_links OWNER TO chouette;

--
-- Name: referential_last_update; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.referential_last_update (
                                                      id bigint NOT NULL,
                                                      last_update_timestamp timestamp without time zone
);


ALTER TABLE chouette_gui.referential_last_update OWNER TO chouette;

--
-- Name: referential_last_update_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.referential_last_update_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.referential_last_update_id_seq OWNER TO chouette;

--
-- Name: referential_last_update_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.referential_last_update_id_seq OWNED BY chouette_gui.referential_last_update.id;


--
-- Name: referentials_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.referentials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.referentials_id_seq OWNER TO chouette;

--
-- Name: referentials; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.referentials (
                                           id bigint DEFAULT nextval('chouette_gui.referentials_id_seq'::regclass) NOT NULL,
                                           name character varying,
                                           slug character varying,
                                           created_at timestamp without time zone,
                                           updated_at timestamp without time zone,
                                           prefix character varying,
                                           projection_type character varying,
                                           time_zone character varying,
                                           bounds character varying,
                                           organisation_id bigint,
                                           geographical_bounds text,
                                           user_id bigint,
                                           user_name character varying,
                                           data_format character varying
);


ALTER TABLE chouette_gui.referentials OWNER TO chouette;

--
-- Name: route_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.route_points (
                                           id bigint NOT NULL,
                                           objectid character varying NOT NULL,
                                           object_version integer,
                                           creation_time timestamp without time zone,
                                           creator_id character varying(255),
                                           scheduled_stop_point_id bigint,
                                           name character varying,
                                           boarder_crossing boolean
);


ALTER TABLE chouette_gui.route_points OWNER TO chouette;

--
-- Name: route_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.route_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.route_points_id_seq OWNER TO chouette;

--
-- Name: route_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.route_points_id_seq OWNED BY chouette_gui.route_points.id;


--
-- Name: route_sections_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.route_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.route_sections_id_seq OWNER TO chouette;

--
-- Name: route_sections; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.route_sections (
                                             id bigint DEFAULT nextval('chouette_gui.route_sections_id_seq'::regclass) NOT NULL,
                                             input_geometry shared_extensions.geometry(LineString,4326),
                                             processed_geometry shared_extensions.geometry(LineString,4326),
                                             objectid character varying NOT NULL,
                                             object_version integer,
                                             creation_time timestamp without time zone,
                                             creator_id character varying,
                                             distance double precision,
                                             no_processing boolean DEFAULT false NOT NULL,
                                             from_scheduled_stop_point_id bigint,
                                             to_scheduled_stop_point_id bigint
);


ALTER TABLE chouette_gui.route_sections OWNER TO chouette;

--
-- Name: routes_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.routes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.routes_id_seq OWNER TO chouette;

--
-- Name: routes; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.routes (
                                     id bigint DEFAULT nextval('chouette_gui.routes_id_seq'::regclass) NOT NULL,
                                     line_id bigint,
                                     objectid character varying NOT NULL,
                                     object_version integer,
                                     creation_time timestamp without time zone,
                                     creator_id character varying,
                                     name character varying,
                                     comment character varying,
                                     opposite_route_id bigint,
                                     published_name character varying,
                                     number character varying,
                                     direction character varying,
                                     wayback character varying
);


ALTER TABLE chouette_gui.routes OWNER TO chouette;

--
-- Name: routes_route_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.routes_route_points (
                                                  id bigint NOT NULL,
                                                  route_id bigint NOT NULL,
                                                  route_point_id bigint NOT NULL,
                                                  "position" integer NOT NULL
);


ALTER TABLE chouette_gui.routes_route_points OWNER TO chouette;

--
-- Name: routes_route_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.routes_route_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.routes_route_points_id_seq OWNER TO chouette;

--
-- Name: routes_route_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.routes_route_points_id_seq OWNED BY chouette_gui.routes_route_points.id;


--
-- Name: routing_constraints_lines; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.routing_constraints_lines (
                                                        line_id bigint,
                                                        stop_area_objectid_key character varying
);


ALTER TABLE chouette_gui.routing_constraints_lines OWNER TO chouette;

--
-- Name: rule_parameter_sets_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.rule_parameter_sets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.rule_parameter_sets_id_seq OWNER TO chouette;

--
-- Name: rule_parameter_sets; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.rule_parameter_sets (
                                                  id bigint DEFAULT nextval('chouette_gui.rule_parameter_sets_id_seq'::regclass) NOT NULL,
                                                  parameters text,
                                                  name character varying,
                                                  created_at timestamp without time zone,
                                                  updated_at timestamp without time zone,
                                                  organisation_id bigint
);


ALTER TABLE chouette_gui.rule_parameter_sets OWNER TO chouette;

--
-- Name: scheduled_stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.scheduled_stop_points (
                                                    id bigint NOT NULL,
                                                    objectid character varying NOT NULL,
                                                    stop_area_objectid_key character varying,
                                                    object_version integer,
                                                    creation_time timestamp without time zone,
                                                    creator_id character varying(255),
                                                    name character varying,
                                                    timing_point_status character varying
);


ALTER TABLE chouette_gui.scheduled_stop_points OWNER TO chouette;

--
-- Name: scheduled_stop_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.scheduled_stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.scheduled_stop_points_id_seq OWNER TO chouette;

--
-- Name: scheduled_stop_points_id_seq; Type: SEQUENCE OWNED BY; Schema: chouette_gui; Owner: chouette
--

ALTER SEQUENCE chouette_gui.scheduled_stop_points_id_seq OWNED BY chouette_gui.scheduled_stop_points.id;


--
-- Name: schema_migrations; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.schema_migrations (
    version character varying NOT NULL
);


ALTER TABLE chouette_gui.schema_migrations OWNER TO chouette;

--
-- Name: stop_areas_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.stop_areas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.stop_areas_id_seq OWNER TO chouette;

--
-- Name: stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.stop_areas (
                                         id bigint DEFAULT nextval('chouette_gui.stop_areas_id_seq'::regclass) NOT NULL,
                                         parent_id bigint,
                                         objectid character varying NOT NULL,
                                         object_version integer,
                                         creation_time timestamp without time zone,
                                         creator_id character varying,
                                         name character varying,
                                         comment character varying,
                                         area_type character varying,
                                         registration_number character varying,
                                         nearest_topic_name character varying,
                                         fare_code integer,
                                         longitude numeric(19,16),
                                         latitude numeric(19,16),
                                         long_lat_type character varying,
                                         country_code character varying,
                                         street_name character varying,
                                         mobility_restricted_suitability boolean,
                                         stairs_availability boolean,
                                         lift_availability boolean,
                                         int_user_needs integer,
                                         zip_code character varying,
                                         city_name character varying,
                                         url character varying,
                                         time_zone character varying,
                                         compass_bearing integer,
                                         stop_place_type character varying,
                                         transport_mode character varying,
                                         transport_sub_mode character varying
);


ALTER TABLE chouette_gui.stop_areas OWNER TO chouette;

--
-- Name: stop_areas_stop_areas; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.stop_areas_stop_areas (
                                                    child_id bigint,
                                                    parent_id bigint
);


ALTER TABLE chouette_gui.stop_areas_stop_areas OWNER TO chouette;

--
-- Name: stop_points_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.stop_points_id_seq OWNER TO chouette;

--
-- Name: stop_points; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.stop_points (
                                          id bigint DEFAULT nextval('chouette_gui.stop_points_id_seq'::regclass) NOT NULL,
                                          route_id bigint,
                                          objectid character varying NOT NULL,
                                          object_version integer,
                                          creation_time timestamp without time zone,
                                          creator_id character varying,
                                          "position" integer,
                                          for_boarding character varying,
                                          for_alighting character varying,
                                          destination_display_id bigint,
                                          scheduled_stop_point_id bigint NOT NULL,
                                          booking_arrangement_id bigint
);


ALTER TABLE chouette_gui.stop_points OWNER TO chouette;

--
-- Name: taggings_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.taggings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.taggings_id_seq OWNER TO chouette;

--
-- Name: taggings; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.taggings (
                                       id bigint DEFAULT nextval('chouette_gui.taggings_id_seq'::regclass) NOT NULL,
                                       tag_id bigint,
                                       taggable_id bigint,
                                       taggable_type character varying,
                                       tagger_id bigint,
                                       tagger_type character varying,
                                       context character varying(128),
                                       created_at timestamp without time zone
);


ALTER TABLE chouette_gui.taggings OWNER TO chouette;

--
-- Name: tags_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.tags_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.tags_id_seq OWNER TO chouette;

--
-- Name: tags; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.tags (
                                   id bigint DEFAULT nextval('chouette_gui.tags_id_seq'::regclass) NOT NULL,
                                   name character varying,
                                   taggings_count integer DEFAULT 0
);


ALTER TABLE chouette_gui.tags OWNER TO chouette;

--
-- Name: time_table_dates_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.time_table_dates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_table_dates_id_seq OWNER TO chouette;

--
-- Name: time_table_dates; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_table_dates (
                                               time_table_id bigint NOT NULL,
                                               date date,
                                               "position" integer NOT NULL,
                                               id bigint DEFAULT nextval('chouette_gui.time_table_dates_id_seq'::regclass) NOT NULL,
                                               in_out boolean
);


ALTER TABLE chouette_gui.time_table_dates OWNER TO chouette;

--
-- Name: time_table_periods_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.time_table_periods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_table_periods_id_seq OWNER TO chouette;

--
-- Name: time_table_periods; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_table_periods (
                                                 time_table_id bigint NOT NULL,
                                                 period_start date,
                                                 period_end date,
                                                 "position" integer NOT NULL,
                                                 id bigint DEFAULT nextval('chouette_gui.time_table_periods_id_seq'::regclass) NOT NULL
);


ALTER TABLE chouette_gui.time_table_periods OWNER TO chouette;

--
-- Name: time_tables_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.time_tables_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.time_tables_id_seq OWNER TO chouette;

--
-- Name: time_tables; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_tables (
                                          id bigint DEFAULT nextval('chouette_gui.time_tables_id_seq'::regclass) NOT NULL,
                                          objectid character varying NOT NULL,
                                          object_version integer DEFAULT 1,
                                          creation_time timestamp without time zone,
                                          creator_id character varying,
                                          version character varying,
                                          comment character varying,
                                          int_day_types integer DEFAULT 0,
                                          start_date date,
                                          end_date date
);


ALTER TABLE chouette_gui.time_tables OWNER TO chouette;

--
-- Name: time_tables_blocks; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_tables_blocks (
                                                 block_id integer NOT NULL,
                                                 time_table_id integer NOT NULL
);


ALTER TABLE chouette_gui.time_tables_blocks OWNER TO chouette;

--
-- Name: time_tables_dead_runs; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_tables_dead_runs (
                                                    dead_run_id integer NOT NULL,
                                                    time_table_id integer NOT NULL
);


ALTER TABLE chouette_gui.time_tables_dead_runs OWNER TO chouette;

--
-- Name: time_tables_vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.time_tables_vehicle_journeys (
                                                           time_table_id bigint,
                                                           vehicle_journey_id bigint
);


ALTER TABLE chouette_gui.time_tables_vehicle_journeys OWNER TO chouette;

--
-- Name: timebands_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.timebands_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.timebands_id_seq OWNER TO chouette;

--
-- Name: timebands; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.timebands (
                                        id bigint DEFAULT nextval('chouette_gui.timebands_id_seq'::regclass) NOT NULL,
                                        objectid character varying NOT NULL,
                                        object_version integer,
                                        creation_time timestamp without time zone,
                                        creator_id character varying,
                                        name character varying,
                                        start_time time without time zone NOT NULL,
                                        end_time time without time zone NOT NULL,
                                        created_at timestamp without time zone,
                                        updated_at timestamp without time zone
);


ALTER TABLE chouette_gui.timebands OWNER TO chouette;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.users_id_seq OWNER TO chouette;

--
-- Name: users; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.users (
                                    id bigint DEFAULT nextval('chouette_gui.users_id_seq'::regclass) NOT NULL,
                                    email character varying DEFAULT ''::character varying NOT NULL,
                                    encrypted_password character varying DEFAULT ''::character varying,
                                    reset_password_token character varying,
                                    reset_password_sent_at timestamp without time zone,
                                    remember_created_at timestamp without time zone,
                                    sign_in_count integer DEFAULT 0,
                                    current_sign_in_at timestamp without time zone,
                                    last_sign_in_at timestamp without time zone,
                                    current_sign_in_ip character varying,
                                    last_sign_in_ip character varying,
                                    created_at timestamp without time zone,
                                    updated_at timestamp without time zone,
                                    organisation_id bigint,
                                    name character varying,
                                    confirmation_token character varying,
                                    confirmed_at timestamp without time zone,
                                    confirmation_sent_at timestamp without time zone,
                                    unconfirmed_email character varying,
                                    failed_attempts integer DEFAULT 0,
                                    unlock_token character varying,
                                    locked_at timestamp without time zone,
                                    authentication_token character varying,
                                    invitation_token character varying,
                                    invitation_sent_at timestamp without time zone,
                                    invitation_accepted_at timestamp without time zone,
                                    invitation_limit integer,
                                    invited_by_id bigint,
                                    invited_by_type character varying,
                                    invitation_created_at timestamp without time zone,
                                    role integer DEFAULT 1 NOT NULL,
                                    provider character varying,
                                    uid character varying
);


ALTER TABLE chouette_gui.users OWNER TO chouette;

--
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.vehicle_journey_at_stops_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.vehicle_journey_at_stops_id_seq OWNER TO chouette;

--
-- Name: vehicle_journey_at_stops; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.vehicle_journey_at_stops (
                                                       id bigint DEFAULT nextval('chouette_gui.vehicle_journey_at_stops_id_seq'::regclass) NOT NULL,
                                                       vehicle_journey_id bigint,
                                                       stop_point_id bigint,
                                                       connecting_service_id character varying,
                                                       boarding_alighting_possibility character varying,
                                                       arrival_time time without time zone,
                                                       departure_time time without time zone,
                                                       for_boarding character varying,
                                                       for_alighting character varying,
                                                       arrival_day_offset integer DEFAULT 0 NOT NULL,
                                                       departure_day_offset integer DEFAULT 0 NOT NULL,
                                                       objectid character varying,
                                                       object_version integer,
                                                       creator_id character varying,
                                                       creation_time timestamp without time zone
);


ALTER TABLE chouette_gui.vehicle_journey_at_stops OWNER TO chouette;

--
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE; Schema: chouette_gui; Owner: chouette
--

CREATE SEQUENCE chouette_gui.vehicle_journeys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chouette_gui.vehicle_journeys_id_seq OWNER TO chouette;

--
-- Name: vehicle_journeys; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.vehicle_journeys (
                                               id bigint DEFAULT nextval('chouette_gui.vehicle_journeys_id_seq'::regclass) NOT NULL,
                                               route_id bigint,
                                               journey_pattern_id bigint,
                                               company_id bigint,
                                               objectid character varying NOT NULL,
                                               object_version integer,
                                               creation_time timestamp without time zone,
                                               creator_id character varying,
                                               comment character varying,
                                               status_value character varying,
                                               transport_mode character varying,
                                               published_journey_name character varying,
                                               published_journey_identifier character varying,
                                               facility character varying,
                                               vehicle_type_identifier character varying,
                                               number bigint,
                                               mobility_restricted_suitability boolean,
                                               flexible_service boolean,
                                               journey_category integer DEFAULT 0 NOT NULL,
                                               transport_submode_name character varying,
                                               private_code character varying,
                                               service_alteration character varying,
                                               flexible_service_properties_id bigint
);


ALTER TABLE chouette_gui.vehicle_journeys OWNER TO chouette;

--
-- Name: vehicle_journeys_key_values; Type: TABLE; Schema: chouette_gui; Owner: chouette
--

CREATE TABLE chouette_gui.vehicle_journeys_key_values (
                                                          vehicle_journey_id bigint,
                                                          type_of_key character varying,
                                                          key character varying,
                                                          value character varying
);


ALTER TABLE chouette_gui.vehicle_journeys_key_values OWNER TO chouette;

--
-- Name: access_links; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.access_links (
                                     id bigint NOT NULL,
                                     access_point_id bigint,
                                     stop_area_id bigint,
                                     objectid character varying NOT NULL,
                                     object_version integer,
                                     creation_time timestamp without time zone,
                                     creator_id character varying,
                                     name character varying,
                                     comment character varying,
                                     link_distance numeric(19,2),
                                     lift_availability boolean,
                                     mobility_restricted_suitability boolean,
                                     stairs_availability boolean,
                                     default_duration time without time zone,
                                     frequent_traveller_duration time without time zone,
                                     occasional_traveller_duration time without time zone,
                                     mobility_restricted_traveller_duration time without time zone,
                                     link_type character varying,
                                     int_user_needs integer,
                                     link_orientation character varying
);


ALTER TABLE public.access_links OWNER TO chouette;

--
-- Name: access_links_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.access_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_links_id_seq OWNER TO chouette;

--
-- Name: access_links_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.access_links_id_seq OWNED BY public.access_links.id;


--
-- Name: access_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.access_points (
                                      id bigint NOT NULL,
                                      objectid character varying,
                                      object_version integer,
                                      creation_time timestamp without time zone,
                                      creator_id character varying,
                                      name character varying,
                                      comment character varying,
                                      longitude numeric(19,16),
                                      latitude numeric(19,16),
                                      long_lat_type character varying,
                                      country_code character varying,
                                      street_name character varying,
                                      contained_in character varying,
                                      openning_time time without time zone,
                                      closing_time time without time zone,
                                      access_type character varying,
                                      lift_availability boolean,
                                      mobility_restricted_suitability boolean,
                                      stairs_availability boolean,
                                      stop_area_id bigint,
                                      zip_code character varying,
                                      city_name character varying
);


ALTER TABLE public.access_points OWNER TO chouette;

--
-- Name: access_points_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.access_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_points_id_seq OWNER TO chouette;

--
-- Name: access_points_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.access_points_id_seq OWNED BY public.access_points.id;


--
-- Name: api_keys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.api_keys (
                                 id bigint NOT NULL,
                                 referential_id bigint,
                                 token character varying,
                                 name character varying,
                                 created_at timestamp without time zone,
                                 updated_at timestamp without time zone
);


ALTER TABLE public.api_keys OWNER TO chouette;

--
-- Name: api_keys_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.api_keys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.api_keys_id_seq OWNER TO chouette;

--
-- Name: api_keys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.api_keys_id_seq OWNED BY public.api_keys.id;


--
-- Name: blocks; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.blocks (
                               id bigint NOT NULL,
                               objectid character varying NOT NULL,
                               object_version integer,
                               creation_time timestamp without time zone,
                               creator_id character varying(255),
                               private_code character varying,
                               name character varying,
                               description character varying,
                               start_time time without time zone,
                               end_time time without time zone,
                               end_time_day_offset integer,
                               start_point_id integer,
                               end_point_id integer
);


ALTER TABLE public.blocks OWNER TO chouette;

--
-- Name: blocks_dead_runs; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.blocks_dead_runs (
                                         block_id integer NOT NULL,
                                         dead_run_id integer NOT NULL,
                                         "position" integer
);


ALTER TABLE public.blocks_dead_runs OWNER TO chouette;

--
-- Name: blocks_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.blocks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.blocks_id_seq OWNER TO chouette;

--
-- Name: blocks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.blocks_id_seq OWNED BY public.blocks.id;


--
-- Name: blocks_vehicle_journeys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.blocks_vehicle_journeys (
                                                block_id integer NOT NULL,
                                                vehicle_journey_id integer NOT NULL,
                                                "position" integer
);


ALTER TABLE public.blocks_vehicle_journeys OWNER TO chouette;

--
-- Name: booking_arrangements; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.booking_arrangements (
                                             id bigint NOT NULL,
                                             booking_note character varying,
                                             booking_access character varying,
                                             book_when character varying,
                                             latest_booking_time time without time zone,
                                             minimum_booking_period time without time zone,
                                             booking_contact_id bigint
);


ALTER TABLE public.booking_arrangements OWNER TO chouette;

--
-- Name: booking_arrangements_booking_methods; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.booking_arrangements_booking_methods (
                                                             booking_arrangement_id bigint,
                                                             booking_method character varying
);


ALTER TABLE public.booking_arrangements_booking_methods OWNER TO chouette;

--
-- Name: booking_arrangements_buy_when; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.booking_arrangements_buy_when (
                                                      booking_arrangement_id bigint,
                                                      buy_when character varying
);


ALTER TABLE public.booking_arrangements_buy_when OWNER TO chouette;

--
-- Name: booking_arrangements_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.booking_arrangements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.booking_arrangements_id_seq OWNER TO chouette;

--
-- Name: booking_arrangements_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.booking_arrangements_id_seq OWNED BY public.booking_arrangements.id;


--
-- Name: brandings; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.brandings (
                                  id bigint NOT NULL,
                                  objectid character varying NOT NULL,
                                  object_version integer,
                                  creation_time timestamp without time zone,
                                  creator_id character varying(255),
                                  name character varying,
                                  description character varying,
                                  url character varying,
                                  image character varying
);


ALTER TABLE public.brandings OWNER TO chouette;

--
-- Name: brandings_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.brandings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.brandings_id_seq OWNER TO chouette;

--
-- Name: brandings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.brandings_id_seq OWNED BY public.brandings.id;


--
-- Name: codespaces; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.codespaces (
                                   id bigint NOT NULL,
                                   xmlns character varying NOT NULL,
                                   xmlns_url character varying NOT NULL,
                                   created_at timestamp without time zone,
                                   updated_at timestamp without time zone
);


ALTER TABLE public.codespaces OWNER TO chouette;

--
-- Name: codespaces_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.codespaces_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.codespaces_id_seq OWNER TO chouette;

--
-- Name: codespaces_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.codespaces_id_seq OWNED BY public.codespaces.id;


--
-- Name: companies; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.companies (
                                  id bigint NOT NULL,
                                  objectid character varying NOT NULL,
                                  object_version integer,
                                  creation_time timestamp without time zone,
                                  creator_id character varying,
                                  name character varying,
                                  short_name character varying,
                                  organizational_unit character varying,
                                  operating_department_name character varying,
                                  code character varying,
                                  phone character varying,
                                  fax character varying,
                                  email character varying,
                                  registration_number character varying,
                                  url character varying,
                                  time_zone character varying,
                                  organisation_type character varying,
                                  legal_name character varying,
                                  public_email character varying,
                                  public_url character varying,
                                  public_phone character varying,
                                  branding_id bigint
);


ALTER TABLE public.companies OWNER TO chouette;

--
-- Name: companies_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.companies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.companies_id_seq OWNER TO chouette;

--
-- Name: companies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.companies_id_seq OWNED BY public.companies.id;


--
-- Name: connection_links; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.connection_links (
                                         id bigint NOT NULL,
                                         departure_id bigint,
                                         arrival_id bigint,
                                         objectid character varying NOT NULL,
                                         object_version integer,
                                         creation_time timestamp without time zone,
                                         creator_id character varying,
                                         name character varying,
                                         comment character varying,
                                         link_distance numeric(19,2),
                                         link_type character varying,
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
-- Name: connection_links_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.connection_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.connection_links_id_seq OWNER TO chouette;

--
-- Name: connection_links_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.connection_links_id_seq OWNED BY public.connection_links.id;


--
-- Name: contact_structures; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.contact_structures (
                                           id bigint NOT NULL,
                                           contact_person character varying,
                                           email character varying,
                                           phone character varying,
                                           fax character varying,
                                           url character varying,
                                           further_details character varying
);


ALTER TABLE public.contact_structures OWNER TO chouette;

--
-- Name: contact_structures_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.contact_structures_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.contact_structures_id_seq OWNER TO chouette;

--
-- Name: contact_structures_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.contact_structures_id_seq OWNED BY public.contact_structures.id;


--
-- Name: dated_service_journey_refs; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.dated_service_journey_refs (
                                                   original_dsj_id integer,
                                                   derived_dsj_id integer
);


ALTER TABLE public.dated_service_journey_refs OWNER TO chouette;

--
-- Name: dated_service_journeys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.dated_service_journeys (
                                               id bigint NOT NULL,
                                               objectid character varying NOT NULL,
                                               object_version integer,
                                               creation_time timestamp without time zone,
                                               creator_id character varying(255),
                                               operating_day date NOT NULL,
                                               vehicle_journey_id integer NOT NULL,
                                               service_alteration character varying
);


ALTER TABLE public.dated_service_journeys OWNER TO chouette;

--
-- Name: dated_service_journeys_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.dated_service_journeys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dated_service_journeys_id_seq OWNER TO chouette;

--
-- Name: dated_service_journeys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.dated_service_journeys_id_seq OWNED BY public.dated_service_journeys.id;


--
-- Name: dead_run_at_stops; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.dead_run_at_stops (
                                          id bigint NOT NULL,
                                          objectid character varying NOT NULL,
                                          object_version integer,
                                          creation_time timestamp without time zone,
                                          creator_id character varying,
                                          dead_run_id bigint,
                                          stop_point_id bigint,
                                          arrival_time time without time zone,
                                          departure_time time without time zone,
                                          arrival_day_offset integer,
                                          departure_day_offset integer
);


ALTER TABLE public.dead_run_at_stops OWNER TO chouette;

--
-- Name: dead_run_at_stops_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.dead_run_at_stops_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dead_run_at_stops_id_seq OWNER TO chouette;

--
-- Name: dead_run_at_stops_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.dead_run_at_stops_id_seq OWNED BY public.dead_run_at_stops.id;


--
-- Name: dead_runs; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.dead_runs (
                                  id bigint NOT NULL,
                                  journey_pattern_id bigint,
                                  objectid character varying NOT NULL,
                                  object_version integer,
                                  creation_time timestamp without time zone,
                                  creator_id character varying
);


ALTER TABLE public.dead_runs OWNER TO chouette;

--
-- Name: dead_runs_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.dead_runs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dead_runs_id_seq OWNER TO chouette;

--
-- Name: dead_runs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.dead_runs_id_seq OWNED BY public.dead_runs.id;


--
-- Name: delayed_jobs; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.delayed_jobs (
                                     id bigint NOT NULL,
                                     priority integer DEFAULT 0,
                                     attempts integer DEFAULT 0,
                                     handler text,
                                     last_error text,
                                     run_at timestamp without time zone,
                                     locked_at timestamp without time zone,
                                     failed_at timestamp without time zone,
                                     locked_by character varying,
                                     queue character varying,
                                     created_at timestamp without time zone,
                                     updated_at timestamp without time zone
);


ALTER TABLE public.delayed_jobs OWNER TO chouette;

--
-- Name: delayed_jobs_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.delayed_jobs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.delayed_jobs_id_seq OWNER TO chouette;

--
-- Name: delayed_jobs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.delayed_jobs_id_seq OWNED BY public.delayed_jobs.id;


--
-- Name: destination_display_via; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.destination_display_via (
                                                destination_display_id bigint NOT NULL,
                                                via_id bigint NOT NULL,
                                                "position" bigint,
                                                created_at timestamp without time zone,
                                                updated_at timestamp without time zone
);


ALTER TABLE public.destination_display_via OWNER TO chouette;

--
-- Name: destination_displays; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.destination_displays (
                                             id bigint NOT NULL,
                                             name character varying,
                                             side_text character varying,
                                             front_text character varying NOT NULL,
                                             objectid character varying NOT NULL,
                                             object_version bigint,
                                             creation_time timestamp without time zone,
                                             creator_id character varying
);


ALTER TABLE public.destination_displays OWNER TO chouette;

--
-- Name: destination_displays_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.destination_displays_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.destination_displays_id_seq OWNER TO chouette;

--
-- Name: destination_displays_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.destination_displays_id_seq OWNED BY public.destination_displays.id;


--
-- Name: exports; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.exports (
                                id bigint NOT NULL,
                                referential_id bigint,
                                status character varying,
                                type character varying,
                                options character varying,
                                created_at timestamp without time zone,
                                updated_at timestamp without time zone,
                                references_type character varying,
                                reference_ids character varying
);


ALTER TABLE public.exports OWNER TO chouette;

--
-- Name: exports_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.exports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.exports_id_seq OWNER TO chouette;

--
-- Name: exports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.exports_id_seq OWNED BY public.exports.id;


--
-- Name: facilities; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.facilities (
                                   id bigint NOT NULL,
                                   stop_area_id bigint,
                                   line_id bigint,
                                   connection_link_id bigint,
                                   stop_point_id bigint,
                                   objectid character varying NOT NULL,
                                   object_version integer,
                                   creation_time timestamp without time zone,
                                   creator_id character varying,
                                   name character varying,
                                   comment character varying,
                                   description character varying,
                                   free_access boolean,
                                   longitude numeric(19,16),
                                   latitude numeric(19,16),
                                   long_lat_type character varying,
                                   x numeric(19,2),
                                   y numeric(19,2),
                                   projection_type character varying,
                                   country_code character varying,
                                   street_name character varying,
                                   contained_in character varying
);


ALTER TABLE public.facilities OWNER TO chouette;

--
-- Name: facilities_features; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.facilities_features (
                                            facility_id bigint,
                                            choice_code integer
);


ALTER TABLE public.facilities_features OWNER TO chouette;

--
-- Name: facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.facilities_id_seq OWNER TO chouette;

--
-- Name: facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.facilities_id_seq OWNED BY public.facilities.id;


--
-- Name: flexible_service_properties; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.flexible_service_properties (
                                                    id bigint NOT NULL,
                                                    objectid character varying NOT NULL,
                                                    object_version integer,
                                                    creation_time timestamp without time zone,
                                                    creator_id character varying(255),
                                                    flexible_service_type character varying(255),
                                                    cancellation_possible boolean,
                                                    change_of_time_possible boolean,
                                                    booking_arrangement_id bigint
);


ALTER TABLE public.flexible_service_properties OWNER TO chouette;

--
-- Name: flexible_service_properties_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.flexible_service_properties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.flexible_service_properties_id_seq OWNER TO chouette;

--
-- Name: flexible_service_properties_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.flexible_service_properties_id_seq OWNED BY public.flexible_service_properties.id;


--
-- Name: footnote_alternative_texts; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnote_alternative_texts (
                                                   id bigint NOT NULL,
                                                   objectid character varying NOT NULL,
                                                   object_version integer,
                                                   creation_time timestamp without time zone,
                                                   creator_id character varying(255),
                                                   footnote_id integer NOT NULL,
                                                   text character varying,
                                                   language character varying
);


ALTER TABLE public.footnote_alternative_texts OWNER TO chouette;

--
-- Name: footnote_alternative_texts_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.footnote_alternative_texts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.footnote_alternative_texts_id_seq OWNER TO chouette;

--
-- Name: footnote_alternative_texts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.footnote_alternative_texts_id_seq OWNED BY public.footnote_alternative_texts.id;


--
-- Name: footnotes; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes (
                                  id bigint NOT NULL,
                                  code character varying,
                                  label character varying,
                                  creation_time timestamp without time zone,
                                  objectid character varying NOT NULL,
                                  object_version integer,
                                  creator_id character varying
);


ALTER TABLE public.footnotes OWNER TO chouette;

--
-- Name: footnotes_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.footnotes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.footnotes_id_seq OWNER TO chouette;

--
-- Name: footnotes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.footnotes_id_seq OWNED BY public.footnotes.id;


--
-- Name: footnotes_journey_patterns; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes_journey_patterns (
                                                   journey_pattern_id bigint,
                                                   footnote_id bigint
);


ALTER TABLE public.footnotes_journey_patterns OWNER TO chouette;

--
-- Name: footnotes_lines; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes_lines (
                                        line_id bigint,
                                        footnote_id bigint
);


ALTER TABLE public.footnotes_lines OWNER TO chouette;

--
-- Name: footnotes_stop_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes_stop_points (
                                              stop_point_id bigint,
                                              footnote_id bigint
);


ALTER TABLE public.footnotes_stop_points OWNER TO chouette;

--
-- Name: footnotes_vehicle_journey_at_stops; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes_vehicle_journey_at_stops (
                                                           vehicle_journey_at_stop_id bigint,
                                                           footnote_id bigint
);


ALTER TABLE public.footnotes_vehicle_journey_at_stops OWNER TO chouette;

--
-- Name: footnotes_vehicle_journeys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.footnotes_vehicle_journeys (
                                                   vehicle_journey_id bigint,
                                                   footnote_id bigint
);


ALTER TABLE public.footnotes_vehicle_journeys OWNER TO chouette;

--
-- Name: group_of_lines; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.group_of_lines (
                                       id bigint NOT NULL,
                                       objectid character varying NOT NULL,
                                       object_version integer,
                                       creation_time timestamp without time zone,
                                       creator_id character varying,
                                       name character varying,
                                       comment character varying,
                                       registration_number character varying
);


ALTER TABLE public.group_of_lines OWNER TO chouette;

--
-- Name: group_of_lines_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.group_of_lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_of_lines_id_seq OWNER TO chouette;

--
-- Name: group_of_lines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.group_of_lines_id_seq OWNED BY public.group_of_lines.id;


--
-- Name: group_of_lines_lines; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.group_of_lines_lines (
                                             group_of_line_id bigint,
                                             line_id bigint
);


ALTER TABLE public.group_of_lines_lines OWNER TO chouette;

--
-- Name: interchanges; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.interchanges (
                                     id bigint NOT NULL,
                                     objectid character varying NOT NULL,
                                     object_version integer,
                                     creation_time timestamp without time zone,
                                     creator_id character varying,
                                     name character varying,
                                     priority integer,
                                     planned boolean,
                                     guaranteed boolean,
                                     advertised boolean,
                                     maximum_wait_time time without time zone,
                                     from_point character varying,
                                     to_point character varying,
                                     from_vehicle_journey character varying,
                                     to_vehicle_journey character varying,
                                     stay_seated boolean,
                                     minimum_transfer_time time without time zone,
                                     from_visit_number integer,
                                     to_visit_number integer
);


ALTER TABLE public.interchanges OWNER TO chouette;

--
-- Name: interchanges_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.interchanges_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.interchanges_id_seq OWNER TO chouette;

--
-- Name: interchanges_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.interchanges_id_seq OWNED BY public.interchanges.id;


--
-- Name: journey_frequencies; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.journey_frequencies (
                                            id bigint NOT NULL,
                                            vehicle_journey_id bigint,
                                            scheduled_headway_interval time without time zone NOT NULL,
                                            first_departure_time time without time zone NOT NULL,
                                            last_departure_time time without time zone,
                                            exact_time boolean DEFAULT false,
                                            created_at timestamp without time zone,
                                            updated_at timestamp without time zone,
                                            timeband_id bigint
);


ALTER TABLE public.journey_frequencies OWNER TO chouette;

--
-- Name: journey_frequencies_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.journey_frequencies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.journey_frequencies_id_seq OWNER TO chouette;

--
-- Name: journey_frequencies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.journey_frequencies_id_seq OWNED BY public.journey_frequencies.id;


--
-- Name: journey_pattern_sections; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.journey_pattern_sections (
                                                 id bigint NOT NULL,
                                                 journey_pattern_id bigint NOT NULL,
                                                 route_section_id bigint NOT NULL,
                                                 rank integer NOT NULL,
                                                 created_at timestamp without time zone,
                                                 updated_at timestamp without time zone
);


ALTER TABLE public.journey_pattern_sections OWNER TO chouette;

--
-- Name: journey_pattern_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.journey_pattern_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.journey_pattern_sections_id_seq OWNER TO chouette;

--
-- Name: journey_pattern_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.journey_pattern_sections_id_seq OWNED BY public.journey_pattern_sections.id;


--
-- Name: journey_patterns; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.journey_patterns (
                                         id bigint NOT NULL,
                                         route_id bigint,
                                         objectid character varying NOT NULL,
                                         object_version integer,
                                         creation_time timestamp without time zone,
                                         creator_id character varying,
                                         name character varying,
                                         comment character varying,
                                         registration_number character varying,
                                         published_name character varying,
                                         departure_stop_point_id bigint,
                                         arrival_stop_point_id bigint,
                                         section_status integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.journey_patterns OWNER TO chouette;

--
-- Name: journey_patterns_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.journey_patterns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.journey_patterns_id_seq OWNER TO chouette;

--
-- Name: journey_patterns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.journey_patterns_id_seq OWNED BY public.journey_patterns.id;


--
-- Name: journey_patterns_stop_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.journey_patterns_stop_points (
                                                     journey_pattern_id bigint,
                                                     stop_point_id bigint
);


ALTER TABLE public.journey_patterns_stop_points OWNER TO chouette;

--
-- Name: lines; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.lines (
                              id bigint NOT NULL,
                              network_id bigint,
                              company_id bigint,
                              objectid character varying NOT NULL,
                              object_version integer,
                              creation_time timestamp without time zone,
                              creator_id character varying,
                              name character varying,
                              number character varying,
                              published_name character varying,
                              transport_mode_name character varying,
                              registration_number character varying,
                              comment character varying,
                              mobility_restricted_suitability boolean,
                              int_user_needs integer,
                              flexible_service boolean,
                              url character varying,
                              color character varying(6),
                              text_color character varying(6),
                              stable_id character varying,
                              transport_submode_name character varying,
                              flexible_line_type character varying,
                              booking_arrangement_id bigint
);


ALTER TABLE public.lines OWNER TO chouette;

--
-- Name: lines_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.lines_id_seq OWNER TO chouette;

--
-- Name: lines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.lines_id_seq OWNED BY public.lines.id;


--
-- Name: lines_key_values; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.lines_key_values (
                                         line_id bigint,
                                         type_of_key character varying,
                                         key character varying,
                                         value character varying
);


ALTER TABLE public.lines_key_values OWNER TO chouette;

--
-- Name: networks; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.networks (
                                 id bigint NOT NULL,
                                 objectid character varying NOT NULL,
                                 object_version integer,
                                 creation_time timestamp without time zone,
                                 creator_id character varying,
                                 version_date date,
                                 description character varying,
                                 name character varying,
                                 registration_number character varying,
                                 source_name character varying,
                                 source_type character varying,
                                 source_identifier character varying,
                                 comment character varying,
                                 company_id bigint
);


ALTER TABLE public.networks OWNER TO chouette;

--
-- Name: networks_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.networks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.networks_id_seq OWNER TO chouette;

--
-- Name: networks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.networks_id_seq OWNED BY public.networks.id;


--
-- Name: organisations; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.organisations (
                                      id bigint NOT NULL,
                                      name character varying,
                                      created_at timestamp without time zone,
                                      updated_at timestamp without time zone,
                                      data_format character varying DEFAULT 'neptune'::character varying
);


ALTER TABLE public.organisations OWNER TO chouette;

--
-- Name: organisations_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.organisations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.organisations_id_seq OWNER TO chouette;

--
-- Name: organisations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.organisations_id_seq OWNED BY public.organisations.id;


--
-- Name: pt_links; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.pt_links (
                                 id bigint NOT NULL,
                                 start_of_link_id bigint,
                                 end_of_link_id bigint,
                                 route_id bigint,
                                 objectid character varying NOT NULL,
                                 object_version integer,
                                 creation_time timestamp without time zone,
                                 creator_id character varying,
                                 name character varying,
                                 comment character varying,
                                 link_distance numeric(19,2)
);


ALTER TABLE public.pt_links OWNER TO chouette;

--
-- Name: pt_links_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.pt_links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pt_links_id_seq OWNER TO chouette;

--
-- Name: pt_links_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.pt_links_id_seq OWNED BY public.pt_links.id;


--
-- Name: referential_last_update; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.referential_last_update (
                                                id bigint NOT NULL,
                                                last_update_timestamp timestamp without time zone
);


ALTER TABLE public.referential_last_update OWNER TO chouette;

--
-- Name: referential_last_update_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.referential_last_update_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.referential_last_update_id_seq OWNER TO chouette;

--
-- Name: referential_last_update_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.referential_last_update_id_seq OWNED BY public.referential_last_update.id;


--
-- Name: referentials; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.referentials (
                                     id bigint NOT NULL,
                                     name character varying,
                                     slug character varying,
                                     created_at timestamp without time zone,
                                     updated_at timestamp without time zone,
                                     prefix character varying,
                                     projection_type character varying,
                                     time_zone character varying,
                                     bounds character varying,
                                     organisation_id bigint,
                                     geographical_bounds text,
                                     user_id bigint,
                                     user_name character varying,
                                     data_format character varying
);


ALTER TABLE public.referentials OWNER TO chouette;

--
-- Name: referentials_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.referentials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.referentials_id_seq OWNER TO chouette;

--
-- Name: referentials_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.referentials_id_seq OWNED BY public.referentials.id;


--
-- Name: route_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.route_points (
                                     id bigint NOT NULL,
                                     objectid character varying NOT NULL,
                                     object_version integer,
                                     creation_time timestamp without time zone,
                                     creator_id character varying(255),
                                     scheduled_stop_point_id bigint,
                                     name character varying,
                                     boarder_crossing boolean
);


ALTER TABLE public.route_points OWNER TO chouette;

--
-- Name: route_points_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.route_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.route_points_id_seq OWNER TO chouette;

--
-- Name: route_points_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.route_points_id_seq OWNED BY public.route_points.id;


--
-- Name: route_sections; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.route_sections (
                                       id bigint NOT NULL,
                                       input_geometry shared_extensions.geometry(LineString,4326),
                                       processed_geometry shared_extensions.geometry(LineString,4326),
                                       objectid character varying NOT NULL,
                                       object_version integer,
                                       creation_time timestamp without time zone,
                                       creator_id character varying,
                                       distance double precision,
                                       no_processing boolean DEFAULT false NOT NULL,
                                       from_scheduled_stop_point_id bigint,
                                       to_scheduled_stop_point_id bigint
);


ALTER TABLE public.route_sections OWNER TO chouette;

--
-- Name: route_sections_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.route_sections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.route_sections_id_seq OWNER TO chouette;

--
-- Name: route_sections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.route_sections_id_seq OWNED BY public.route_sections.id;


--
-- Name: routes; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.routes (
                               id bigint NOT NULL,
                               line_id bigint,
                               objectid character varying NOT NULL,
                               object_version integer,
                               creation_time timestamp without time zone,
                               creator_id character varying,
                               name character varying,
                               comment character varying,
                               opposite_route_id bigint,
                               published_name character varying,
                               number character varying,
                               direction character varying,
                               wayback character varying
);


ALTER TABLE public.routes OWNER TO chouette;

--
-- Name: routes_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.routes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.routes_id_seq OWNER TO chouette;

--
-- Name: routes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.routes_id_seq OWNED BY public.routes.id;


--
-- Name: routes_route_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.routes_route_points (
                                            id bigint NOT NULL,
                                            route_id bigint NOT NULL,
                                            route_point_id bigint NOT NULL,
                                            "position" integer NOT NULL
);


ALTER TABLE public.routes_route_points OWNER TO chouette;

--
-- Name: routes_route_points_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.routes_route_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.routes_route_points_id_seq OWNER TO chouette;

--
-- Name: routes_route_points_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.routes_route_points_id_seq OWNED BY public.routes_route_points.id;


--
-- Name: routing_constraints_lines; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.routing_constraints_lines (
                                                  line_id bigint,
                                                  stop_area_objectid_key character varying
);


ALTER TABLE public.routing_constraints_lines OWNER TO chouette;

--
-- Name: rule_parameter_sets; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.rule_parameter_sets (
                                            id bigint NOT NULL,
                                            parameters text,
                                            name character varying,
                                            created_at timestamp without time zone,
                                            updated_at timestamp without time zone,
                                            organisation_id bigint
);


ALTER TABLE public.rule_parameter_sets OWNER TO chouette;

--
-- Name: rule_parameter_sets_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.rule_parameter_sets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_parameter_sets_id_seq OWNER TO chouette;

--
-- Name: rule_parameter_sets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.rule_parameter_sets_id_seq OWNED BY public.rule_parameter_sets.id;


--
-- Name: scheduled_stop_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.scheduled_stop_points (
                                              id bigint NOT NULL,
                                              objectid character varying NOT NULL,
                                              stop_area_objectid_key character varying,
                                              object_version integer,
                                              creation_time timestamp without time zone,
                                              creator_id character varying(255),
                                              name character varying,
                                              timing_point_status character varying
);


ALTER TABLE public.scheduled_stop_points OWNER TO chouette;

--
-- Name: scheduled_stop_points_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.scheduled_stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scheduled_stop_points_id_seq OWNER TO chouette;

--
-- Name: scheduled_stop_points_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.scheduled_stop_points_id_seq OWNED BY public.scheduled_stop_points.id;


--
-- Name: schema_migrations; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.schema_migrations (
    version character varying NOT NULL
);


ALTER TABLE public.schema_migrations OWNER TO chouette;

--
-- Name: stop_areas; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.stop_areas (
                                   id bigint NOT NULL,
                                   parent_id bigint,
                                   objectid character varying NOT NULL,
                                   object_version integer,
                                   creation_time timestamp without time zone,
                                   creator_id character varying,
                                   name character varying,
                                   comment character varying,
                                   area_type character varying,
                                   registration_number character varying,
                                   nearest_topic_name character varying,
                                   fare_code integer,
                                   longitude numeric(19,16),
                                   latitude numeric(19,16),
                                   long_lat_type character varying,
                                   country_code character varying,
                                   street_name character varying,
                                   mobility_restricted_suitability boolean,
                                   stairs_availability boolean,
                                   lift_availability boolean,
                                   int_user_needs integer,
                                   zip_code character varying,
                                   city_name character varying,
                                   url character varying,
                                   time_zone character varying,
                                   compass_bearing integer,
                                   stop_place_type character varying,
                                   transport_mode character varying,
                                   transport_sub_mode character varying
);


ALTER TABLE public.stop_areas OWNER TO chouette;

--
-- Name: stop_areas_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.stop_areas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stop_areas_id_seq OWNER TO chouette;

--
-- Name: stop_areas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.stop_areas_id_seq OWNED BY public.stop_areas.id;


--
-- Name: stop_areas_stop_areas; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.stop_areas_stop_areas (
                                              child_id bigint,
                                              parent_id bigint
);


ALTER TABLE public.stop_areas_stop_areas OWNER TO chouette;

--
-- Name: stop_points; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.stop_points (
                                    id bigint NOT NULL,
                                    route_id bigint,
                                    objectid character varying NOT NULL,
                                    object_version integer,
                                    creation_time timestamp without time zone,
                                    creator_id character varying,
                                    "position" integer,
                                    for_boarding character varying,
                                    for_alighting character varying,
                                    destination_display_id bigint,
                                    scheduled_stop_point_id bigint NOT NULL,
                                    booking_arrangement_id bigint
);


ALTER TABLE public.stop_points OWNER TO chouette;

--
-- Name: stop_points_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.stop_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stop_points_id_seq OWNER TO chouette;

--
-- Name: stop_points_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.stop_points_id_seq OWNED BY public.stop_points.id;


--
-- Name: taggings; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.taggings (
                                 id bigint NOT NULL,
                                 tag_id bigint,
                                 taggable_id bigint,
                                 taggable_type character varying,
                                 tagger_id bigint,
                                 tagger_type character varying,
                                 context character varying(128),
                                 created_at timestamp without time zone
);


ALTER TABLE public.taggings OWNER TO chouette;

--
-- Name: taggings_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.taggings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.taggings_id_seq OWNER TO chouette;

--
-- Name: taggings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.taggings_id_seq OWNED BY public.taggings.id;


--
-- Name: tags; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.tags (
                             id bigint NOT NULL,
                             name character varying,
                             taggings_count integer DEFAULT 0
);


ALTER TABLE public.tags OWNER TO chouette;

--
-- Name: tags_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.tags_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tags_id_seq OWNER TO chouette;

--
-- Name: tags_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.tags_id_seq OWNED BY public.tags.id;


--
-- Name: time_table_dates; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_table_dates (
                                         time_table_id bigint NOT NULL,
                                         date date,
                                         "position" integer NOT NULL,
                                         id bigint NOT NULL,
                                         in_out boolean
);


ALTER TABLE public.time_table_dates OWNER TO chouette;

--
-- Name: time_table_dates_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.time_table_dates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.time_table_dates_id_seq OWNER TO chouette;

--
-- Name: time_table_dates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.time_table_dates_id_seq OWNED BY public.time_table_dates.id;


--
-- Name: time_table_periods; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_table_periods (
                                           time_table_id bigint NOT NULL,
                                           period_start date,
                                           period_end date,
                                           "position" integer NOT NULL,
                                           id bigint NOT NULL
);


ALTER TABLE public.time_table_periods OWNER TO chouette;

--
-- Name: time_table_periods_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.time_table_periods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.time_table_periods_id_seq OWNER TO chouette;

--
-- Name: time_table_periods_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.time_table_periods_id_seq OWNED BY public.time_table_periods.id;


--
-- Name: time_tables; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_tables (
                                    id bigint NOT NULL,
                                    objectid character varying NOT NULL,
                                    object_version integer DEFAULT 1,
                                    creation_time timestamp without time zone,
                                    creator_id character varying,
                                    version character varying,
                                    comment character varying,
                                    int_day_types integer DEFAULT 0,
                                    start_date date,
                                    end_date date
);


ALTER TABLE public.time_tables OWNER TO chouette;

--
-- Name: time_tables_blocks; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_tables_blocks (
                                           block_id integer NOT NULL,
                                           time_table_id integer NOT NULL
);


ALTER TABLE public.time_tables_blocks OWNER TO chouette;

--
-- Name: time_tables_dead_runs; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_tables_dead_runs (
                                              dead_run_id integer NOT NULL,
                                              time_table_id integer NOT NULL
);


ALTER TABLE public.time_tables_dead_runs OWNER TO chouette;

--
-- Name: time_tables_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.time_tables_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.time_tables_id_seq OWNER TO chouette;

--
-- Name: time_tables_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.time_tables_id_seq OWNED BY public.time_tables.id;


--
-- Name: time_tables_vehicle_journeys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.time_tables_vehicle_journeys (
                                                     time_table_id bigint,
                                                     vehicle_journey_id bigint
);


ALTER TABLE public.time_tables_vehicle_journeys OWNER TO chouette;

--
-- Name: timebands; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.timebands (
                                  id bigint NOT NULL,
                                  objectid character varying NOT NULL,
                                  object_version integer,
                                  creation_time timestamp without time zone,
                                  creator_id character varying,
                                  name character varying,
                                  start_time time without time zone NOT NULL,
                                  end_time time without time zone NOT NULL,
                                  created_at timestamp without time zone,
                                  updated_at timestamp without time zone
);


ALTER TABLE public.timebands OWNER TO chouette;

--
-- Name: timebands_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.timebands_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.timebands_id_seq OWNER TO chouette;

--
-- Name: timebands_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.timebands_id_seq OWNED BY public.timebands.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.users (
                              id bigint NOT NULL,
                              email character varying DEFAULT ''::character varying NOT NULL,
                              encrypted_password character varying DEFAULT ''::character varying,
                              reset_password_token character varying,
                              reset_password_sent_at timestamp without time zone,
                              remember_created_at timestamp without time zone,
                              sign_in_count integer DEFAULT 0,
                              current_sign_in_at timestamp without time zone,
                              last_sign_in_at timestamp without time zone,
                              current_sign_in_ip character varying,
                              last_sign_in_ip character varying,
                              created_at timestamp without time zone,
                              updated_at timestamp without time zone,
                              organisation_id bigint,
                              name character varying,
                              confirmation_token character varying,
                              confirmed_at timestamp without time zone,
                              confirmation_sent_at timestamp without time zone,
                              unconfirmed_email character varying,
                              failed_attempts integer DEFAULT 0,
                              unlock_token character varying,
                              locked_at timestamp without time zone,
                              authentication_token character varying,
                              invitation_token character varying,
                              invitation_sent_at timestamp without time zone,
                              invitation_accepted_at timestamp without time zone,
                              invitation_limit integer,
                              invited_by_id bigint,
                              invited_by_type character varying,
                              invitation_created_at timestamp without time zone,
                              role integer DEFAULT 1 NOT NULL,
                              provider character varying,
                              uid character varying
);


ALTER TABLE public.users OWNER TO chouette;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO chouette;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: vehicle_journey_at_stops; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.vehicle_journey_at_stops (
                                                 id bigint NOT NULL,
                                                 vehicle_journey_id bigint,
                                                 stop_point_id bigint,
                                                 connecting_service_id character varying,
                                                 boarding_alighting_possibility character varying,
                                                 arrival_time time without time zone,
                                                 departure_time time without time zone,
                                                 for_boarding character varying,
                                                 for_alighting character varying,
                                                 arrival_day_offset integer DEFAULT 0 NOT NULL,
                                                 departure_day_offset integer DEFAULT 0 NOT NULL,
                                                 objectid character varying,
                                                 object_version integer,
                                                 creator_id character varying,
                                                 creation_time timestamp without time zone
);


ALTER TABLE public.vehicle_journey_at_stops OWNER TO chouette;

--
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.vehicle_journey_at_stops_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.vehicle_journey_at_stops_id_seq OWNER TO chouette;

--
-- Name: vehicle_journey_at_stops_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.vehicle_journey_at_stops_id_seq OWNED BY public.vehicle_journey_at_stops.id;


--
-- Name: vehicle_journeys; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.vehicle_journeys (
                                         id bigint NOT NULL,
                                         route_id bigint,
                                         journey_pattern_id bigint,
                                         company_id bigint,
                                         objectid character varying NOT NULL,
                                         object_version integer,
                                         creation_time timestamp without time zone,
                                         creator_id character varying,
                                         comment character varying,
                                         status_value character varying,
                                         transport_mode character varying,
                                         published_journey_name character varying,
                                         published_journey_identifier character varying,
                                         facility character varying,
                                         vehicle_type_identifier character varying,
                                         number bigint,
                                         mobility_restricted_suitability boolean,
                                         flexible_service boolean,
                                         journey_category integer DEFAULT 0 NOT NULL,
                                         transport_submode_name character varying,
                                         private_code character varying,
                                         service_alteration character varying,
                                         flexible_service_properties_id bigint
);


ALTER TABLE public.vehicle_journeys OWNER TO chouette;

--
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE; Schema: public; Owner: chouette
--

CREATE SEQUENCE public.vehicle_journeys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.vehicle_journeys_id_seq OWNER TO chouette;

--
-- Name: vehicle_journeys_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: chouette
--

ALTER SEQUENCE public.vehicle_journeys_id_seq OWNED BY public.vehicle_journeys.id;


--
-- Name: vehicle_journeys_key_values; Type: TABLE; Schema: public; Owner: chouette
--

CREATE TABLE public.vehicle_journeys_key_values (
                                                    vehicle_journey_id bigint,
                                                    type_of_key character varying,
                                                    key character varying,
                                                    value character varying
);


ALTER TABLE public.vehicle_journeys_key_values OWNER TO chouette;

--
-- Name: blocks id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks ALTER COLUMN id SET DEFAULT nextval('chouette_gui.blocks_id_seq'::regclass);


--
-- Name: booking_arrangements id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.booking_arrangements ALTER COLUMN id SET DEFAULT nextval('chouette_gui.booking_arrangements_id_seq'::regclass);


--
-- Name: brandings id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.brandings ALTER COLUMN id SET DEFAULT nextval('chouette_gui.brandings_id_seq'::regclass);


--
-- Name: contact_structures id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.contact_structures ALTER COLUMN id SET DEFAULT nextval('chouette_gui.contact_structures_id_seq'::regclass);


--
-- Name: dated_service_journeys id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dated_service_journeys ALTER COLUMN id SET DEFAULT nextval('chouette_gui.dated_service_journeys_id_seq'::regclass);


--
-- Name: dead_run_at_stops id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_run_at_stops ALTER COLUMN id SET DEFAULT nextval('chouette_gui.dead_run_at_stops_id_seq'::regclass);


--
-- Name: dead_runs id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_runs ALTER COLUMN id SET DEFAULT nextval('chouette_gui.dead_runs_id_seq'::regclass);


--
-- Name: destination_displays id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.destination_displays ALTER COLUMN id SET DEFAULT nextval('chouette_gui.destination_displays_id_seq'::regclass);


--
-- Name: flexible_service_properties id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.flexible_service_properties ALTER COLUMN id SET DEFAULT nextval('chouette_gui.flexible_service_properties_id_seq'::regclass);


--
-- Name: footnote_alternative_texts id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnote_alternative_texts ALTER COLUMN id SET DEFAULT nextval('chouette_gui.footnote_alternative_texts_id_seq'::regclass);


--
-- Name: interchanges id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.interchanges ALTER COLUMN id SET DEFAULT nextval('chouette_gui.interchanges_id_seq'::regclass);


--
-- Name: referential_last_update id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.referential_last_update ALTER COLUMN id SET DEFAULT nextval('chouette_gui.referential_last_update_id_seq'::regclass);


--
-- Name: route_points id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.route_points ALTER COLUMN id SET DEFAULT nextval('chouette_gui.route_points_id_seq'::regclass);


--
-- Name: routes_route_points id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes_route_points ALTER COLUMN id SET DEFAULT nextval('chouette_gui.routes_route_points_id_seq'::regclass);


--
-- Name: scheduled_stop_points id; Type: DEFAULT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.scheduled_stop_points ALTER COLUMN id SET DEFAULT nextval('chouette_gui.scheduled_stop_points_id_seq'::regclass);


--
-- Name: access_links id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_links ALTER COLUMN id SET DEFAULT nextval('public.access_links_id_seq'::regclass);


--
-- Name: access_points id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_points ALTER COLUMN id SET DEFAULT nextval('public.access_points_id_seq'::regclass);


--
-- Name: api_keys id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.api_keys ALTER COLUMN id SET DEFAULT nextval('public.api_keys_id_seq'::regclass);


--
-- Name: blocks id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks ALTER COLUMN id SET DEFAULT nextval('public.blocks_id_seq'::regclass);


--
-- Name: booking_arrangements id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.booking_arrangements ALTER COLUMN id SET DEFAULT nextval('public.booking_arrangements_id_seq'::regclass);


--
-- Name: brandings id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.brandings ALTER COLUMN id SET DEFAULT nextval('public.brandings_id_seq'::regclass);


--
-- Name: codespaces id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.codespaces ALTER COLUMN id SET DEFAULT nextval('public.codespaces_id_seq'::regclass);


--
-- Name: companies id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.companies ALTER COLUMN id SET DEFAULT nextval('public.companies_id_seq'::regclass);


--
-- Name: connection_links id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.connection_links ALTER COLUMN id SET DEFAULT nextval('public.connection_links_id_seq'::regclass);


--
-- Name: contact_structures id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.contact_structures ALTER COLUMN id SET DEFAULT nextval('public.contact_structures_id_seq'::regclass);


--
-- Name: dated_service_journeys id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dated_service_journeys ALTER COLUMN id SET DEFAULT nextval('public.dated_service_journeys_id_seq'::regclass);


--
-- Name: dead_run_at_stops id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_run_at_stops ALTER COLUMN id SET DEFAULT nextval('public.dead_run_at_stops_id_seq'::regclass);


--
-- Name: dead_runs id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_runs ALTER COLUMN id SET DEFAULT nextval('public.dead_runs_id_seq'::regclass);


--
-- Name: delayed_jobs id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.delayed_jobs ALTER COLUMN id SET DEFAULT nextval('public.delayed_jobs_id_seq'::regclass);


--
-- Name: destination_displays id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.destination_displays ALTER COLUMN id SET DEFAULT nextval('public.destination_displays_id_seq'::regclass);


--
-- Name: exports id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.exports ALTER COLUMN id SET DEFAULT nextval('public.exports_id_seq'::regclass);


--
-- Name: facilities id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.facilities ALTER COLUMN id SET DEFAULT nextval('public.facilities_id_seq'::regclass);


--
-- Name: flexible_service_properties id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.flexible_service_properties ALTER COLUMN id SET DEFAULT nextval('public.flexible_service_properties_id_seq'::regclass);


--
-- Name: footnote_alternative_texts id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnote_alternative_texts ALTER COLUMN id SET DEFAULT nextval('public.footnote_alternative_texts_id_seq'::regclass);


--
-- Name: footnotes id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes ALTER COLUMN id SET DEFAULT nextval('public.footnotes_id_seq'::regclass);


--
-- Name: group_of_lines id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.group_of_lines ALTER COLUMN id SET DEFAULT nextval('public.group_of_lines_id_seq'::regclass);


--
-- Name: interchanges id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.interchanges ALTER COLUMN id SET DEFAULT nextval('public.interchanges_id_seq'::regclass);


--
-- Name: journey_frequencies id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_frequencies ALTER COLUMN id SET DEFAULT nextval('public.journey_frequencies_id_seq'::regclass);


--
-- Name: journey_pattern_sections id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_pattern_sections ALTER COLUMN id SET DEFAULT nextval('public.journey_pattern_sections_id_seq'::regclass);


--
-- Name: journey_patterns id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns ALTER COLUMN id SET DEFAULT nextval('public.journey_patterns_id_seq'::regclass);


--
-- Name: lines id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines ALTER COLUMN id SET DEFAULT nextval('public.lines_id_seq'::regclass);


--
-- Name: networks id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.networks ALTER COLUMN id SET DEFAULT nextval('public.networks_id_seq'::regclass);


--
-- Name: organisations id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.organisations ALTER COLUMN id SET DEFAULT nextval('public.organisations_id_seq'::regclass);


--
-- Name: pt_links id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.pt_links ALTER COLUMN id SET DEFAULT nextval('public.pt_links_id_seq'::regclass);


--
-- Name: referential_last_update id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.referential_last_update ALTER COLUMN id SET DEFAULT nextval('public.referential_last_update_id_seq'::regclass);


--
-- Name: referentials id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.referentials ALTER COLUMN id SET DEFAULT nextval('public.referentials_id_seq'::regclass);


--
-- Name: route_points id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.route_points ALTER COLUMN id SET DEFAULT nextval('public.route_points_id_seq'::regclass);


--
-- Name: route_sections id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.route_sections ALTER COLUMN id SET DEFAULT nextval('public.route_sections_id_seq'::regclass);


--
-- Name: routes id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes ALTER COLUMN id SET DEFAULT nextval('public.routes_id_seq'::regclass);


--
-- Name: routes_route_points id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes_route_points ALTER COLUMN id SET DEFAULT nextval('public.routes_route_points_id_seq'::regclass);


--
-- Name: rule_parameter_sets id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.rule_parameter_sets ALTER COLUMN id SET DEFAULT nextval('public.rule_parameter_sets_id_seq'::regclass);


--
-- Name: scheduled_stop_points id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.scheduled_stop_points ALTER COLUMN id SET DEFAULT nextval('public.scheduled_stop_points_id_seq'::regclass);


--
-- Name: stop_areas id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas ALTER COLUMN id SET DEFAULT nextval('public.stop_areas_id_seq'::regclass);


--
-- Name: stop_points id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points ALTER COLUMN id SET DEFAULT nextval('public.stop_points_id_seq'::regclass);


--
-- Name: taggings id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.taggings ALTER COLUMN id SET DEFAULT nextval('public.taggings_id_seq'::regclass);


--
-- Name: tags id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.tags ALTER COLUMN id SET DEFAULT nextval('public.tags_id_seq'::regclass);


--
-- Name: time_table_dates id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_dates ALTER COLUMN id SET DEFAULT nextval('public.time_table_dates_id_seq'::regclass);


--
-- Name: time_table_periods id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_periods ALTER COLUMN id SET DEFAULT nextval('public.time_table_periods_id_seq'::regclass);


--
-- Name: time_tables id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables ALTER COLUMN id SET DEFAULT nextval('public.time_tables_id_seq'::regclass);


--
-- Name: timebands id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.timebands ALTER COLUMN id SET DEFAULT nextval('public.timebands_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: vehicle_journey_at_stops id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journey_at_stops ALTER COLUMN id SET DEFAULT nextval('public.vehicle_journey_at_stops_id_seq'::regclass);


--
-- Name: vehicle_journeys id; Type: DEFAULT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys ALTER COLUMN id SET DEFAULT nextval('public.vehicle_journeys_id_seq'::regclass);


--
-- Name: access_links access_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.access_links
    ADD CONSTRAINT access_links_pkey PRIMARY KEY (id);


--
-- Name: access_points access_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.access_points
    ADD CONSTRAINT access_points_pkey PRIMARY KEY (id);


--
-- Name: api_keys api_keys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.api_keys
    ADD CONSTRAINT api_keys_pkey PRIMARY KEY (id);


--
-- Name: blocks_dead_runs blocks_dead_runs_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_pkey PRIMARY KEY (block_id, dead_run_id);


--
-- Name: blocks blocks_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks
    ADD CONSTRAINT blocks_pkey PRIMARY KEY (id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_pkey PRIMARY KEY (block_id, vehicle_journey_id);


--
-- Name: booking_arrangements booking_arrangements_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.booking_arrangements
    ADD CONSTRAINT booking_arrangements_pkey PRIMARY KEY (id);


--
-- Name: brandings brandings_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.brandings
    ADD CONSTRAINT brandings_pkey PRIMARY KEY (id);


--
-- Name: codespaces codespaces_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.codespaces
    ADD CONSTRAINT codespaces_pkey PRIMARY KEY (id);


--
-- Name: companies companies_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (id);


--
-- Name: connection_links connection_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.connection_links
    ADD CONSTRAINT connection_links_pkey PRIMARY KEY (id);


--
-- Name: contact_structures contact_structures_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.contact_structures
    ADD CONSTRAINT contact_structures_pkey PRIMARY KEY (id);


--
-- Name: dated_service_journeys dated_service_journeys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dated_service_journeys
    ADD CONSTRAINT dated_service_journeys_pkey PRIMARY KEY (id);


--
-- Name: dead_run_at_stops dead_run_at_stops_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_pkey PRIMARY KEY (id);


--
-- Name: dead_runs dead_runs_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_runs
    ADD CONSTRAINT dead_runs_pkey PRIMARY KEY (id);


--
-- Name: delayed_jobs delayed_jobs_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.delayed_jobs
    ADD CONSTRAINT delayed_jobs_pkey PRIMARY KEY (id);


--
-- Name: destination_displays destination_displays_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.destination_displays
    ADD CONSTRAINT destination_displays_pkey PRIMARY KEY (id);


--
-- Name: exports exports_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.exports
    ADD CONSTRAINT exports_pkey PRIMARY KEY (id);


--
-- Name: facilities facilities_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- Name: flexible_service_properties flexible_service_properties_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.flexible_service_properties
    ADD CONSTRAINT flexible_service_properties_pkey PRIMARY KEY (id);


--
-- Name: footnote_alternative_texts footnote_alternative_texts_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnote_alternative_texts
    ADD CONSTRAINT footnote_alternative_texts_pkey PRIMARY KEY (id);


--
-- Name: footnotes footnotes_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes
    ADD CONSTRAINT footnotes_pkey PRIMARY KEY (id);


--
-- Name: group_of_lines group_of_lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.group_of_lines
    ADD CONSTRAINT group_of_lines_pkey PRIMARY KEY (id);


--
-- Name: interchanges interchanges_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.interchanges
    ADD CONSTRAINT interchanges_pkey PRIMARY KEY (id);


--
-- Name: journey_frequencies journey_frequencies_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_frequencies
    ADD CONSTRAINT journey_frequencies_pkey PRIMARY KEY (id);


--
-- Name: journey_pattern_sections journey_pattern_sections_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_pattern_sections
    ADD CONSTRAINT journey_pattern_sections_pkey PRIMARY KEY (id);


--
-- Name: journey_patterns journey_patterns_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns
    ADD CONSTRAINT journey_patterns_pkey PRIMARY KEY (id);


--
-- Name: lines lines_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.lines
    ADD CONSTRAINT lines_pkey PRIMARY KEY (id);


--
-- Name: networks networks_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.networks
    ADD CONSTRAINT networks_pkey PRIMARY KEY (id);


--
-- Name: organisations organisations_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.organisations
    ADD CONSTRAINT organisations_pkey PRIMARY KEY (id);


--
-- Name: pt_links pt_links_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.pt_links
    ADD CONSTRAINT pt_links_pkey PRIMARY KEY (id);


--
-- Name: referential_last_update referential_last_update_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.referential_last_update
    ADD CONSTRAINT referential_last_update_pkey PRIMARY KEY (id);


--
-- Name: referentials referentials_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.referentials
    ADD CONSTRAINT referentials_pkey PRIMARY KEY (id);


--
-- Name: route_points route_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.route_points
    ADD CONSTRAINT route_points_pkey PRIMARY KEY (id);


--
-- Name: route_sections route_sections_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.route_sections
    ADD CONSTRAINT route_sections_pkey PRIMARY KEY (id);


--
-- Name: routes routes_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (id);


--
-- Name: routes_route_points routes_route_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes_route_points
    ADD CONSTRAINT routes_route_points_pkey PRIMARY KEY (id);


--
-- Name: rule_parameter_sets rule_parameter_sets_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.rule_parameter_sets
    ADD CONSTRAINT rule_parameter_sets_pkey PRIMARY KEY (id);


--
-- Name: scheduled_stop_points scheduled_stop_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.scheduled_stop_points
    ADD CONSTRAINT scheduled_stop_points_pkey PRIMARY KEY (id);


--
-- Name: stop_areas stop_areas_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_areas
    ADD CONSTRAINT stop_areas_pkey PRIMARY KEY (id);


--
-- Name: stop_points stop_points_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_points
    ADD CONSTRAINT stop_points_pkey PRIMARY KEY (id);


--
-- Name: taggings taggings_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.taggings
    ADD CONSTRAINT taggings_pkey PRIMARY KEY (id);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (id);


--
-- Name: time_table_dates time_table_dates_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_table_dates
    ADD CONSTRAINT time_table_dates_pkey PRIMARY KEY (id);


--
-- Name: time_table_periods time_table_periods_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_table_periods
    ADD CONSTRAINT time_table_periods_pkey PRIMARY KEY (id);


--
-- Name: time_tables_blocks time_tables_blocks_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_pkey PRIMARY KEY (time_table_id, block_id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_pkey PRIMARY KEY (time_table_id, dead_run_id);


--
-- Name: time_tables time_tables_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables
    ADD CONSTRAINT time_tables_pkey PRIMARY KEY (id);


--
-- Name: timebands timebands_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.timebands
    ADD CONSTRAINT timebands_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vehicle_journey_at_stops vehicle_journey_at_stops_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journey_at_stops
    ADD CONSTRAINT vehicle_journey_at_stops_pkey PRIMARY KEY (id);


--
-- Name: vehicle_journeys vehicle_journeys_pkey; Type: CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_pkey PRIMARY KEY (id);


--
-- Name: access_links access_links_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT access_links_pkey PRIMARY KEY (id);


--
-- Name: access_points access_points_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_points
    ADD CONSTRAINT access_points_pkey PRIMARY KEY (id);


--
-- Name: api_keys api_keys_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.api_keys
    ADD CONSTRAINT api_keys_pkey PRIMARY KEY (id);


--
-- Name: blocks_dead_runs blocks_dead_runs_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_pkey PRIMARY KEY (block_id, dead_run_id);


--
-- Name: blocks blocks_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks
    ADD CONSTRAINT blocks_pkey PRIMARY KEY (id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_pkey PRIMARY KEY (block_id, vehicle_journey_id);


--
-- Name: booking_arrangements booking_arrangements_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.booking_arrangements
    ADD CONSTRAINT booking_arrangements_pkey PRIMARY KEY (id);


--
-- Name: brandings brandings_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.brandings
    ADD CONSTRAINT brandings_pkey PRIMARY KEY (id);


--
-- Name: codespaces codespaces_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.codespaces
    ADD CONSTRAINT codespaces_pkey PRIMARY KEY (id);


--
-- Name: companies companies_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (id);


--
-- Name: connection_links connection_links_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.connection_links
    ADD CONSTRAINT connection_links_pkey PRIMARY KEY (id);


--
-- Name: contact_structures contact_structures_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.contact_structures
    ADD CONSTRAINT contact_structures_pkey PRIMARY KEY (id);


--
-- Name: dated_service_journeys dated_service_journeys_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dated_service_journeys
    ADD CONSTRAINT dated_service_journeys_pkey PRIMARY KEY (id);


--
-- Name: dead_run_at_stops dead_run_at_stops_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_pkey PRIMARY KEY (id);


--
-- Name: dead_runs dead_runs_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_runs
    ADD CONSTRAINT dead_runs_pkey PRIMARY KEY (id);


--
-- Name: delayed_jobs delayed_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.delayed_jobs
    ADD CONSTRAINT delayed_jobs_pkey PRIMARY KEY (id);


--
-- Name: destination_displays destination_displays_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.destination_displays
    ADD CONSTRAINT destination_displays_pkey PRIMARY KEY (id);


--
-- Name: exports exports_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.exports
    ADD CONSTRAINT exports_pkey PRIMARY KEY (id);


--
-- Name: facilities facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- Name: flexible_service_properties flexible_service_properties_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.flexible_service_properties
    ADD CONSTRAINT flexible_service_properties_pkey PRIMARY KEY (id);


--
-- Name: footnote_alternative_texts footnote_alternative_texts_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnote_alternative_texts
    ADD CONSTRAINT footnote_alternative_texts_pkey PRIMARY KEY (id);


--
-- Name: footnotes footnotes_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes
    ADD CONSTRAINT footnotes_pkey PRIMARY KEY (id);


--
-- Name: group_of_lines group_of_lines_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.group_of_lines
    ADD CONSTRAINT group_of_lines_pkey PRIMARY KEY (id);


--
-- Name: interchanges interchanges_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.interchanges
    ADD CONSTRAINT interchanges_pkey PRIMARY KEY (id);


--
-- Name: journey_frequencies journey_frequencies_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_frequencies
    ADD CONSTRAINT journey_frequencies_pkey PRIMARY KEY (id);


--
-- Name: journey_pattern_sections journey_pattern_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_pattern_sections
    ADD CONSTRAINT journey_pattern_sections_pkey PRIMARY KEY (id);


--
-- Name: journey_patterns journey_patterns_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns
    ADD CONSTRAINT journey_patterns_pkey PRIMARY KEY (id);


--
-- Name: lines lines_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines
    ADD CONSTRAINT lines_pkey PRIMARY KEY (id);


--
-- Name: networks networks_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.networks
    ADD CONSTRAINT networks_pkey PRIMARY KEY (id);


--
-- Name: organisations organisations_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.organisations
    ADD CONSTRAINT organisations_pkey PRIMARY KEY (id);


--
-- Name: pt_links pt_links_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.pt_links
    ADD CONSTRAINT pt_links_pkey PRIMARY KEY (id);


--
-- Name: referential_last_update referential_last_update_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.referential_last_update
    ADD CONSTRAINT referential_last_update_pkey PRIMARY KEY (id);


--
-- Name: referentials referentials_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.referentials
    ADD CONSTRAINT referentials_pkey PRIMARY KEY (id);


--
-- Name: route_points route_points_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.route_points
    ADD CONSTRAINT route_points_pkey PRIMARY KEY (id);


--
-- Name: route_sections route_sections_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.route_sections
    ADD CONSTRAINT route_sections_pkey PRIMARY KEY (id);


--
-- Name: routes routes_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (id);


--
-- Name: routes_route_points routes_route_points_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes_route_points
    ADD CONSTRAINT routes_route_points_pkey PRIMARY KEY (id);


--
-- Name: rule_parameter_sets rule_parameter_sets_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.rule_parameter_sets
    ADD CONSTRAINT rule_parameter_sets_pkey PRIMARY KEY (id);


--
-- Name: scheduled_stop_points scheduled_stop_points_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.scheduled_stop_points
    ADD CONSTRAINT scheduled_stop_points_pkey PRIMARY KEY (id);


--
-- Name: stop_areas stop_areas_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas
    ADD CONSTRAINT stop_areas_pkey PRIMARY KEY (id);


--
-- Name: stop_points stop_points_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points
    ADD CONSTRAINT stop_points_pkey PRIMARY KEY (id);


--
-- Name: taggings taggings_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.taggings
    ADD CONSTRAINT taggings_pkey PRIMARY KEY (id);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (id);


--
-- Name: time_table_dates time_table_dates_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_dates
    ADD CONSTRAINT time_table_dates_pkey PRIMARY KEY (id);


--
-- Name: time_table_periods time_table_periods_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_periods
    ADD CONSTRAINT time_table_periods_pkey PRIMARY KEY (id);


--
-- Name: time_tables_blocks time_tables_blocks_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_pkey PRIMARY KEY (time_table_id, block_id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_pkey PRIMARY KEY (time_table_id, dead_run_id);


--
-- Name: time_tables time_tables_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables
    ADD CONSTRAINT time_tables_pkey PRIMARY KEY (id);


--
-- Name: timebands timebands_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.timebands
    ADD CONSTRAINT timebands_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vehicle_journey_at_stops vehicle_journey_at_stops_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journey_at_stops
    ADD CONSTRAINT vehicle_journey_at_stops_pkey PRIMARY KEY (id);


--
-- Name: vehicle_journeys vehicle_journeys_pkey; Type: CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_pkey PRIMARY KEY (id);


--
-- Name: access_links_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX access_links_objectid_idx ON chouette_gui.access_links USING btree (objectid);


--
-- Name: access_points_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX access_points_objectid_idx ON chouette_gui.access_points USING btree (objectid);


--
-- Name: blocks_dead_runs_dead_run_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX blocks_dead_runs_dead_run_id_idx ON chouette_gui.blocks_dead_runs USING btree (dead_run_id);


--
-- Name: blocks_end_point_id_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX blocks_end_point_id_key ON chouette_gui.blocks USING btree (end_point_id);


--
-- Name: blocks_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX blocks_objectid_key ON chouette_gui.blocks USING btree (objectid);


--
-- Name: blocks_start_point_id_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX blocks_start_point_id_key ON chouette_gui.blocks USING btree (start_point_id);


--
-- Name: blocks_vehicle_journeys_vehicle_journey_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX blocks_vehicle_journeys_vehicle_journey_id_idx ON chouette_gui.blocks_vehicle_journeys USING btree (vehicle_journey_id);


--
-- Name: brandings_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX brandings_objectid_key ON chouette_gui.brandings USING btree (objectid);


--
-- Name: companies_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX companies_objectid_idx ON chouette_gui.companies USING btree (objectid);


--
-- Name: companies_registration_number_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX companies_registration_number_idx ON chouette_gui.companies USING btree (registration_number);


--
-- Name: connection_links_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX connection_links_objectid_idx ON chouette_gui.connection_links USING btree (objectid);


--
-- Name: dated_service_journey_refs_derived_dsj_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX dated_service_journey_refs_derived_dsj_id_idx ON chouette_gui.dated_service_journey_refs USING btree (derived_dsj_id);


--
-- Name: dated_service_journey_refs_original_dsj_id_derived_dsj_id_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX dated_service_journey_refs_original_dsj_id_derived_dsj_id_key ON chouette_gui.dated_service_journey_refs USING btree (original_dsj_id, derived_dsj_id);


--
-- Name: dated_service_journeys_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX dated_service_journeys_objectid_key ON chouette_gui.dated_service_journeys USING btree (objectid);


--
-- Name: dead_runs_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX dead_runs_objectid_key ON chouette_gui.dead_runs USING btree (objectid);


--
-- Name: delayed_jobs_priority_run_at_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX delayed_jobs_priority_run_at_idx ON chouette_gui.delayed_jobs USING btree (priority, run_at);


--
-- Name: exports_referential_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX exports_referential_id_idx ON chouette_gui.exports USING btree (referential_id);


--
-- Name: facilities_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX facilities_objectid_idx ON chouette_gui.facilities USING btree (objectid);


--
-- Name: flexible_service_propertiess_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX flexible_service_propertiess_objectid_key ON chouette_gui.flexible_service_properties USING btree (objectid);


--
-- Name: footnote_alternative_texts_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX footnote_alternative_texts_objectid_key ON chouette_gui.footnote_alternative_texts USING btree (objectid);


--
-- Name: footnotes_footnote_line_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_footnote_line_id_idx ON chouette_gui.footnotes_lines USING btree (footnote_id);


--
-- Name: footnotes_id_journey_patterns_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_id_journey_patterns_id_idx ON chouette_gui.footnotes_journey_patterns USING btree (footnote_id);


--
-- Name: footnotes_journey_patterns_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_journey_patterns_id_idx ON chouette_gui.footnotes_journey_patterns USING btree (journey_pattern_id);


--
-- Name: footnotes_line_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_line_id_idx ON chouette_gui.footnotes_lines USING btree (line_id);


--
-- Name: footnotes_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX footnotes_objectid_key ON chouette_gui.footnotes USING btree (objectid);


--
-- Name: footnotes_stop_point_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_stop_point_id_idx ON chouette_gui.footnotes_stop_points USING btree (footnote_id);


--
-- Name: footnotes_vehicle_journey_at_stop_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX footnotes_vehicle_journey_at_stop_id_idx ON chouette_gui.footnotes_vehicle_journey_at_stops USING btree (footnote_id);


--
-- Name: group_of_lines_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX group_of_lines_objectid_idx ON chouette_gui.group_of_lines USING btree (objectid);


--
-- Name: index_dead_run_at_stops_on_dead_run_id; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_dead_run_at_stops_on_dead_run_id ON chouette_gui.dead_run_at_stops USING btree (dead_run_id);


--
-- Name: index_dead_run_at_stops_on_stop_pointid; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_dead_run_at_stops_on_stop_pointid ON chouette_gui.dead_run_at_stops USING btree (stop_point_id);


--
-- Name: index_destination_display_id_on_destination_display_via; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_destination_display_id_on_destination_display_via ON chouette_gui.destination_display_via USING btree (destination_display_id);


--
-- Name: index_stop_areas_on_name; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_stop_areas_on_name ON chouette_gui.stop_areas USING btree (name);


--
-- Name: index_stop_areas_on_stop_place_type; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_stop_areas_on_stop_place_type ON chouette_gui.stop_areas USING btree (stop_place_type);


--
-- Name: index_stop_areas_on_transport_mode; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_stop_areas_on_transport_mode ON chouette_gui.stop_areas USING btree (transport_mode);


--
-- Name: index_stop_areas_on_transport_sub_mode; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX index_stop_areas_on_transport_sub_mode ON chouette_gui.stop_areas USING btree (transport_sub_mode);


--
-- Name: interchanges_from_point_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX interchanges_from_point_key ON chouette_gui.interchanges USING btree (from_point);


--
-- Name: interchanges_from_vehicle_journey_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX interchanges_from_vehicle_journey_key ON chouette_gui.interchanges USING btree (from_vehicle_journey);


--
-- Name: interchanges_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX interchanges_objectid_key ON chouette_gui.interchanges USING btree (objectid);


--
-- Name: interchanges_to_poinnt_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX interchanges_to_poinnt_key ON chouette_gui.interchanges USING btree (to_point);


--
-- Name: interchanges_to_vehicle_journey_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX interchanges_to_vehicle_journey_key ON chouette_gui.interchanges USING btree (objectid);


--
-- Name: journey_frequencies_timeband_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX journey_frequencies_timeband_id_idx ON chouette_gui.journey_frequencies USING btree (timeband_id);


--
-- Name: journey_frequencies_vehicle_journey_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX journey_frequencies_vehicle_journey_id_idx ON chouette_gui.journey_frequencies USING btree (vehicle_journey_id);


--
-- Name: journey_pattern_sections_journey_pattern_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX journey_pattern_sections_journey_pattern_id_idx ON chouette_gui.journey_pattern_sections USING btree (journey_pattern_id);


--
-- Name: journey_pattern_sections_journey_pattern_id_route_section_i_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX journey_pattern_sections_journey_pattern_id_route_section_i_idx ON chouette_gui.journey_pattern_sections USING btree (journey_pattern_id, route_section_id, rank);


--
-- Name: journey_pattern_sections_route_section_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX journey_pattern_sections_route_section_id_idx ON chouette_gui.journey_pattern_sections USING btree (route_section_id);


--
-- Name: journey_patterns_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX journey_patterns_objectid_idx ON chouette_gui.journey_patterns USING btree (objectid);


--
-- Name: journey_patterns_stop_points_journey_pattern_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX journey_patterns_stop_points_journey_pattern_id_idx ON chouette_gui.journey_patterns_stop_points USING btree (journey_pattern_id);


--
-- Name: lines_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX lines_objectid_idx ON chouette_gui.lines USING btree (objectid);


--
-- Name: lines_registration_number_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX lines_registration_number_idx ON chouette_gui.lines USING btree (registration_number);


--
-- Name: networks_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX networks_objectid_idx ON chouette_gui.networks USING btree (objectid);


--
-- Name: networks_registration_number_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX networks_registration_number_idx ON chouette_gui.networks USING btree (registration_number);


--
-- Name: pt_links_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX pt_links_objectid_idx ON chouette_gui.pt_links USING btree (objectid);


--
-- Name: referentials_name_organisation_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX referentials_name_organisation_id_idx ON chouette_gui.referentials USING btree (name, organisation_id);


--
-- Name: route_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX route_points_objectid_key ON chouette_gui.route_points USING btree (objectid);


--
-- Name: route_sections_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX route_sections_objectid_key ON chouette_gui.route_sections USING btree (objectid);


--
-- Name: routes_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX routes_objectid_idx ON chouette_gui.routes USING btree (objectid);


--
-- Name: routing_constraints_lines_stop_area_objectid_key_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX routing_constraints_lines_stop_area_objectid_key_idx ON chouette_gui.routing_constraints_lines USING btree (stop_area_objectid_key);


--
-- Name: scheduled_stop_points_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX scheduled_stop_points_objectid_key ON chouette_gui.scheduled_stop_points USING btree (objectid);


--
-- Name: scheduled_stop_points_stop_area_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX scheduled_stop_points_stop_area_idx ON chouette_gui.scheduled_stop_points USING btree (stop_area_objectid_key);


--
-- Name: schema_migrations_version_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX schema_migrations_version_idx ON chouette_gui.schema_migrations USING btree (version);


--
-- Name: stop_areas_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX stop_areas_objectid_idx ON chouette_gui.stop_areas USING btree (objectid);


--
-- Name: stop_areas_parent_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX stop_areas_parent_id_idx ON chouette_gui.stop_areas USING btree (parent_id);


--
-- Name: stop_point_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX stop_point_id_idx ON chouette_gui.footnotes_stop_points USING btree (stop_point_id);


--
-- Name: stop_points_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX stop_points_objectid_idx ON chouette_gui.stop_points USING btree (objectid);


--
-- Name: taggings_tag_id_taggable_id_taggable_type_context_tagger_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX taggings_tag_id_taggable_id_taggable_type_context_tagger_id_idx ON chouette_gui.taggings USING btree (tag_id, taggable_id, taggable_type, context, tagger_id, tagger_type);


--
-- Name: taggings_taggable_id_taggable_type_context_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX taggings_taggable_id_taggable_type_context_idx ON chouette_gui.taggings USING btree (taggable_id, taggable_type, context);


--
-- Name: tags_name_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX tags_name_idx ON chouette_gui.tags USING btree (name);


--
-- Name: time_table_dates_time_table_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_table_dates_time_table_id_idx ON chouette_gui.time_table_dates USING btree (time_table_id);


--
-- Name: time_table_periods_time_table_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_table_periods_time_table_id_idx ON chouette_gui.time_table_periods USING btree (time_table_id);


--
-- Name: time_tables_blocks_block_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_tables_blocks_block_id_idx ON chouette_gui.time_tables_blocks USING btree (block_id);


--
-- Name: time_tables_dead_runs_dead_run_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_tables_dead_runs_dead_run_id_idx ON chouette_gui.time_tables_dead_runs USING btree (dead_run_id);


--
-- Name: time_tables_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX time_tables_objectid_idx ON chouette_gui.time_tables USING btree (objectid);


--
-- Name: time_tables_vehicle_journeys_time_table_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_tables_vehicle_journeys_time_table_id_idx ON chouette_gui.time_tables_vehicle_journeys USING btree (time_table_id);


--
-- Name: time_tables_vehicle_journeys_vehicle_journey_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX time_tables_vehicle_journeys_vehicle_journey_id_idx ON chouette_gui.time_tables_vehicle_journeys USING btree (vehicle_journey_id);


--
-- Name: users_email_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX users_email_idx ON chouette_gui.users USING btree (email);


--
-- Name: users_invitation_token_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX users_invitation_token_idx ON chouette_gui.users USING btree (invitation_token);


--
-- Name: users_reset_password_token_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX users_reset_password_token_idx ON chouette_gui.users USING btree (reset_password_token);


--
-- Name: vehicle_journey_at_stop_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX vehicle_journey_at_stop_id_idx ON chouette_gui.footnotes_vehicle_journey_at_stops USING btree (vehicle_journey_at_stop_id);


--
-- Name: vehicle_journey_at_stops_objectid_key; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX vehicle_journey_at_stops_objectid_key ON chouette_gui.vehicle_journey_at_stops USING btree (objectid);


--
-- Name: vehicle_journey_at_stops_stop_point_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX vehicle_journey_at_stops_stop_point_id_idx ON chouette_gui.vehicle_journey_at_stops USING btree (stop_point_id);


--
-- Name: vehicle_journey_at_stops_vehicle_journey_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX vehicle_journey_at_stops_vehicle_journey_id_idx ON chouette_gui.vehicle_journey_at_stops USING btree (vehicle_journey_id);


--
-- Name: vehicle_journeys_objectid_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE UNIQUE INDEX vehicle_journeys_objectid_idx ON chouette_gui.vehicle_journeys USING btree (objectid);


--
-- Name: vehicle_journeys_route_id_idx; Type: INDEX; Schema: chouette_gui; Owner: chouette
--

CREATE INDEX vehicle_journeys_route_id_idx ON chouette_gui.vehicle_journeys USING btree (route_id);


--
-- Name: access_links_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX access_links_objectid_key ON public.access_links USING btree (objectid);


--
-- Name: access_points_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX access_points_objectid_key ON public.access_points USING btree (objectid);


--
-- Name: blocks_dead_runs_dead_run_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX blocks_dead_runs_dead_run_id_idx ON public.blocks_dead_runs USING btree (dead_run_id);


--
-- Name: blocks_end_point_id_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX blocks_end_point_id_key ON public.blocks USING btree (end_point_id);


--
-- Name: blocks_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX blocks_objectid_key ON public.blocks USING btree (objectid);


--
-- Name: blocks_start_point_id_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX blocks_start_point_id_key ON public.blocks USING btree (start_point_id);


--
-- Name: blocks_vehicle_journeys_vehicle_journey_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX blocks_vehicle_journeys_vehicle_journey_id_idx ON public.blocks_vehicle_journeys USING btree (vehicle_journey_id);


--
-- Name: brandings_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX brandings_objectid_key ON public.brandings USING btree (objectid);


--
-- Name: companies_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX companies_objectid_key ON public.companies USING btree (objectid);


--
-- Name: companies_registration_number_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX companies_registration_number_key ON public.companies USING btree (registration_number);


--
-- Name: connection_links_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX connection_links_objectid_key ON public.connection_links USING btree (objectid);


--
-- Name: dated_service_journey_refs_derived_dsj_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX dated_service_journey_refs_derived_dsj_id_idx ON public.dated_service_journey_refs USING btree (derived_dsj_id);


--
-- Name: dated_service_journey_refs_original_dsj_id_derived_dsj_id_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX dated_service_journey_refs_original_dsj_id_derived_dsj_id_key ON public.dated_service_journey_refs USING btree (original_dsj_id, derived_dsj_id);


--
-- Name: dated_service_journeys_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX dated_service_journeys_objectid_key ON public.dated_service_journeys USING btree (objectid);


--
-- Name: dead_runs_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX dead_runs_objectid_key ON public.dead_runs USING btree (objectid);


--
-- Name: delayed_jobs_priority; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX delayed_jobs_priority ON public.delayed_jobs USING btree (priority, run_at);


--
-- Name: facilities_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX facilities_objectid_key ON public.facilities USING btree (objectid);


--
-- Name: flexible_service_propertiess_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX flexible_service_propertiess_objectid_key ON public.flexible_service_properties USING btree (objectid);


--
-- Name: footnote_alternative_texts_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX footnote_alternative_texts_objectid_key ON public.footnote_alternative_texts USING btree (objectid);


--
-- Name: footnotes_footnote_line_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_footnote_line_id_idx ON public.footnotes_lines USING btree (footnote_id);


--
-- Name: footnotes_id_journey_patterns_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_id_journey_patterns_id_idx ON public.footnotes_journey_patterns USING btree (footnote_id);


--
-- Name: footnotes_journey_patterns_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_journey_patterns_id_idx ON public.footnotes_journey_patterns USING btree (journey_pattern_id);


--
-- Name: footnotes_line_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_line_id_idx ON public.footnotes_lines USING btree (line_id);


--
-- Name: footnotes_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX footnotes_objectid_key ON public.footnotes USING btree (objectid);


--
-- Name: footnotes_stop_point_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_stop_point_id_idx ON public.footnotes_stop_points USING btree (footnote_id);


--
-- Name: footnotes_vehicle_journey_at_stop_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX footnotes_vehicle_journey_at_stop_id_idx ON public.footnotes_vehicle_journey_at_stops USING btree (footnote_id);


--
-- Name: group_of_lines_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX group_of_lines_objectid_key ON public.group_of_lines USING btree (objectid);


--
-- Name: index_dead_run_at_stops_on_dead_run_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_dead_run_at_stops_on_dead_run_id ON public.dead_run_at_stops USING btree (dead_run_id);


--
-- Name: index_dead_run_at_stops_on_stop_pointid; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_dead_run_at_stops_on_stop_pointid ON public.dead_run_at_stops USING btree (stop_point_id);


--
-- Name: index_destination_display_id_on_destination_display_via; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_destination_display_id_on_destination_display_via ON public.destination_display_via USING btree (destination_display_id);


--
-- Name: index_exports_on_referential_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_exports_on_referential_id ON public.exports USING btree (referential_id);


--
-- Name: index_journey_frequencies_on_timeband_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_journey_frequencies_on_timeband_id ON public.journey_frequencies USING btree (timeband_id);


--
-- Name: index_journey_frequencies_on_vehicle_journey_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_journey_frequencies_on_vehicle_journey_id ON public.journey_frequencies USING btree (vehicle_journey_id);


--
-- Name: index_journey_pattern_id_on_journey_patterns_stop_points; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_journey_pattern_id_on_journey_patterns_stop_points ON public.journey_patterns_stop_points USING btree (journey_pattern_id);


--
-- Name: index_journey_pattern_sections_on_journey_pattern_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_journey_pattern_sections_on_journey_pattern_id ON public.journey_pattern_sections USING btree (journey_pattern_id);


--
-- Name: index_journey_pattern_sections_on_route_section_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_journey_pattern_sections_on_route_section_id ON public.journey_pattern_sections USING btree (route_section_id);


--
-- Name: index_jps_on_journey_pattern_id_and_route_section_id_and_rank; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_jps_on_journey_pattern_id_and_route_section_id_and_rank ON public.journey_pattern_sections USING btree (journey_pattern_id, route_section_id, rank);


--
-- Name: index_referentials_on_name_and_organisation_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_referentials_on_name_and_organisation_id ON public.referentials USING btree (name, organisation_id);


--
-- Name: index_routing_constraints_lines_on_stop_area_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_routing_constraints_lines_on_stop_area_objectid_key ON public.routing_constraints_lines USING btree (stop_area_objectid_key);


--
-- Name: index_stop_areas_on_name; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_stop_areas_on_name ON public.stop_areas USING btree (name);


--
-- Name: index_stop_areas_on_parent_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_stop_areas_on_parent_id ON public.stop_areas USING btree (parent_id);


--
-- Name: index_stop_areas_on_stop_place_type; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_stop_areas_on_stop_place_type ON public.stop_areas USING btree (stop_place_type);


--
-- Name: index_stop_areas_on_transport_mode; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_stop_areas_on_transport_mode ON public.stop_areas USING btree (transport_mode);


--
-- Name: index_stop_areas_on_transport_sub_mode; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_stop_areas_on_transport_sub_mode ON public.stop_areas USING btree (transport_sub_mode);


--
-- Name: index_taggings_on_taggable_id_and_taggable_type_and_context; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_taggings_on_taggable_id_and_taggable_type_and_context ON public.taggings USING btree (taggable_id, taggable_type, context);


--
-- Name: index_tags_on_name; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_tags_on_name ON public.tags USING btree (name);


--
-- Name: index_time_table_dates_on_time_table_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_time_table_dates_on_time_table_id ON public.time_table_dates USING btree (time_table_id);


--
-- Name: index_time_table_periods_on_time_table_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_time_table_periods_on_time_table_id ON public.time_table_periods USING btree (time_table_id);


--
-- Name: index_time_tables_vehicle_journeys_on_time_table_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_time_tables_vehicle_journeys_on_time_table_id ON public.time_tables_vehicle_journeys USING btree (time_table_id);


--
-- Name: index_time_tables_vehicle_journeys_on_vehicle_journey_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_time_tables_vehicle_journeys_on_vehicle_journey_id ON public.time_tables_vehicle_journeys USING btree (vehicle_journey_id);


--
-- Name: index_users_on_email; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_users_on_email ON public.users USING btree (email);


--
-- Name: index_users_on_invitation_token; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_users_on_invitation_token ON public.users USING btree (invitation_token);


--
-- Name: index_users_on_reset_password_token; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX index_users_on_reset_password_token ON public.users USING btree (reset_password_token);


--
-- Name: index_vehicle_journey_at_stops_on_stop_pointid; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_vehicle_journey_at_stops_on_stop_pointid ON public.vehicle_journey_at_stops USING btree (stop_point_id);


--
-- Name: index_vehicle_journey_at_stops_on_vehicle_journey_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_vehicle_journey_at_stops_on_vehicle_journey_id ON public.vehicle_journey_at_stops USING btree (vehicle_journey_id);


--
-- Name: index_vehicle_journeys_on_route_id; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX index_vehicle_journeys_on_route_id ON public.vehicle_journeys USING btree (route_id);


--
-- Name: interchanges_from_point_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX interchanges_from_point_key ON public.interchanges USING btree (from_point);


--
-- Name: interchanges_from_vehicle_journey_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX interchanges_from_vehicle_journey_key ON public.interchanges USING btree (from_vehicle_journey);


--
-- Name: interchanges_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX interchanges_objectid_key ON public.interchanges USING btree (objectid);


--
-- Name: interchanges_to_poinnt_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX interchanges_to_poinnt_key ON public.interchanges USING btree (to_point);


--
-- Name: interchanges_to_vehicle_journey_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX interchanges_to_vehicle_journey_key ON public.interchanges USING btree (objectid);


--
-- Name: journey_patterns_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX journey_patterns_objectid_key ON public.journey_patterns USING btree (objectid);


--
-- Name: lines_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX lines_objectid_key ON public.lines USING btree (objectid);


--
-- Name: lines_registration_number_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX lines_registration_number_key ON public.lines USING btree (registration_number);


--
-- Name: networks_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX networks_objectid_key ON public.networks USING btree (objectid);


--
-- Name: networks_registration_number_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX networks_registration_number_key ON public.networks USING btree (registration_number);


--
-- Name: pt_links_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX pt_links_objectid_key ON public.pt_links USING btree (objectid);


--
-- Name: route_points_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX route_points_objectid_key ON public.route_points USING btree (objectid);


--
-- Name: route_sections_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX route_sections_objectid_key ON public.route_sections USING btree (objectid);


--
-- Name: routes_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX routes_objectid_key ON public.routes USING btree (objectid);


--
-- Name: scheduled_stop_points_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX scheduled_stop_points_objectid_key ON public.scheduled_stop_points USING btree (objectid);


--
-- Name: scheduled_stop_points_stop_area_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX scheduled_stop_points_stop_area_idx ON public.scheduled_stop_points USING btree (stop_area_objectid_key);


--
-- Name: stop_areas_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX stop_areas_objectid_key ON public.stop_areas USING btree (objectid);


--
-- Name: stop_point_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX stop_point_id_idx ON public.footnotes_stop_points USING btree (stop_point_id);


--
-- Name: stop_points_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX stop_points_objectid_key ON public.stop_points USING btree (objectid);


--
-- Name: taggings_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX taggings_idx ON public.taggings USING btree (tag_id, taggable_id, taggable_type, context, tagger_id, tagger_type);


--
-- Name: time_tables_blocks_block_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX time_tables_blocks_block_id_idx ON public.time_tables_blocks USING btree (block_id);


--
-- Name: time_tables_dead_runs_dead_run_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX time_tables_dead_runs_dead_run_id_idx ON public.time_tables_dead_runs USING btree (dead_run_id);


--
-- Name: time_tables_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX time_tables_objectid_key ON public.time_tables USING btree (objectid);


--
-- Name: unique_schema_migrations; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX unique_schema_migrations ON public.schema_migrations USING btree (version);


--
-- Name: vehicle_journey_at_stop_id_idx; Type: INDEX; Schema: public; Owner: chouette
--

CREATE INDEX vehicle_journey_at_stop_id_idx ON public.footnotes_vehicle_journey_at_stops USING btree (vehicle_journey_at_stop_id);


--
-- Name: vehicle_journey_at_stops_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX vehicle_journey_at_stops_objectid_key ON public.vehicle_journey_at_stops USING btree (objectid);


--
-- Name: vehicle_journeys_objectid_key; Type: INDEX; Schema: public; Owner: chouette
--

CREATE UNIQUE INDEX vehicle_journeys_objectid_key ON public.vehicle_journeys USING btree (objectid);


--
-- Name: access_points access_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.access_points
    ADD CONSTRAINT access_area_fkey FOREIGN KEY (stop_area_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: access_links aclk_acpt_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.access_links
    ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (access_point_id) REFERENCES chouette_gui.access_points(id) ON DELETE CASCADE;


--
-- Name: access_links aclk_area_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.access_links
    ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stop_area_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_areas area_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_areas
    ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parent_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE SET NULL;


--
-- Name: journey_patterns arrival_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns
    ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id) REFERENCES chouette_gui.stop_points(id) ON DELETE SET NULL;


--
-- Name: blocks_dead_runs blocks_dead_runs_block_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_block_id_fkey FOREIGN KEY (block_id) REFERENCES chouette_gui.blocks(id);


--
-- Name: blocks_dead_runs blocks_dead_runs_dead_run_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_dead_run_id_fkey FOREIGN KEY (dead_run_id) REFERENCES chouette_gui.dead_runs(id);


--
-- Name: blocks blocks_scheduled_stop_points_end_point_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks
    ADD CONSTRAINT blocks_scheduled_stop_points_end_point_id_fkey FOREIGN KEY (end_point_id) REFERENCES chouette_gui.scheduled_stop_points(id);


--
-- Name: blocks blocks_scheduled_stop_points_start_point_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks
    ADD CONSTRAINT blocks_scheduled_stop_points_start_point_id_fkey FOREIGN KEY (start_point_id) REFERENCES chouette_gui.scheduled_stop_points(id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_block_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_block_id_fkey FOREIGN KEY (block_id) REFERENCES chouette_gui.blocks(id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_vehicle_journey_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_vehicle_journey_id_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id);


--
-- Name: booking_arrangements booking_arrangement_booking_contact_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.booking_arrangements
    ADD CONSTRAINT booking_arrangement_booking_contact_fkey FOREIGN KEY (booking_contact_id) REFERENCES chouette_gui.contact_structures(id);


--
-- Name: booking_arrangements_buy_when booking_arrangement_buy_when_lines_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.booking_arrangements_buy_when
    ADD CONSTRAINT booking_arrangement_buy_when_lines_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id);


--
-- Name: booking_arrangements_booking_methods booking_arrangements_booking_methods_lines_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.booking_arrangements_booking_methods
    ADD CONSTRAINT booking_arrangements_booking_methods_lines_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id);


--
-- Name: connection_links colk_endarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.connection_links
    ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrival_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: connection_links colk_startarea_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.connection_links
    ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departure_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: dated_service_journey_refs dated_service_journey_refs_derived_dsj_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dated_service_journey_refs
    ADD CONSTRAINT dated_service_journey_refs_derived_dsj_id_fkey FOREIGN KEY (derived_dsj_id) REFERENCES chouette_gui.dated_service_journeys(id);


--
-- Name: dated_service_journey_refs dated_service_journey_refs_original_dsj_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dated_service_journey_refs
    ADD CONSTRAINT dated_service_journey_refs_original_dsj_id_fkey FOREIGN KEY (original_dsj_id) REFERENCES chouette_gui.dated_service_journeys(id);


--
-- Name: dated_service_journeys dated_service_journeys_vehicle_journey_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dated_service_journeys
    ADD CONSTRAINT dated_service_journeys_vehicle_journey_id_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id);


--
-- Name: dead_run_at_stops dead_run_at_stops_dead_runs_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_dead_runs_id_fkey FOREIGN KEY (dead_run_id) REFERENCES chouette_gui.dead_runs(id);


--
-- Name: dead_run_at_stops dead_run_at_stops_stop_point_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_stop_point_id_fkey FOREIGN KEY (stop_point_id) REFERENCES chouette_gui.stop_points(id);


--
-- Name: dead_runs dead_runs_journey_patterns_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.dead_runs
    ADD CONSTRAINT dead_runs_journey_patterns_id_fkey FOREIGN KEY (journey_pattern_id) REFERENCES chouette_gui.journey_patterns(id);


--
-- Name: journey_patterns departure_point_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns
    ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id) REFERENCES chouette_gui.stop_points(id) ON DELETE SET NULL;


--
-- Name: journey_pattern_sections fk_rails_0dbc726f14; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_pattern_sections
    ADD CONSTRAINT fk_rails_0dbc726f14 FOREIGN KEY (route_section_id) REFERENCES chouette_gui.route_sections(id);


--
-- Name: stop_points fk_rails_4955aab740; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_points
    ADD CONSTRAINT fk_rails_4955aab740 FOREIGN KEY (scheduled_stop_point_id) REFERENCES chouette_gui.scheduled_stop_points(id);


--
-- Name: journey_frequencies fk_rails_60bb6f7bd3; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_frequencies
    ADD CONSTRAINT fk_rails_60bb6f7bd3 FOREIGN KEY (timeband_id) REFERENCES chouette_gui.timebands(id);


--
-- Name: journey_pattern_sections fk_rails_73ae46b20f; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_pattern_sections
    ADD CONSTRAINT fk_rails_73ae46b20f FOREIGN KEY (journey_pattern_id) REFERENCES chouette_gui.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: routes_route_points fk_rails_74f8c8553c; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes_route_points
    ADD CONSTRAINT fk_rails_74f8c8553c FOREIGN KEY (route_id) REFERENCES chouette_gui.routes(id) ON DELETE CASCADE;


--
-- Name: routes_route_points fk_rails_be69ec7593; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes_route_points
    ADD CONSTRAINT fk_rails_be69ec7593 FOREIGN KEY (route_point_id) REFERENCES chouette_gui.route_points(id);


--
-- Name: route_points fk_rails_ceb6b1b896; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.route_points
    ADD CONSTRAINT fk_rails_ceb6b1b896 FOREIGN KEY (scheduled_stop_point_id) REFERENCES chouette_gui.scheduled_stop_points(id);


--
-- Name: journey_frequencies fk_rails_d322c5d659; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_frequencies
    ADD CONSTRAINT fk_rails_d322c5d659 FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id);


--
-- Name: flexible_service_properties flexible_props_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.flexible_service_properties
    ADD CONSTRAINT flexible_props_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id);


--
-- Name: footnote_alternative_texts footnotes_footnote_alternative_texts_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnote_alternative_texts
    ADD CONSTRAINT footnotes_footnote_alternative_texts_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id);


--
-- Name: footnotes_journey_patterns footnotes_journey_patterns_footnotes_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_journey_patterns
    ADD CONSTRAINT footnotes_journey_patterns_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_journey_patterns footnotes_journey_patterns_journey_patterns_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_journey_patterns
    ADD CONSTRAINT footnotes_journey_patterns_journey_patterns_fkey FOREIGN KEY (journey_pattern_id) REFERENCES chouette_gui.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: footnotes_lines footnotes_lines_footnotes_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_lines
    ADD CONSTRAINT footnotes_lines_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_lines footnotes_lines_lines_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_lines
    ADD CONSTRAINT footnotes_lines_lines_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;


--
-- Name: footnotes_stop_points footnotes_stop_points_footnotes_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_stop_points
    ADD CONSTRAINT footnotes_stop_points_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_stop_points footnotes_stop_points_stop_points_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_stop_points
    ADD CONSTRAINT footnotes_stop_points_stop_points_fkey FOREIGN KEY (stop_point_id) REFERENCES chouette_gui.stop_points(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journey_at_stops footnotes_vehicle_journey_at_stops_footnotes_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_vehicle_journey_at_stops
    ADD CONSTRAINT footnotes_vehicle_journey_at_stops_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journey_at_stops footnotes_vehicle_journey_at_stops_vjas_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_vehicle_journey_at_stops
    ADD CONSTRAINT footnotes_vehicle_journey_at_stops_vjas_fkey FOREIGN KEY (vehicle_journey_at_stop_id) REFERENCES chouette_gui.vehicle_journey_at_stops(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journeys footnotes_vehicle_journeys_footnotes_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_vehicle_journeys
    ADD CONSTRAINT footnotes_vehicle_journeys_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES chouette_gui.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journeys footnotes_vehicle_journeys_vehicle_journeys_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.footnotes_vehicle_journeys
    ADD CONSTRAINT footnotes_vehicle_journeys_vehicle_journeys_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: group_of_lines_lines groupofline_group_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.group_of_lines_lines
    ADD CONSTRAINT groupofline_group_fkey FOREIGN KEY (group_of_line_id) REFERENCES chouette_gui.group_of_lines(id) ON DELETE CASCADE;


--
-- Name: group_of_lines_lines groupofline_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.group_of_lines_lines
    ADD CONSTRAINT groupofline_line_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;


--
-- Name: journey_patterns jp_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns
    ADD CONSTRAINT jp_route_fkey FOREIGN KEY (route_id) REFERENCES chouette_gui.routes(id) ON DELETE CASCADE;


--
-- Name: journey_patterns_stop_points jpsp_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns_stop_points
    ADD CONSTRAINT jpsp_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES chouette_gui.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: journey_patterns_stop_points jpsp_stoppoint_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.journey_patterns_stop_points
    ADD CONSTRAINT jpsp_stoppoint_fkey FOREIGN KEY (stop_point_id) REFERENCES chouette_gui.stop_points(id) ON DELETE CASCADE;


--
-- Name: lines line_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.lines
    ADD CONSTRAINT line_company_fkey FOREIGN KEY (company_id) REFERENCES chouette_gui.companies(id) ON DELETE SET NULL;


--
-- Name: lines line_ptnetwork_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.lines
    ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (network_id) REFERENCES chouette_gui.networks(id) ON DELETE SET NULL;


--
-- Name: lines lines_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.lines
    ADD CONSTRAINT lines_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id);


--
-- Name: lines_key_values lines_key_values_lines_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.lines_key_values
    ADD CONSTRAINT lines_key_values_lines_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;


--
-- Name: networks network_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.networks
    ADD CONSTRAINT network_company_fkey FOREIGN KEY (company_id) REFERENCES chouette_gui.companies(id) ON DELETE SET NULL;


--
-- Name: routes route_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes
    ADD CONSTRAINT route_line_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;


--
-- Name: routes route_opposite_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routes
    ADD CONSTRAINT route_opposite_route_fkey FOREIGN KEY (opposite_route_id) REFERENCES chouette_gui.routes(id) ON DELETE SET NULL;


--
-- Name: routing_constraints_lines routingconstraint_line_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.routing_constraints_lines
    ADD CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (line_id) REFERENCES chouette_gui.lines(id) ON DELETE CASCADE;


--
-- Name: stop_points stop_point_destination_display_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_points
    ADD CONSTRAINT stop_point_destination_display_fkey FOREIGN KEY (destination_display_id) REFERENCES chouette_gui.destination_displays(id);


--
-- Name: stop_points stop_points_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_points
    ADD CONSTRAINT stop_points_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES chouette_gui.booking_arrangements(id);


--
-- Name: stop_areas_stop_areas stoparea_child_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (child_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_areas_stop_areas stoparea_parent_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parent_id) REFERENCES chouette_gui.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_points stoppoint_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.stop_points
    ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (route_id) REFERENCES chouette_gui.routes(id) ON DELETE CASCADE;


--
-- Name: time_tables_blocks time_tables_blocks_block_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_block_id_fkey FOREIGN KEY (block_id) REFERENCES chouette_gui.blocks(id);


--
-- Name: time_tables_blocks time_tables_blocks_time_table_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_time_table_id_fkey FOREIGN KEY (time_table_id) REFERENCES chouette_gui.time_tables(id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_dead_run_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_dead_run_id_fkey FOREIGN KEY (dead_run_id) REFERENCES chouette_gui.dead_runs(id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_time_table_id_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_time_table_id_fkey FOREIGN KEY (time_table_id) REFERENCES chouette_gui.time_tables(id);


--
-- Name: time_table_dates tm_date_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_table_dates
    ADD CONSTRAINT tm_date_fkey FOREIGN KEY (time_table_id) REFERENCES chouette_gui.time_tables(id) ON DELETE CASCADE;


--
-- Name: time_table_periods tm_period_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_table_periods
    ADD CONSTRAINT tm_period_fkey FOREIGN KEY (time_table_id) REFERENCES chouette_gui.time_tables(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vehicle_journeys_flexible_props_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_flexible_props_fkey FOREIGN KEY (flexible_service_properties_id) REFERENCES chouette_gui.flexible_service_properties(id);


--
-- Name: vehicle_journeys_key_values vehicle_journeys_key_values_vehicle_journey_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys_key_values
    ADD CONSTRAINT vehicle_journeys_key_values_vehicle_journey_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vj_company_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys
    ADD CONSTRAINT vj_company_fkey FOREIGN KEY (company_id) REFERENCES chouette_gui.companies(id) ON DELETE SET NULL;


--
-- Name: vehicle_journeys vj_jp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys
    ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES chouette_gui.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vj_route_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journeys
    ADD CONSTRAINT vj_route_fkey FOREIGN KEY (route_id) REFERENCES chouette_gui.routes(id) ON DELETE CASCADE;


--
-- Name: vehicle_journey_at_stops vjas_sp_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journey_at_stops
    ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stop_point_id) REFERENCES chouette_gui.stop_points(id) ON DELETE CASCADE;


--
-- Name: vehicle_journey_at_stops vjas_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.vehicle_journey_at_stops
    ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: time_tables_vehicle_journeys vjtm_tm_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_tm_fkey FOREIGN KEY (time_table_id) REFERENCES chouette_gui.time_tables(id) ON DELETE CASCADE;


--
-- Name: time_tables_vehicle_journeys vjtm_vj_fkey; Type: FK CONSTRAINT; Schema: chouette_gui; Owner: chouette
--

ALTER TABLE ONLY chouette_gui.time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES chouette_gui.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: access_points access_area_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_points
    ADD CONSTRAINT access_area_fkey FOREIGN KEY (stop_area_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: access_links aclk_acpt_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (access_point_id) REFERENCES public.access_points(id) ON DELETE CASCADE;


--
-- Name: access_links aclk_area_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.access_links
    ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stop_area_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_areas area_parent_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas
    ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parent_id) REFERENCES public.stop_areas(id) ON DELETE SET NULL;


--
-- Name: journey_patterns arrival_point_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns
    ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id) REFERENCES public.stop_points(id) ON DELETE SET NULL;


--
-- Name: blocks_dead_runs blocks_dead_runs_block_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_block_id_fkey FOREIGN KEY (block_id) REFERENCES public.blocks(id);


--
-- Name: blocks_dead_runs blocks_dead_runs_dead_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_dead_runs
    ADD CONSTRAINT blocks_dead_runs_dead_run_id_fkey FOREIGN KEY (dead_run_id) REFERENCES public.dead_runs(id);


--
-- Name: blocks blocks_scheduled_stop_points_end_point_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks
    ADD CONSTRAINT blocks_scheduled_stop_points_end_point_id_fkey FOREIGN KEY (end_point_id) REFERENCES public.scheduled_stop_points(id);


--
-- Name: blocks blocks_scheduled_stop_points_start_point_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks
    ADD CONSTRAINT blocks_scheduled_stop_points_start_point_id_fkey FOREIGN KEY (start_point_id) REFERENCES public.scheduled_stop_points(id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_block_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_block_id_fkey FOREIGN KEY (block_id) REFERENCES public.blocks(id);


--
-- Name: blocks_vehicle_journeys blocks_vehicle_journeys_vehicle_journey_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.blocks_vehicle_journeys
    ADD CONSTRAINT blocks_vehicle_journeys_vehicle_journey_id_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id);


--
-- Name: booking_arrangements booking_arrangement_booking_contact_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.booking_arrangements
    ADD CONSTRAINT booking_arrangement_booking_contact_fkey FOREIGN KEY (booking_contact_id) REFERENCES public.contact_structures(id);


--
-- Name: booking_arrangements_buy_when booking_arrangement_buy_when_lines_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.booking_arrangements_buy_when
    ADD CONSTRAINT booking_arrangement_buy_when_lines_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES public.booking_arrangements(id);


--
-- Name: booking_arrangements_booking_methods booking_arrangements_booking_methods_lines_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.booking_arrangements_booking_methods
    ADD CONSTRAINT booking_arrangements_booking_methods_lines_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES public.booking_arrangements(id);


--
-- Name: connection_links colk_endarea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.connection_links
    ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrival_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: connection_links colk_startarea_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.connection_links
    ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departure_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: dated_service_journey_refs dated_service_journey_refs_derived_dsj_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dated_service_journey_refs
    ADD CONSTRAINT dated_service_journey_refs_derived_dsj_id_fkey FOREIGN KEY (derived_dsj_id) REFERENCES public.dated_service_journeys(id);


--
-- Name: dated_service_journey_refs dated_service_journey_refs_original_dsj_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dated_service_journey_refs
    ADD CONSTRAINT dated_service_journey_refs_original_dsj_id_fkey FOREIGN KEY (original_dsj_id) REFERENCES public.dated_service_journeys(id);


--
-- Name: dated_service_journeys dated_service_journeys_vehicle_journey_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dated_service_journeys
    ADD CONSTRAINT dated_service_journeys_vehicle_journey_id_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id);


--
-- Name: dead_run_at_stops dead_run_at_stops_dead_runs_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_dead_runs_id_fkey FOREIGN KEY (dead_run_id) REFERENCES public.dead_runs(id);


--
-- Name: dead_run_at_stops dead_run_at_stops_stop_point_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_run_at_stops
    ADD CONSTRAINT dead_run_at_stops_stop_point_id_fkey FOREIGN KEY (stop_point_id) REFERENCES public.stop_points(id);


--
-- Name: dead_runs dead_runs_journey_patterns_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.dead_runs
    ADD CONSTRAINT dead_runs_journey_patterns_id_fkey FOREIGN KEY (journey_pattern_id) REFERENCES public.journey_patterns(id);


--
-- Name: journey_patterns departure_point_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns
    ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id) REFERENCES public.stop_points(id) ON DELETE SET NULL;


--
-- Name: journey_pattern_sections fk_rails_0dbc726f14; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_pattern_sections
    ADD CONSTRAINT fk_rails_0dbc726f14 FOREIGN KEY (route_section_id) REFERENCES public.route_sections(id);


--
-- Name: stop_points fk_rails_4955aab740; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points
    ADD CONSTRAINT fk_rails_4955aab740 FOREIGN KEY (scheduled_stop_point_id) REFERENCES public.scheduled_stop_points(id);


--
-- Name: journey_frequencies fk_rails_60bb6f7bd3; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_frequencies
    ADD CONSTRAINT fk_rails_60bb6f7bd3 FOREIGN KEY (timeband_id) REFERENCES public.timebands(id);


--
-- Name: journey_pattern_sections fk_rails_73ae46b20f; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_pattern_sections
    ADD CONSTRAINT fk_rails_73ae46b20f FOREIGN KEY (journey_pattern_id) REFERENCES public.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: routes_route_points fk_rails_74f8c8553c; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes_route_points
    ADD CONSTRAINT fk_rails_74f8c8553c FOREIGN KEY (route_id) REFERENCES public.routes(id) ON DELETE CASCADE;


--
-- Name: routes_route_points fk_rails_be69ec7593; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes_route_points
    ADD CONSTRAINT fk_rails_be69ec7593 FOREIGN KEY (route_point_id) REFERENCES public.route_points(id);


--
-- Name: route_points fk_rails_ceb6b1b896; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.route_points
    ADD CONSTRAINT fk_rails_ceb6b1b896 FOREIGN KEY (scheduled_stop_point_id) REFERENCES public.scheduled_stop_points(id);


--
-- Name: journey_frequencies fk_rails_d322c5d659; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_frequencies
    ADD CONSTRAINT fk_rails_d322c5d659 FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id);


--
-- Name: flexible_service_properties flexible_props_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.flexible_service_properties
    ADD CONSTRAINT flexible_props_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES public.booking_arrangements(id);


--
-- Name: footnote_alternative_texts footnotes_footnote_alternative_texts_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnote_alternative_texts
    ADD CONSTRAINT footnotes_footnote_alternative_texts_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id);


--
-- Name: footnotes_journey_patterns footnotes_journey_patterns_footnotes_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_journey_patterns
    ADD CONSTRAINT footnotes_journey_patterns_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_journey_patterns footnotes_journey_patterns_journey_patterns_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_journey_patterns
    ADD CONSTRAINT footnotes_journey_patterns_journey_patterns_fkey FOREIGN KEY (journey_pattern_id) REFERENCES public.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: footnotes_lines footnotes_lines_footnotes_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_lines
    ADD CONSTRAINT footnotes_lines_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_lines footnotes_lines_lines_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_lines
    ADD CONSTRAINT footnotes_lines_lines_fkey FOREIGN KEY (line_id) REFERENCES public.lines(id) ON DELETE CASCADE;


--
-- Name: footnotes_stop_points footnotes_stop_points_footnotes_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_stop_points
    ADD CONSTRAINT footnotes_stop_points_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_stop_points footnotes_stop_points_stop_points_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_stop_points
    ADD CONSTRAINT footnotes_stop_points_stop_points_fkey FOREIGN KEY (stop_point_id) REFERENCES public.stop_points(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journey_at_stops footnotes_vehicle_journey_at_stops_footnotes_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_vehicle_journey_at_stops
    ADD CONSTRAINT footnotes_vehicle_journey_at_stops_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journey_at_stops footnotes_vehicle_journey_at_stops_vjas_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_vehicle_journey_at_stops
    ADD CONSTRAINT footnotes_vehicle_journey_at_stops_vjas_fkey FOREIGN KEY (vehicle_journey_at_stop_id) REFERENCES public.vehicle_journey_at_stops(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journeys footnotes_vehicle_journeys_footnotes_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_vehicle_journeys
    ADD CONSTRAINT footnotes_vehicle_journeys_footnotes_fkey FOREIGN KEY (footnote_id) REFERENCES public.footnotes(id) ON DELETE CASCADE;


--
-- Name: footnotes_vehicle_journeys footnotes_vehicle_journeys_vehicle_journeys_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.footnotes_vehicle_journeys
    ADD CONSTRAINT footnotes_vehicle_journeys_vehicle_journeys_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: group_of_lines_lines groupofline_group_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.group_of_lines_lines
    ADD CONSTRAINT groupofline_group_fkey FOREIGN KEY (group_of_line_id) REFERENCES public.group_of_lines(id) ON DELETE CASCADE;


--
-- Name: group_of_lines_lines groupofline_line_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.group_of_lines_lines
    ADD CONSTRAINT groupofline_line_fkey FOREIGN KEY (line_id) REFERENCES public.lines(id) ON DELETE CASCADE;


--
-- Name: journey_patterns jp_route_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns
    ADD CONSTRAINT jp_route_fkey FOREIGN KEY (route_id) REFERENCES public.routes(id) ON DELETE CASCADE;


--
-- Name: journey_patterns_stop_points jpsp_jp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns_stop_points
    ADD CONSTRAINT jpsp_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES public.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: journey_patterns_stop_points jpsp_stoppoint_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.journey_patterns_stop_points
    ADD CONSTRAINT jpsp_stoppoint_fkey FOREIGN KEY (stop_point_id) REFERENCES public.stop_points(id) ON DELETE CASCADE;


--
-- Name: lines line_company_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines
    ADD CONSTRAINT line_company_fkey FOREIGN KEY (company_id) REFERENCES public.companies(id) ON DELETE SET NULL;


--
-- Name: lines line_ptnetwork_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines
    ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (network_id) REFERENCES public.networks(id) ON DELETE SET NULL;


--
-- Name: lines lines_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines
    ADD CONSTRAINT lines_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES public.booking_arrangements(id);


--
-- Name: lines_key_values lines_key_values_lines_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.lines_key_values
    ADD CONSTRAINT lines_key_values_lines_fkey FOREIGN KEY (line_id) REFERENCES public.lines(id) ON DELETE CASCADE;


--
-- Name: networks network_company_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.networks
    ADD CONSTRAINT network_company_fkey FOREIGN KEY (company_id) REFERENCES public.companies(id) ON DELETE SET NULL;


--
-- Name: routes route_line_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT route_line_fkey FOREIGN KEY (line_id) REFERENCES public.lines(id) ON DELETE CASCADE;


--
-- Name: routes route_opposite_route_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT route_opposite_route_fkey FOREIGN KEY (opposite_route_id) REFERENCES public.routes(id) ON DELETE SET NULL;


--
-- Name: routing_constraints_lines routingconstraint_line_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.routing_constraints_lines
    ADD CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (line_id) REFERENCES public.lines(id) ON DELETE CASCADE;


--
-- Name: stop_points stop_point_destination_display_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points
    ADD CONSTRAINT stop_point_destination_display_fkey FOREIGN KEY (destination_display_id) REFERENCES public.destination_displays(id);


--
-- Name: stop_points stop_points_booking_arrangement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points
    ADD CONSTRAINT stop_points_booking_arrangement_fkey FOREIGN KEY (booking_arrangement_id) REFERENCES public.booking_arrangements(id);


--
-- Name: stop_areas_stop_areas stoparea_child_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (child_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_areas_stop_areas stoparea_parent_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_areas_stop_areas
    ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parent_id) REFERENCES public.stop_areas(id) ON DELETE CASCADE;


--
-- Name: stop_points stoppoint_route_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.stop_points
    ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (route_id) REFERENCES public.routes(id) ON DELETE CASCADE;


--
-- Name: time_tables_blocks time_tables_blocks_block_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_block_id_fkey FOREIGN KEY (block_id) REFERENCES public.blocks(id);


--
-- Name: time_tables_blocks time_tables_blocks_time_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_blocks
    ADD CONSTRAINT time_tables_blocks_time_table_id_fkey FOREIGN KEY (time_table_id) REFERENCES public.time_tables(id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_dead_run_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_dead_run_id_fkey FOREIGN KEY (dead_run_id) REFERENCES public.dead_runs(id);


--
-- Name: time_tables_dead_runs time_tables_dead_runs_time_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_dead_runs
    ADD CONSTRAINT time_tables_dead_runs_time_table_id_fkey FOREIGN KEY (time_table_id) REFERENCES public.time_tables(id);


--
-- Name: time_table_dates tm_date_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_dates
    ADD CONSTRAINT tm_date_fkey FOREIGN KEY (time_table_id) REFERENCES public.time_tables(id) ON DELETE CASCADE;


--
-- Name: time_table_periods tm_period_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_table_periods
    ADD CONSTRAINT tm_period_fkey FOREIGN KEY (time_table_id) REFERENCES public.time_tables(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vehicle_journeys_flexible_props_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys
    ADD CONSTRAINT vehicle_journeys_flexible_props_fkey FOREIGN KEY (flexible_service_properties_id) REFERENCES public.flexible_service_properties(id);


--
-- Name: vehicle_journeys_key_values vehicle_journeys_key_values_vehicle_journey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys_key_values
    ADD CONSTRAINT vehicle_journeys_key_values_vehicle_journey_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vj_company_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys
    ADD CONSTRAINT vj_company_fkey FOREIGN KEY (company_id) REFERENCES public.companies(id) ON DELETE SET NULL;


--
-- Name: vehicle_journeys vj_jp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys
    ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journey_pattern_id) REFERENCES public.journey_patterns(id) ON DELETE CASCADE;


--
-- Name: vehicle_journeys vj_route_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journeys
    ADD CONSTRAINT vj_route_fkey FOREIGN KEY (route_id) REFERENCES public.routes(id) ON DELETE CASCADE;


--
-- Name: vehicle_journey_at_stops vjas_sp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journey_at_stops
    ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stop_point_id) REFERENCES public.stop_points(id) ON DELETE CASCADE;


--
-- Name: vehicle_journey_at_stops vjas_vj_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.vehicle_journey_at_stops
    ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: time_tables_vehicle_journeys vjtm_tm_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_tm_fkey FOREIGN KEY (time_table_id) REFERENCES public.time_tables(id) ON DELETE CASCADE;


--
-- Name: time_tables_vehicle_journeys vjtm_vj_fkey; Type: FK CONSTRAINT; Schema: public; Owner: chouette
--

ALTER TABLE ONLY public.time_tables_vehicle_journeys
    ADD CONSTRAINT vjtm_vj_fkey FOREIGN KEY (vehicle_journey_id) REFERENCES public.vehicle_journeys(id) ON DELETE CASCADE;


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: cloudsqlsuperuser
--




--
-- PostgreSQL database dump complete
--

-- SCRIPT CUSTOMIZATION ENTUR
insert into public.referentials (id,name,slug) values (1,'Test referential','chouette_gui');
insert into public.referential_last_update(last_update_timestamp) values(current_timestamp);
insert into chouette_gui.referential_last_update(last_update_timestamp) values(current_timestamp);
-- END SCRIPT CUSTOMIZATION ENTUR



