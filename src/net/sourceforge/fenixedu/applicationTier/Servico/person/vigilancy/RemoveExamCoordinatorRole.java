package net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.person.RoleType;
import pt.ist.fenixWebFramework.services.Service;

public class RemoveExamCoordinatorRole extends FenixService {

    @Service
    public static void run(Person person) {

	person.removeRoleByType(RoleType.EXAM_COORDINATOR);

    }

}