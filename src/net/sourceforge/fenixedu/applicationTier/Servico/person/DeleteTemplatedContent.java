package net.sourceforge.fenixedu.applicationTier.Servico.person;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.domain.contents.Content;
import net.sourceforge.fenixedu.domain.contents.MetaDomainObjectPortal;
import net.sourceforge.fenixedu.domain.contents.Node;
import pt.ist.fenixWebFramework.services.Service;

public class DeleteTemplatedContent extends FenixService {

    @Service
    public static void run(MetaDomainObjectPortal portal, Content content) {

	portal.removePool(content);
	List<Node> nodesToDelete = new ArrayList<Node>();

	for (Node parentNode : content.getParents()) {
	    if (portal.equals(parentNode.getParent().getPortal())) {
		nodesToDelete.add(parentNode);
	    }
	}

	for (; !nodesToDelete.isEmpty(); nodesToDelete.get(0).delete())
	    ;
    }
}