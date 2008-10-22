package net.sourceforge.fenixedu.applicationTier.Servico.person;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.domain.contents.Container;
import net.sourceforge.fenixedu.domain.contents.Content;
import pt.ist.fenixWebFramework.services.Service;

public class RemoveContentFromContainer extends FenixService {

    @Service
    public static void run(Container container, Content content) {
	content.getParentNode(container).delete();
    }

}