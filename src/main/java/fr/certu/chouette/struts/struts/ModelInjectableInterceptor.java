package fr.certu.chouette.struts.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import fr.certu.chouette.struts.util.Struts2Utils;
import fr.certu.chouette.util.ClassUtils;

public class ModelInjectableInterceptor extends AbstractInterceptor  {

	// WARNING / LES INTERCEPTEURS DOIVENT ETRE THREAD SAFE CAR ILS SONT PARTAGES ENTRE REQUETES
	
	private static final Log log = LogFactory.getLog(ModelInjectableInterceptor.class);
	
	public String intercept (ActionInvocation invocation) throws Exception {

		Object action = invocation.getAction();

		if (action instanceof ModelInjectable) {
			
			try {
				
				String modelClassFullName = Struts2Utils.getModelClassFullNameFromConfig(invocation.getProxy().getConfig());
        		
        		Object model = ClassUtils.getClassInstanceFromFullName(modelClassFullName);
        		
        		boolean modelIsNotPreparable = !(model instanceof PreparableModel);
            	if (modelIsNotPreparable) {
            		log.debug ("LE MODEL DE DONNEES AYANT COMME CLASSE / " + modelClassFullName + " ET CORRESPONDANT A L'ACTION DE L'URL / " + ServletActionContext.getRequest().getRequestURL() + " DEMANDEE N'EST PAS PREPARABLE (=> S'IL CONTIENT UNE METHODE PREPARE, ELLE NE SERA PAS APPELLEE DANS LE PREPARE DE L'ACTION SI CELA ETAIT PREVU, SINON IGNORER CE MESSAGE ..)");
            	}
            	
            	((ModelInjectable)action).setModel(model);            	
        		
        	} catch (Exception e) {
        		
        		log.error(e + " CAUSE / " + e.getCause());
        		
        	}
        }
        
        return invocation.invoke();
	}
}
