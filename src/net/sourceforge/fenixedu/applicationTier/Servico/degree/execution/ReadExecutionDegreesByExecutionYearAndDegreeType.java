/*
 * Created on Dec 11, 2003 by jpvl
 *  
 */
package net.sourceforge.fenixedu.applicationTier.Servico.degree.execution;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.FenixService;
import net.sourceforge.fenixedu.dataTransferObject.InfoExecutionDegree;
import net.sourceforge.fenixedu.domain.ExecutionDegree;
import net.sourceforge.fenixedu.domain.degree.DegreeType;
import pt.ist.fenixWebFramework.services.Service;

/**
 * @author jpvl
 */
public class ReadExecutionDegreesByExecutionYearAndDegreeType extends FenixService {

    @Service
    public static List run(String executionYear, DegreeType degreeType) {

	final List<ExecutionDegree> executionDegrees = degreeType == null ? ExecutionDegree.getAllByExecutionYear(executionYear)
		: ExecutionDegree.getAllByExecutionYearAndDegreeType(executionYear, degreeType);

	final List infoExecutionDegreeList = new ArrayList<InfoExecutionDegree>();
	for (final ExecutionDegree executionDegree : executionDegrees) {
	    infoExecutionDegreeList.add(InfoExecutionDegree.newInfoFromDomain(executionDegree));
	}
	return infoExecutionDegreeList;
    }
}