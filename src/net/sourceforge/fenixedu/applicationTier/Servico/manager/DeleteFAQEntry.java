/*
 * Created on 2004/08/30
 * 
 */
package net.sourceforge.fenixedu.applicationTier.Servico.manager;

import net.sourceforge.fenixedu.applicationTier.FenixService;

/**
 * @author Luis Cruz
 */
public class DeleteFAQEntry extends FenixService {

    public void run(Integer entryId) {
	rootDomainObject.readFAQEntryByOID(entryId).delete();
    }

}
