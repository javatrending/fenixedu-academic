/*
 * Created on Nov 6, 2003
 *
 */
package net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.exams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Servico.commons.ReadExecutionCourseByOID;
import net.sourceforge.fenixedu.applicationTier.Servico.resourceAllocationManager.ReadRoomByOID;
import net.sourceforge.fenixedu.dataTransferObject.InfoExecutionCourse;
import net.sourceforge.fenixedu.dataTransferObject.InfoRoom;
import net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.utils.ServiceUtils;
import net.sourceforge.fenixedu.presentationTier.Action.resourceAllocationManager.utils.SessionConstants;
import net.sourceforge.fenixedu.presentationTier.Action.utils.ContextUtils;
import net.sourceforge.fenixedu.util.DiaSemana;
import net.sourceforge.fenixedu.util.HourMinuteSecond;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.validator.DynaValidatorForm;
import org.joda.time.YearMonthDay;

import pt.ist.fenixWebFramework.security.UserView;

/**
 * @author Ana e Ricardo
 * 
 */
public class AssociateRoomToExamDA extends FenixDateAndTimeContextDispatchAction
// extends
// FenixExecutionCourseAndExecutionDegreeAndCurricularYearContextDispatchAction
{

    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	String infoExamId = (String) request.getAttribute(SessionConstants.EXAM_OID);
	if (infoExamId == null) {
	    infoExamId = request.getParameter(SessionConstants.EXAM_OID);
	}
	request.setAttribute(SessionConstants.EXAM_OID, infoExamId);

	DynaActionForm examForm = (DynaActionForm) form;
	IUserView userView = UserView.getUser();

	String[] executionCourse = (String[]) examForm.get("executionCourses");
	request.setAttribute("executionCoursesArray", executionCourse);

	List<String> executionCourseNames = new ArrayList<String>();

	for (int iterEC = 0; iterEC < executionCourse.length; iterEC++) {

	    InfoExecutionCourse infoExecutionCourse = (InfoExecutionCourse) ReadExecutionCourseByOID.run(new Integer(
		    executionCourse[iterEC]));

	    executionCourseNames.add(infoExecutionCourse.getNome());
	}

	request.setAttribute(SessionConstants.LIST_EXECUTION_COURSE_NAMES, executionCourseNames);

	// exam start time
	Calendar examStartTime = Calendar.getInstance();
	Integer startHour = new Integer((String) examForm.get("beginningHour"));
	Integer startMinute = new Integer((String) examForm.get("beginningMinute"));
	examStartTime.set(Calendar.HOUR_OF_DAY, startHour.intValue());
	examStartTime.set(Calendar.MINUTE, startMinute.intValue());
	examStartTime.set(Calendar.SECOND, 0);

	// exam end time
	Calendar examEndTime = Calendar.getInstance();
	Integer endHour = new Integer((String) examForm.get("endHour"));
	Integer endMinute = new Integer((String) examForm.get("endMinute"));
	examEndTime.set(Calendar.HOUR_OF_DAY, endHour.intValue());
	examEndTime.set(Calendar.MINUTE, endMinute.intValue());
	examEndTime.set(Calendar.SECOND, 0);

	// exam date
	Calendar examDate = Calendar.getInstance();
	Integer day = new Integer((String) examForm.get("day"));
	Integer month = new Integer((String) examForm.get("month"));
	Integer year = new Integer((String) examForm.get("year"));
	examDate.set(Calendar.YEAR, year.intValue());
	examDate.set(Calendar.MONTH, month.intValue() - 1);
	examDate.set(Calendar.DAY_OF_MONTH, day.intValue());

	int dayOfWeekInt = examDate.get(Calendar.DAY_OF_WEEK);
	DiaSemana dayOfWeek = new DiaSemana(dayOfWeekInt);

	Object args[] = { YearMonthDay.fromCalendarFields(examDate), YearMonthDay.fromCalendarFields(examDate),
		HourMinuteSecond.fromCalendarFields(examStartTime), HourMinuteSecond.fromCalendarFields(examEndTime), dayOfWeek,
		null, null, Boolean.FALSE };

	List<InfoRoom> availableInfoRoom = (List<InfoRoom>) ServiceUtils.executeService("ReadAvailableRoomsForExam", args);

	String[] rooms = (String[]) examForm.get("rooms");
	List<InfoRoom> selectedRooms = new ArrayList<InfoRoom>();
	List<InfoRoom> finalAvailableRooms = new ArrayList<InfoRoom>();

	if (rooms != null && rooms.length > 0) {

	    for (int iterRooms = 0; iterRooms < rooms.length; iterRooms++) {
		InfoRoom infoRoom = (InfoRoom) ReadRoomByOID.run(Integer.valueOf(rooms[iterRooms]));
		selectedRooms.add(infoRoom);
	    }

	    for (int iterSR = 0; iterSR < selectedRooms.size(); iterSR++) {
		InfoRoom selectedInfoRoom = (InfoRoom) selectedRooms.get(iterSR);

		boolean infoContida = false;

		for (int iterAIF = 0; iterAIF < availableInfoRoom.size(); iterAIF++) {
		    InfoRoom availInfoRoom = (InfoRoom) availableInfoRoom.get(iterAIF);

		    if (selectedInfoRoom.equals(availInfoRoom)) {
			infoContida = true;
			break;
		    }
		}

		if (!infoContida) {
		    finalAvailableRooms.add(selectedInfoRoom);
		}
	    }

	    finalAvailableRooms.addAll(availableInfoRoom);

	    sortList(request, finalAvailableRooms);

	    request.setAttribute(SessionConstants.AVAILABLE_ROOMS, finalAvailableRooms);
	} else {
	    sortList(request, availableInfoRoom);
	    request.setAttribute(SessionConstants.AVAILABLE_ROOMS, availableInfoRoom);
	}

	String date = new String((String) examForm.get("day") + "/" + (String) examForm.get("month") + "/"
		+ (String) examForm.get("year"));

	String startTime = new String((String) examForm.get("beginningHour") + ":" + (String) examForm.get("beginningMinute"));

	String endTime = new String((String) examForm.get("endHour") + ":" + (String) examForm.get("endMinute"));

	request.setAttribute(SessionConstants.EXAM_DATEANDTIME_STR, date + " das " + startTime + " �s " + endTime);

	String[] scopeIDArray = (String[]) examForm.get("scopes");
	request.setAttribute("scopes", scopeIDArray);

	InfoExecutionCourse infoExecutionCourse = (InfoExecutionCourse) request.getAttribute(SessionConstants.EXECUTION_COURSE);
	request.setAttribute("executionCourseOID", infoExecutionCourse.getIdInternal());

	return mapping.findForward("AssociateRoom");
    }

    public ActionForward choose(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	String infoExamId = (String) request.getAttribute(SessionConstants.EXAM_OID);
	if (infoExamId == null) {
	    infoExamId = request.getParameter(SessionConstants.EXAM_OID);
	}
	request.setAttribute(SessionConstants.EXAM_OID, infoExamId);

	DynaValidatorForm chooseRoomForm = (DynaValidatorForm) form;

	String[] scopeIDArray = (String[]) chooseRoomForm.get("scopes");
	request.setAttribute("scopes", scopeIDArray);

	String[] roomIDArray = (String[]) chooseRoomForm.get("rooms");
	request.setAttribute("rooms", roomIDArray);

	ContextUtils.setCurricularYearContext(request);
	ContextUtils.setExecutionDegreeContext(request);
	ContextUtils.setExecutionPeriodContext(request);
	ContextUtils.setCurricularYearsContext(request);

	String executionDegreeOID = (String) request.getAttribute(SessionConstants.EXECUTION_DEGREE_OID);
	request.setAttribute("executionDegreeOID", executionDegreeOID);
	// request.setAttribute(SessionConstants.EXECUTION_DEGREE_OID,
	// executionDegreeOID);

	InfoExecutionCourse infoExecutionCourse = (InfoExecutionCourse) request.getAttribute(SessionConstants.EXECUTION_COURSE);
	request.setAttribute("executionCourseOID", infoExecutionCourse.getIdInternal());

	return mapping.findForward("forwardChoose");
    }

    private void sortList(HttpServletRequest request, List<InfoRoom> infoRooms) {

	String sortParameter = request.getParameter("sortBy");

	if ((sortParameter != null) && (sortParameter.length() != 0)) {
	    if (sortParameter.equals("capacity")) {
		Collections.sort(infoRooms, new ReverseComparator(new BeanComparator("capacidadeExame")));
	    } else if (sortParameter.equals("building")) {
		Collections.sort(infoRooms, new BeanComparator("edificio"));
	    } else {
		Collections.sort(infoRooms, new BeanComparator("tipo"));
	    }
	} else {
	    Collections.sort(infoRooms, new BeanComparator("capacidadeExame"));
	}
    }
}