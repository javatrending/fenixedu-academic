package ServidorAplicacao.Servico.publico;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DataBeans.ExecutionCourseSiteView;
import DataBeans.ISiteComponent;
import DataBeans.InfoEvaluation;
import DataBeans.InfoMark;
import DataBeans.InfoSiteCommon;
import DataBeans.InfoSiteMarks;
import DataBeans.util.Cloner;
import Dominio.Evaluation;
import Dominio.IEvaluation;
import Dominio.IMark;
import Dominio.ISite;
import Dominio.Site;
import ServidorAplicacao.IServico;
import ServidorAplicacao.Factory.ExecutionCourseSiteComponentBuilder;
import ServidorAplicacao.Servico.ExcepcaoInexistente;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IPersistentEvaluation;
import ServidorPersistente.IPersistentMark;
import ServidorPersistente.IPersistentSite;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

/**
 * @author Fernanda Quit�rio
 *
 */
public class ReadPublishedMarksByExam implements IServico {
	private static ReadPublishedMarksByExam _servico = new ReadPublishedMarksByExam();

	/**
		* The actor of this class.
		**/
	private ReadPublishedMarksByExam() {

	}

	/**
	 * Returns Service Name
	 */
	public String getNome() {
		return "ReadPublishedMarksByExam";
	}

	/**
	 * Returns the _servico.
	 * @return ReadPublishedMarksByExam
	 */
	public static ReadPublishedMarksByExam getService() {
		return _servico;
	}

	public Object run(Integer siteCode, Integer evaluationCode) throws ExcepcaoInexistente, FenixServiceException {
		List marksList = null;
		List infoMarksList = null;

		ISite site = null;
		IEvaluation evaluation = null;
		InfoEvaluation infoEvaluation = null;

		try {
			ISuportePersistente sp = SuportePersistenteOJB.getInstance();

			//Site
			site = new Site(siteCode);
			IPersistentSite siteDAO = sp.getIPersistentSite();
			site = (ISite) siteDAO.readByOId(site, false);

			// Evaluation
			evaluation = new Evaluation();
			evaluation.setIdInternal(evaluationCode);
			IPersistentEvaluation persistentEvaluation = sp.getIPersistentEvaluation();
			evaluation = (IEvaluation)persistentEvaluation.readByOId(evaluation, false);
			infoEvaluation = Cloner.copyIEvaluation2InfoEvaluation(evaluation);			
			
			//Marks
			IPersistentMark markDAO = sp.getIPersistentMark();
			marksList = markDAO.readBy(evaluation);

			infoMarksList = new ArrayList();
			Iterator iterator = marksList.listIterator();
			IMark mark = null;
			InfoMark infoMark = null;
			while (iterator.hasNext()) {
				mark = (IMark) iterator.next();

				infoMark = Cloner.copyIMark2InfoMark(mark);
			
				infoMarksList.add(infoMark);
			}			
		} catch (ExcepcaoPersistencia e) {
			e.printStackTrace();
			throw new FenixServiceException("error.impossibleReadMarksList");
		}

		InfoSiteMarks infoSiteMarks = new InfoSiteMarks();
		infoSiteMarks.setMarksList(infoMarksList);
		infoSiteMarks.setInfoEvaluation(infoEvaluation);
		ExecutionCourseSiteComponentBuilder componentBuilder = new ExecutionCourseSiteComponentBuilder();
		ISiteComponent commonComponent = componentBuilder.getComponent(new InfoSiteCommon(), site, null, null);

		ExecutionCourseSiteView siteView = new ExecutionCourseSiteView(commonComponent, infoSiteMarks);

		return siteView;
	}
}