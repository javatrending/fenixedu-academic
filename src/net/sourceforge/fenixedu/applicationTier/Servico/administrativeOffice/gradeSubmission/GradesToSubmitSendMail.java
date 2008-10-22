package net.sourceforge.fenixedu.applicationTier.Servico.administrativeOffice.gradeSubmission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.util.Email;
import pt.ist.fenixWebFramework.services.Service;

public class GradesToSubmitSendMail extends FenixService {

    @Service
    public static void run(Collection<ExecutionCourse> executionCourses, String from, String cc, String subject, String message) {
	List<String> mails = new ArrayList<String>();
	for (ExecutionCourse executionCourse : executionCourses) {
	    for (Professorship professorship : executionCourse.getProfessorships()) {
		if (professorship.getResponsibleFor()) {
		    if (professorship.getTeacher().getPerson().getEmail() != null) {
			mails.add(professorship.getTeacher().getPerson().getEmail());
		    }
		}
	    }

	}

	String[] tokens = cc.split(",");
	List<String> ccs = Arrays.asList(tokens);

	new Email(null, from, null, mails, ccs, null, subject, message);
    }

}