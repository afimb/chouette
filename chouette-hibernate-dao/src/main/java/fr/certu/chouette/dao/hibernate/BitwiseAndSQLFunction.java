package fr.certu.chouette.dao.hibernate;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class BitwiseAndSQLFunction extends StandardSQLFunction implements SQLFunction {

	public BitwiseAndSQLFunction(String name) {
		super(name);
	}

	public BitwiseAndSQLFunction(String name, Type typeValue) {
		super(name, typeValue);
	} 

	// CETTE FONCTION SE CHARGE DE TRADUIRE L'EXPRESSION SAISIE DANS LA REQUETE HQL
	// EN UNE EXPRESSION EQUIVALENTE DANS LE LANGAGE SQL
	
	// EX. / bitwise_and(tableauMarche.intDayTypes, 128) = 128
	// SERA TRADUITE EN / tableauMarche.intDayTypes & 128 DANS LA REQUETE SQL GENEREE PAR HIBERNATE
	@SuppressWarnings("rawtypes")
	public String render(List args, SessionFactoryImplementor sessionFactory) throws QueryException {
       if (args.size() != 2){
           throw new IllegalArgumentException("LA FONCTION PREND OBLIGATOIREMENT 2 PARAMETRES :(");
       }
       StringBuffer buffer = new StringBuffer(args.get(0).toString());
       buffer.append(" & ").append(args.get(1));
       return buffer.toString(); 
	}
}
