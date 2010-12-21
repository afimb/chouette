package unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Time;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;

import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.Horaire;

public class TestGenerationHoraires
{
	/**
	 * Logger for this class
	 */
	private static final Logger	logger	= Logger.getLogger(ImportFormatInterneTest.class);

	private ITemplateDao<Horaire> horaireDao;

	@Configuration(beforeTestClass = true)
	public void initialisation()
	{
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();

		horaireDao = (ITemplateDao<Horaire>)applicationContext.getBean( "horaireDao");
	}

	@Test(groups = "tests unitaires", description = "generation d'horaires")
	public void generationHoraires()
	{
		Horaire horaire = new Horaire();
		Long arret1 = 53L;
		Long arret2 = 55L;
		Long arret3 = 57L;
		Long arret4 = 59L;
		Long course1 = 64L;
		Long course2 = 180L;
		Long course3 = 233L;
		
		Time time = new Time(6666L);

		Long[] tabArret = new Long[] { arret1, arret2, arret3, arret4 };
		Long[] tabCourse = new Long[] { course1, course2, course3 };

		List<Long> idHoraires = new ArrayList<Long>();
		for (int i = 0; i < tabCourse.length; i++)
			{
				horaire.setIdCourse(tabCourse[i]);
				for (int j = 0; j < tabArret.length; j++)
					{
						horaire.setIdArret(tabArret[j]);
						horaire.setDepartureTime(time.toDate());
						horaire.setArrivalTime(time.toDate());
						horaireDao.save(horaire);
						
						idHoraires.add( horaire.getId());

					}
			}
		
		for (Long idHoraire : idHoraires) {
			horaireDao.remove(idHoraire);
		}

	}
}
