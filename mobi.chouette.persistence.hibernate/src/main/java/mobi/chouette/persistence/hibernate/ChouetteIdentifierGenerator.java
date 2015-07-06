package mobi.chouette.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.log4j.Log4j;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;

@Log4j
public class ChouetteIdentifierGenerator implements IdentifierGenerator,
		Configurable {

	public static final String SCHEMA = "schema";
	public static final String CATALOG = "catalog";
	public static final String IDENTIFIER_NORMALIZER = "identifier_normalizer";
	public static final String SEQUENCE_PARAM = "sequence_name";
	public static final String DEF_SEQUENCE_NAME = "hibernate_sequence";
	public static final String INCREMENT_PARAM = "increment_size";
	public static final int DEFAULT_INCREMENT_SIZE = 100;

	private String sql;
	private String sequenceName;
	private Type identifierType;
	private int incrementSize;
	
	private static List<ChouetteIdentifierGenerator> instances = new ArrayList<>();
	
	private Map<String,State> states = new ConcurrentHashMap<>();

	
	public static void deleteTenant(String tenantIdentifier)
	{
		for (ChouetteIdentifierGenerator instance : instances) {
			instance.states.remove(tenantIdentifier);
		}
	}
	
	public ChouetteIdentifierGenerator()
	{
		instances.add(this);
	}
	
	@Override
	public void configure(Type type, Properties params, Dialect dialect)
			throws MappingException {
		this.identifierType = type;
		this.sequenceName = determineSequenceName(params, dialect);
		this.incrementSize = determineIncrementSize(params);
		this.sql = getSequenceNextValString(sequenceName, incrementSize);	
		log.info("----------------configure sequence "+sequenceName+" ------------") ;
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {

		State state = states.get(session.getTenantIdentifier());
		
		if (state == null)
		{
			state = new State();
			states.put(session.getTenantIdentifier(), state);
		}
		
		if (state.hiValue == null || state.value == null || state.hiValue.lt(state.value) ) {
			state.hiValue = getNextValue(session);
			state.value = state.hiValue.copy().subtract(incrementSize);
			// System.out.println("[DSU] ? nextval --------------> : " + value);
		}
		Number result = state.value.makeValueThenIncrement();
		// System.out.println("[DSU] nextval --------------> : " + value);
		return result;
	}

	protected IntegralDataTypeHolder getNextValue(SessionImplementor session) {
		try {

			//System.out.println("ChouetteIdentifierGenerator.getNextValue() : " + sql);
			PreparedStatement st = session.getTransactionCoordinator()
					.getJdbcCoordinator().getStatementPreparer()
					.prepareStatement(sql);
			try {
				ResultSet rs = session.getTransactionCoordinator()
						.getJdbcCoordinator().getResultSetReturn().extract(st);
				try {
					rs.next();
					IntegralDataTypeHolder result = buildHolder();
					result.initialize(rs, 1);
					return result;
				} finally {
					session.getTransactionCoordinator().getJdbcCoordinator()
							.release(rs, st);
				}
			} finally {
				session.getTransactionCoordinator().getJdbcCoordinator()
						.release(st);
			}

		} catch (SQLException sqle) {
			throw session.getFactory().getSQLExceptionHelper()
					.convert(sqle, "could not get next sequence value", sql);
		}

	}

	protected String getSequenceNextValString(String sequenceName,
			int incrementSize) {
		return "select setval('" + sequenceName + "', nextval('"
				+ sequenceName + "') + " + incrementSize + ")";
	}

	protected IntegralDataTypeHolder buildHolder() {
		return IdentifierGeneratorHelper
				.getIntegralDataTypeHolder(identifierType.getReturnedClass());
	}

	protected String determineSequenceName(Properties params, Dialect dialect) {
		ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params
				.get(IDENTIFIER_NORMALIZER);
		String sequenceName = ConfigurationHelper.getString(SEQUENCE_PARAM,
				params, DEF_SEQUENCE_NAME);
		if (sequenceName.indexOf('.') < 0) {
			sequenceName = normalizer.normalizeIdentifierQuoting(sequenceName);
			String schemaName = params.getProperty(SCHEMA);
			String catalogName = params.getProperty(CATALOG);
			sequenceName = Table.qualify(dialect.quote(catalogName),
					dialect.quote(schemaName), dialect.quote(sequenceName));
		}
		return sequenceName;
	}

	protected int determineIncrementSize(Properties params) {
		return ConfigurationHelper.getInt(INCREMENT_PARAM, params,
				DEFAULT_INCREMENT_SIZE);
	}
	
	private class State
	{
		IntegralDataTypeHolder hiValue ;
		 IntegralDataTypeHolder value ;
	}
}