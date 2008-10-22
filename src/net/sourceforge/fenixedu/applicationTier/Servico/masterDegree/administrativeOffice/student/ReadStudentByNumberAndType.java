/*
 * LerAula.java
 *
 * Created on December 16th, 2002, 1:58
 */

package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.student;

/**
 * Servi�o LerAluno.
 * 
 * @author tfc130
 */

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.dataTransferObject.InfoStudent;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import net.sourceforge.fenixedu.domain.student.Registration;

public class ReadStudentByNumberAndType extends FenixService {

    // FIXME: We have to read the student by type also !!

    public Object run(Integer number, DegreeType degreeType) {

	InfoStudent infoStudent = null;

	////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////
	// Isto n�o � para ficar assim. Est� assim temporariamente at� se
	// saber como � feita de facto a distin��o
	// dos aluno, referente ao tipo, a partir da p�gina de login.
	////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////
	Registration registration = Registration.readStudentByNumberAndDegreeType(number, degreeType);

	if (registration != null) {
	    infoStudent = InfoStudent.newInfoFromDomain(registration);
	}

	return infoStudent;
    }

}