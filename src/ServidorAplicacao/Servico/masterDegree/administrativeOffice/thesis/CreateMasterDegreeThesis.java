package ServidorAplicacao.Servico.masterDegree.administrativeOffice.thesis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataBeans.InfoStudentCurricularPlan;
import DataBeans.util.Cloner;
import Dominio.IEmployee;
import Dominio.IMasterDegreeThesis;
import Dominio.IMasterDegreeThesisDataVersion;
import Dominio.IPessoa;
import Dominio.IStudentCurricularPlan;
import Dominio.MasterDegreeThesis;
import Dominio.MasterDegreeThesisDataVersion;
import ServidorAplicacao.IServico;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;
import Util.State;

/**
 * 
 * @author
 *   - Shezad Anavarali (sana@mega.ist.utl.pt)
 *   - Nadir Tarmahomed (naat@mega.ist.utl.pt)
 *
 */
public class CreateMasterDegreeThesis implements IServico {

	private static CreateMasterDegreeThesis servico = new CreateMasterDegreeThesis();

	/**
	 * The singleton access method of this class.
	 **/
	public static CreateMasterDegreeThesis getService() {
		return servico;
	}

	/**
	 * The actor of this class.
	 **/
	private CreateMasterDegreeThesis() {
	}

	/**
	 * Returns The Service Name */
	public final String getNome() {
		return "CreateMasterDegreeThesis";
	}

	public void run(
		IUserView userView,
		InfoStudentCurricularPlan infoStudentCurricularPlan,
		String dissertationTitle,
		ArrayList infoTeacherGuiders,
		ArrayList infoTeacherAssistentGuiders,
		ArrayList infoExternalPersonExternalAssistentGuiders)
		throws FenixServiceException {

		try {
			ISuportePersistente sp = SuportePersistenteOJB.getInstance();
			IStudentCurricularPlan studentCurricularPlan = Cloner.copyInfoStudentCurricularPlan2IStudentCurricularPlan(infoStudentCurricularPlan);
			
			IMasterDegreeThesis storedMasterDegreeThesis = sp.getIPersistentMasterDegreeThesis().readByStudentCurricularPlan(studentCurricularPlan);
			if (storedMasterDegreeThesis != null)
				throw new ExistingServiceException("message.masterDegree.existingMasterDegreeThesis");
			
			IMasterDegreeThesisDataVersion storedMasterDegreeThesisDataVersion = sp.getIPersistentMasterDegreeThesisDataVersion().readActiveByDissertationTitle(dissertationTitle);
			if (storedMasterDegreeThesisDataVersion != null)
				if (!storedMasterDegreeThesisDataVersion.getMasterDegreeThesis().getStudentCurricularPlan().equals(studentCurricularPlan))
					throw new ExistingServiceException("message.masterDegree.dissertationTitleAlreadyChosen");

			IPessoa person = sp.getIPessoaPersistente().lerPessoaPorUsername(userView.getUtilizador());
			IEmployee employee = sp.getIPersistentEmployee().readByPerson(person.getIdInternal().intValue());

			IMasterDegreeThesis masterDegreeThesis = new MasterDegreeThesis(studentCurricularPlan);
			sp.getIPersistentMasterDegreeThesis().simpleLockWrite(masterDegreeThesis);

			//write data version
			IMasterDegreeThesisDataVersion masterDegreeThesisDataVersion =
				new MasterDegreeThesisDataVersion(
					masterDegreeThesis,
					employee,
					dissertationTitle,
					new Timestamp(new Date().getTime()),
					new State(State.ACTIVE));
			List guiders = Cloner.copyListInfoTeacher2ListITeacher(infoTeacherGuiders);
			List assistentGuiders = Cloner.copyListInfoTeacher2ListITeacher(infoTeacherAssistentGuiders);
			List externalAssistentGuiders = Cloner.copyListInfoExternalPerson2ListIExternalPerson(infoExternalPersonExternalAssistentGuiders);
			masterDegreeThesisDataVersion.setGuiders(guiders);
			masterDegreeThesisDataVersion.setAssistentGuiders(assistentGuiders);
			masterDegreeThesisDataVersion.setExternalAssistentGuiders(externalAssistentGuiders);
			sp.getIPersistentMasterDegreeThesisDataVersion().simpleLockWrite(masterDegreeThesisDataVersion);

			//Timestamp actualTimestamp = new Timestamp(new Date().getTime());
			//IMasterDegreeProofVersion masterDegreeProofVersion = new MasterDegreeProofVersion(masterDegreeThesis, employee,
			//		actualTimestamp,actualTimestamp,actualTimestamp, MasterDegreeClassification.NOT_APPROVED, new Integer(5), new State(State.ACTIVE));
			//sp.getIPersistentMasterDegreeProofVersion().simpleLockWrite(masterDegreeProofVersion);

		} catch (ExcepcaoPersistencia ex) {
			FenixServiceException newEx = new FenixServiceException("Persistence layer error");
			newEx.fillInStackTrace();
			throw newEx;
		}

	}
}