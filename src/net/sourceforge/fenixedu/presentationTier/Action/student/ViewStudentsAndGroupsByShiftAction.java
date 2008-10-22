/*
 * Created on 8/Jan/2005
 *
 */
package net.sourceforge.fenixedu.presentationTier.Action.student;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidSituationServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.publico.ReadStudentsAndGroupsByShiftID;
import net.sourceforge.fenixedu.applicationTier.Servico.student.ReadExportGroupingsByGrouping;
import net.sourceforge.fenixedu.dataTransferObject.InfoExportGrouping;
import net.sourceforge.fenixedu.dataTransferObject.InfoSiteStudentsAndGroups;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixContextAction;
import net.sourceforge.fenixedu.presentationTier.Action.exceptions.FenixActionException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author joaosa & rmalo
 * 
 */
public class ViewStudentsAndGroupsByShiftAction extends FenixContextAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws FenixActionException, FenixFilterException, FenixServiceException {

	IUserView userView = getUserView(request);

	String groupPropertiesCodeString = request.getParameter("groupPropertiesCode");
	Integer groupPropertiesCode = new Integer(groupPropertiesCodeString);
	String shiftCodeString = request.getParameter("shiftCode");
	Integer shiftCode = new Integer(shiftCodeString);

	InfoSiteStudentsAndGroups infoSiteStudentsAndGroups = new InfoSiteStudentsAndGroups();

	try {
	    infoSiteStudentsAndGroups = (InfoSiteStudentsAndGroups) ReadStudentsAndGroupsByShiftID.run(groupPropertiesCode,
		    shiftCode);

	} catch (InvalidSituationServiceException e) {
	    ActionErrors actionErrors2 = new ActionErrors();
	    ActionError error2 = null;
	    error2 = new ActionError("error.noProject");
	    actionErrors2.add("error.noProject", error2);
	    saveErrors(request, actionErrors2);
	    return mapping.findForward("viewExecutionCourseProjects");
	} catch (FenixServiceException e) {
	    throw new FenixActionException(e);
	}

	request.setAttribute("infoSiteStudentsAndGroups", infoSiteStudentsAndGroups);

	List<InfoExportGrouping> infoExportGroupings = (List<InfoExportGrouping>) ReadExportGroupingsByGrouping
		.run(groupPropertiesCode);
	request.setAttribute("infoExportGroupings", infoExportGroupings);

	return mapping.findForward("sucess");
    }
}