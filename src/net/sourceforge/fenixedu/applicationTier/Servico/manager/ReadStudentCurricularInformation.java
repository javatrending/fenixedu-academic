/*
 * Created on Feb 18, 2005
 *
 */
package net.sourceforge.fenixedu.applicationTier.Servico.manager;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.dataTransferObject.InfoStudentCurricularPlan;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.student.Registration;

/**
 * @author Luis Cruz
 * 
 */
public class ReadStudentCurricularInformation extends FenixService {

    public List run(final Integer studentNumber, final DegreeType degreeType) {

	final List<InfoStudentCurricularPlan> infoStudentCurricularPlans = new ArrayList<InfoStudentCurricularPlan>();

	for (Registration registration : Registration.readByNumberAndDegreeType(studentNumber, degreeType)) {
	    for (StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlans()) {
		infoStudentCurricularPlans.add(constructInfoStudentCurricularPlan(studentCurricularPlan));
	    }
	}

	return infoStudentCurricularPlans;
    }

    protected InfoStudentCurricularPlan constructInfoStudentCurricularPlan(final StudentCurricularPlan studentCurricularPlan) {
	return InfoStudentCurricularPlan.newInfoFromDomain(studentCurricularPlan);
    }

}
