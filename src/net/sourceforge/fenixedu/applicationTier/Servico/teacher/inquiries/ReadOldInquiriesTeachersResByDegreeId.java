/*
 * Created on Feb 4, 2005
 * 
 */
package net.sourceforge.fenixedu.applicationTier.Servico.teacher.inquiries;

import java.util.List;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.inquiries.InfoOldInquiriesTeachersRes;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.inquiries.OldInquiriesTeachersRes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 * @author Jo�o Fialho & Rita Ferreira
 * 
 */
public class ReadOldInquiriesTeachersResByDegreeId extends FenixService {

    public List run(Integer degreeId) throws FenixServiceException {
	Degree degree = rootDomainObject.readDegreeByOID(degreeId);

	if (degree == null) {
	    throw new FenixServiceException("nullDegreeId");
	}

	List<OldInquiriesTeachersRes> oldInquiriesTeachersResList = degree.getAssociatedOldInquiriesTeachersRes();

	CollectionUtils.transform(oldInquiriesTeachersResList, new Transformer() {

	    public Object transform(Object oldInquiriesTeachersRes) {
		InfoOldInquiriesTeachersRes ioits = new InfoOldInquiriesTeachersRes();
		try {
		    ioits.copyFromDomain((OldInquiriesTeachersRes) oldInquiriesTeachersRes);

		} catch (Exception ex) {
		}

		return ioits;
	    }
	});

	return oldInquiriesTeachersResList;
    }

}
