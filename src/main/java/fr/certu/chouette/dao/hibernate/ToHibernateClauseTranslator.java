package fr.certu.chouette.dao.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

import fr.certu.chouette.critere.AbstractOperatorClause;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IBinaryClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.IUnaryClause;
import fr.certu.chouette.critere.NotClause;
import fr.certu.chouette.critere.OrClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.dao.IClauseTranslator;

public class ToHibernateClauseTranslator implements IClauseTranslator {

	protected static final Logger logger = Logger.getLogger(ToHibernateClauseTranslator.class);
	
	public Object translate (IClause clause) {
		
		if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");
		
		if (clause instanceof AbstractOperatorClause) {
			
			AbstractOperatorClause terminalClause = (AbstractOperatorClause)clause;
			String name = terminalClause.getParameterName();
			Object allTuples = allTuplesRestriction(terminalClause);
			
			if (terminalClause instanceof ScalarClause) {
				
				ScalarClause terminalScalarClause = (ScalarClause)terminalClause;
				
				if (terminalScalarClause.getValue() == null) return allTuples;
				
				switch (terminalScalarClause.getOperator()) {
					case	Like	:	return Restrictions.like (name, getILikeOrLikeRestrictionValue(terminalScalarClause));
					case	Ilike	: 	return Restrictions.ilike (name, getILikeOrLikeRestrictionValue(terminalScalarClause));
					case	Equals	: 	return Restrictions.eq (name, terminalScalarClause.getValue());
				}
				
			} else if (terminalClause instanceof VectorClause) {
				
				VectorClause terminalVectorOperator = (VectorClause)terminalClause;
				
				if (terminalVectorOperator.getValues() == null) return allTuples;

				switch (terminalVectorOperator.getOperator()) {
					case	In	:	return Restrictions.in (name, terminalVectorOperator.getValues());
				}
			}
		} 
		
		if (clause instanceof IBinaryClause) {
			
			IBinaryClause binaryClause = (IBinaryClause)clause;
			Criterion leftCriterion = (Criterion)translate(binaryClause.getLeftSubClause());
			Criterion rightCriterion = (Criterion)translate(binaryClause.getRightSubClause());
			if (binaryClause instanceof OrClause) return Restrictions.or (leftCriterion, rightCriterion);
			if (binaryClause instanceof AndClause) return Restrictions.and (leftCriterion, rightCriterion);			
		} 
		
		if (clause instanceof IUnaryClause) {
			
			IUnaryClause unaryClause = ( IUnaryClause)clause;
			if (unaryClause instanceof NotClause) return Restrictions.not((Criterion)translate(unaryClause.getClause()));
		}
		
		throw new UnsupportedOperationException("CLAUSE RENCONTREE NON PRISE EN CHARGE PAR LE TRADUCTEUR HIBERNATE DE CLAUSES / " + clause.getClass());
	}

	private String getILikeOrLikeRestrictionValue(ScalarClause clause) {
		return "%" + clause.getValue() + "%";
	}

	/*
	 * RETOURNE UNE RESTRICTION QUI PREND EN COMPTE
	 * TOUTES LES LIGNES DE LA COLONNE CONCERNEE PAR LA CLAUSE
	 * SANS AUCUNE CONDITION APPLIQUEE
	 */
	private LogicalExpression allTuplesRestriction(AbstractOperatorClause clause) {
		return Restrictions.or (
				Restrictions.isNotNull(clause.getParameterName()),
				Restrictions.isNull(clause.getParameterName()));
	}
}
