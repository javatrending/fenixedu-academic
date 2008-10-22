package net.sourceforge.fenixedu.applicationTier.Servico.research.project;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.research.project.ProjectParticipation;

public class DeleteProjectParticipant extends FenixService {

    public void run(Integer participationId) throws FenixServiceException {
	ProjectParticipation participation = rootDomainObject.readProjectParticipationByOID(participationId);
	if (participation == null) {
	    throw new FenixServiceException();
	}
	participation.delete();
    }
}
