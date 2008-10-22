package net.sourceforge.fenixedu.presentationTier.Action.vigilancy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy.ChangeConvokeActive;
import net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy.ChangeConvokeStatus;
import net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy.ConvokesAttended;
import net.sourceforge.fenixedu.applicationTier.Servico.person.vigilancy.CreateConvokes;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.WrittenEvaluation;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.vigilancy.AttendingStatus;
import net.sourceforge.fenixedu.domain.vigilancy.ExamCoordinator;
import net.sourceforge.fenixedu.domain.vigilancy.Vigilancy;
import net.sourceforge.fenixedu.domain.vigilancy.Vigilant;
import net.sourceforge.fenixedu.domain.vigilancy.VigilantGroup;
import net.sourceforge.fenixedu.domain.vigilancy.strategies.StrategySugestion;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

public class ConvokeManagement extends FenixDispatchAction {

    public ActionForward showReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	String writtenEvaluationId = request.getParameter("writtenEvaluationId");
	ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
	WrittenEvaluation writtenEvaluation = (WrittenEvaluation) RootDomainObject.readDomainObjectByOID(WrittenEvaluation.class,
		Integer.valueOf(writtenEvaluationId));

	if (currentExecutionYear != writtenEvaluation.getExecutionYear()) {
	    request.setAttribute("permission", false);
	} else {
	    request.setAttribute("permission", true);
	}
	request.setAttribute("writtenEvaluation", writtenEvaluation);
	return mapping.findForward("showReport");
    }

    public ActionForward prepareEditConvoke(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	recoverBeanFromRequest(request);
	return mapping.findForward("prepareEditConvoke");
    }

    public ActionForward changeVisualizationOptions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean bean = (ConvokeBean) RenderUtils.getViewState("options").getMetaObject().getObject();

	Person person = getLoggedPerson(request);
	ExamCoordinator coordinator = person.getExamCoordinatorForGivenExecutionYear(bean.getExecutionYear());

	bean.setExamCoordinator(coordinator);
	if (coordinator != null) {
	    bean.setVigilantGroups(coordinator.getVigilantGroups());
	} else {
	    bean.setVigilantGroups(getVigilantGroups(request, bean.getExecutionYear()));
	}

	VigilantGroup group = bean.getSelectedVigilantGroup();

	putInformationOnRequest(request, group, bean.isShowInformationByVigilant(), bean.getExecutionYear());
	request.setAttribute("bean", bean);

	RenderUtils.invalidateViewState("options");
	return mapping.findForward("prepareEditConvoke");
    }

    private void editAttend(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String id = request.getParameter("oid");
	Integer idInternal = Integer.valueOf(id);
	String bool = request.getParameter("bool");
	Boolean value = Boolean.valueOf(bool);
	Vigilancy convoke = (Vigilancy) RootDomainObject.readDomainObjectByOID(Vigilancy.class, idInternal);

	try {

	    ConvokesAttended.run(convoke, value);
	} catch (DomainException exception) {
	    addActionMessage(request, exception.getMessage());
	}

    }

    public ActionForward convokeAttended(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	editAttend(mapping, form, request, response);
	return prepareEditConvoke(mapping, form, request, response);
    }

    public ActionForward changeConvokeStatusInReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	String id = request.getParameter("oid");
	Integer idInternal = Integer.valueOf(id);
	String participationType = request.getParameter("participationType");
	AttendingStatus status = AttendingStatus.valueOf(participationType);

	Vigilancy vigilancy = (Vigilancy) RootDomainObject.readDomainObjectByOID(Vigilancy.class, idInternal);
	try {

	    ChangeConvokeStatus.run(vigilancy, status);
	} catch (DomainException exception) {
	    addActionMessage(request, exception.getMessage());
	}
	return showReport(mapping, form, request, response);
    }

    private void editActive(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	String id = request.getParameter("oid");
	Integer idInternal = Integer.valueOf(id);
	String bool = request.getParameter("bool");
	Boolean value = Boolean.valueOf(bool);
	Person person = getLoggedPerson(request);
	Vigilancy convoke = (Vigilancy) RootDomainObject.readDomainObjectByOID(Vigilancy.class, idInternal);

	try {

	    ChangeConvokeActive.run(convoke, value, person);
	} catch (DomainException exception) {
	    addActionMessage(request, exception.getMessage());
	}
    }

    public ActionForward convokeActive(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	editActive(mapping, form, request, response);
	return prepareEditConvoke(mapping, form, request, response);
    }

    public ActionForward convokeActiveEditInReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	editActive(mapping, form, request, response);
	return showReport(mapping, form, request, response);
    }

    public ActionForward prepareConvoke(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean bean = new ConvokeBean();
	ExamCoordinator coordinator = getCoordinatorForCurrentYear(request);
	Person person = getLoggedPerson(request);
	ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();

	Unit unit = coordinator.getUnit();

	bean.setPerson(person);
	bean.setUnit(unit);
	bean.setExecutionYear(executionYear);
	bean.setExamCoordinator(coordinator);

	request.setAttribute("bean", bean);
	return mapping.findForward("prepareGenerateConvokes");

    }

    public ActionForward generateConvokesSugestion(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean bean = (ConvokeBean) RenderUtils.getViewState("selectEvaluation").getMetaObject().getObject();
	VigilantGroup vigilantGroup = bean.getSelectedVigilantGroup();
	WrittenEvaluation evaluation = bean.getWrittenEvaluation();

	if (vigilantGroup != null && evaluation != null) {
	    if (vigilantGroup.getAllAssociatedWrittenEvaluations().contains(evaluation)) {
		StrategySugestion vigilantSugestion = vigilantGroup.sugestVigilantsToConvoke(evaluation);
		bean.setTeachersForAGivenCourse(vigilantSugestion.getVigilantsThatAreTeachersSugestion());
		bean.setSelectedTeachers(bean.getTeachersForAGivenCourse());
		bean.setVigilantsSugestion(vigilantSugestion.getVigilantSugestion());
		bean.setUnavailableVigilants(vigilantSugestion.getUnavailableVigilants());
		bean.setUnavailableInformation(vigilantSugestion.getUnavailableVigilantsWithInformation());
	    } else {
		bean.setWrittenEvaluation(null);
	    }
	}
	request.setAttribute("bean", bean);
	RenderUtils.invalidateViewState("selectEvaluation");
	return mapping.findForward("prepareGenerateConvokes");
    }

    public ActionForward createConvokes(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean bean = (ConvokeBean) RenderUtils.getViewState("confirmConvokes").getMetaObject().getObject();
	List<Vigilant> vigilantSugestion = bean.getVigilants();
	WrittenEvaluation writtenEvaluation = bean.getWrittenEvaluation();

	try {
	    CreateConvokes.run(vigilantSugestion, writtenEvaluation, bean.getSelectedVigilantGroup(), bean.getExamCoordinator(),
		    bean.getEmailMessage());
	} catch (DomainException e) {
	    addActionMessage(request, e.getMessage());
	}
	recoverBeanFromRequest(request);
	return mapping.findForward("prepareEditConvoke");
    }

    public ActionForward confirmConvokes(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean beanWithTeachers = (ConvokeBean) RenderUtils.getViewState("selectVigilantsThatAreTeachers").getMetaObject()
		.getObject();

	ConvokeBean beanWithVigilants = (ConvokeBean) RenderUtils.getViewState("selectVigilants").getMetaObject().getObject();

	ConvokeBean beanWithUnavailables = (ConvokeBean) RenderUtils.getViewState("selectVigilantsThatAreUnavailable")
		.getMetaObject().getObject();
	List<Vigilant> teachers, vigilants, unavailables;

	teachers = beanWithTeachers.getSelectedTeachers();
	vigilants = beanWithVigilants.getVigilants();
	unavailables = beanWithUnavailables.getSelectedUnavailableVigilants();

	String convokedVigilants = beanWithVigilants.getTeachersAsString();
	String teachersVigilancies = beanWithTeachers.getVigilantsAsString();

	vigilants.addAll(teachers);
	vigilants.addAll(unavailables);
	beanWithVigilants.setVigilants(vigilants);

	String email = RenderUtils.getResourceString("VIGILANCY_RESOURCES", "label.vigilancy.emailConvoke");
	MessageFormat format = new MessageFormat(email);
	WrittenEvaluation evaluation = beanWithVigilants.getWrittenEvaluation();
	DateTime beginDate = evaluation.getBeginningDateTime();
	String date = beginDate.getDayOfMonth() + "/" + beginDate.getMonthOfYear() + "/" + beginDate.getYear();

	String minutes = String.format("%02d", new Object[] { beginDate.getMinuteOfHour() });

	Object[] args = { evaluation.getFullName(), date, beginDate.getHourOfDay(), minutes,
		beanWithVigilants.getRoomsAsString(), teachersVigilancies, convokedVigilants,
		beanWithVigilants.getSelectedVigilantGroup().getRulesLink() };
	beanWithVigilants.setEmailMessage(format.format(args));
	request.setAttribute("bean", beanWithVigilants);
	return mapping.findForward("confirmConvokes");
    }

    public ActionForward revertConvokes(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	return forwardBean(mapping, request, "convoke", "prepareGenerateConvokes");

    }

    public ActionForward selectVigilantGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	return forwardBean(mapping, request, "selectGroup", "prepareGenerateConvokes");
    }

    public ActionForward prepareAddMoreVigilants(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	String writtenEvalatuionId = request.getParameter("writtenEvaluationId");
	WrittenEvaluation writtenEvaluation = (WrittenEvaluation) RootDomainObject.readDomainObjectByOID(WrittenEvaluation.class,
		Integer.valueOf(writtenEvalatuionId));

	Person person = getLoggedPerson(request);
	ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();
	ExamCoordinator coordinator = person.getExamCoordinatorForGivenExecutionYear(executionYear);
	Unit unit = coordinator.getUnit();

	ConvokeBean bean = new ConvokeBean();

	bean.setPerson(person);
	bean.setUnit(unit);
	bean.setExecutionYear(executionYear);
	bean.setExamCoordinator(coordinator);
	bean.setWrittenEvaluation(writtenEvaluation);

	List<VigilantGroup> allGroups = coordinator.getVigilantGroups();

	bean.setVigilantGroups(allGroups);
	VigilantGroup group = writtenEvaluation.getAssociatedExecutionCourses().get(0).getVigilantGroup();
	StrategySugestion sugestion = group.sugestVigilantsToConvoke(writtenEvaluation);

	bean.setVigilantsSugestion(sugestion.getVigilantSugestion());
	bean.setUnavailableVigilants(sugestion.getUnavailableVigilants());
	bean.setUnavailableInformation(sugestion.getUnavailableVigilantsWithInformation());
	bean.setTeachersForAGivenCourse(sugestion.getVigilantsThatAreTeachersSugestion());
	bean.setSelectedVigilantGroup(group);

	request.setAttribute("bean", bean);
	return mapping.findForward("prepareGenerateConvokes");

    }

    public ActionForward showConvokesByVigilants(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	recoverBeanFromRequest(request);
	return mapping.findForward("prepareEditConvoke");
    }

    public ActionForward showConvokesByEvaluation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	recoverBeanFromRequest(request);
	return mapping.findForward("prepareEditConvoke");
    }

    public ActionForward exportVigilancyTable(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	recoverBeanFromRequest(request);
	return mapping.findForward("export-table");
    }

    public ActionForward checkForIncompatibilities(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	ConvokeBean bean = (ConvokeBean) RenderUtils.getViewState("selectVigilants").getMetaObject().getObject();
	List<Vigilant> vigilants = bean.getTeachersForAGivenCourse();
	vigilants.addAll(bean.getVigilants());
	List<Vigilant> incompatibleVigilants = new ArrayList<Vigilant>();
	for (Vigilant vigilant : vigilants) {
	    if (vigilant.hasIncompatiblePerson()) {
		incompatibleVigilants.add(vigilant.getIncompatibleVigilant());
	    }
	}
	vigilants.retainAll(incompatibleVigilants);
	if (!vigilants.isEmpty()) {
	    request.setAttribute("incompatibleVigilants", vigilants);
	}
	request.setAttribute("bean", bean);
	return mapping.findForward("prepareGenerateConvokes");
    }

    private ExamCoordinator getCoordinatorForCurrentYear(HttpServletRequest request) throws FenixFilterException,
	    FenixServiceException {
	ExecutionYear currentYear = ExecutionYear.readCurrentExecutionYear();
	Person person = getLoggedPerson(request);
	ExamCoordinator coordinator = person.getExamCoordinatorForGivenExecutionYear(currentYear);
	return coordinator;
    }

    private ActionForward forwardBean(ActionMapping mapping, HttpServletRequest request, String viewStateName, String forwardTo) {
	ConvokeBean bean = (ConvokeBean) RenderUtils.getViewState(viewStateName).getMetaObject().getObject();

	RenderUtils.invalidateViewState(viewStateName);
	request.setAttribute("bean", bean);
	return mapping.findForward(forwardTo);
    }

    private void recoverBeanFromRequest(HttpServletRequest request) throws FenixFilterException, FenixServiceException {
	ConvokeBean bean = new ConvokeBean();
	String vigilantGroup = request.getParameter("gid");
	String incompatiblities = request.getParameter("showIncompatibilities");
	String unavailables = request.getParameter("showUnavailables");
	String convokeInfo = request.getParameter("showConvokeInfo");
	String whatToShow = request.getParameter("whatToShow");
	String bounds = request.getParameter("showBoundsJustification");
	String notActiveConvokes = request.getParameter("showNotActiveConvokes");
	String startPoints = request.getParameter("showStartPoints");
	String pointsWeight = request.getParameter("showPointsWeight");
	bean.setShowInformationByVigilant((whatToShow != null) ? whatToShow.equals("vigilants") : Boolean.TRUE);
	bean.setShowIncompatibilities((incompatiblities != null) ? Boolean.valueOf(incompatiblities) : Boolean.FALSE);
	bean.setShowUnavailables((unavailables != null) ? Boolean.valueOf(unavailables) : Boolean.FALSE);
	bean.setShowBoundsJustification((bounds != null) ? Boolean.valueOf(bounds) : Boolean.FALSE);
	bean.setShowAllVigilancyInfo((convokeInfo != null) ? Boolean.valueOf(convokeInfo) : Boolean.FALSE);
	bean.setShowNotActiveConvokes((notActiveConvokes != null) ? Boolean.valueOf(notActiveConvokes) : Boolean.FALSE);
	bean.setShowStartPoints((startPoints != null) ? Boolean.valueOf(startPoints) : Boolean.FALSE);
	bean.setShowPointsWeight((pointsWeight != null) ? Boolean.valueOf(pointsWeight) : Boolean.FALSE);
	ExamCoordinator coordinator = getCoordinatorForCurrentYear(request);
	bean.setExamCoordinator(coordinator);
	bean.setVigilantGroups(coordinator.getVigilantGroups());
	String executionYear = request.getParameter("executionYear");
	ExecutionYear executionYearObj = executionYear != null ? (ExecutionYear) RootDomainObject.readDomainObjectByOID(
		ExecutionYear.class, Integer.valueOf(executionYear)) : null;
	VigilantGroup group = null;
	if (vigilantGroup != null) {
	    group = (VigilantGroup) RootDomainObject.readDomainObjectByOID(VigilantGroup.class, Integer.valueOf(vigilantGroup));
	    bean.setSelectedVigilantGroup(group);
	}
	bean.setExecutionYear(executionYearObj);
	putInformationOnRequest(request, group, bean.isShowInformationByVigilant(), executionYearObj);
	request.setAttribute("bean", bean);
    }

    private void putInformationOnRequest(HttpServletRequest request, VigilantGroup group, boolean showVigilants,
	    ExecutionYear executionYear) throws FenixFilterException, FenixServiceException {

	if (showVigilants) {
	    request.setAttribute("group", group);
	} else {
	    List<WrittenEvaluation> writtenEvaluations = new ArrayList<WrittenEvaluation>();
	    if (group != null) {
		writtenEvaluations.addAll(group.getAllAssociatedWrittenEvaluations());
	    } else {
		List<VigilantGroup> groups = getVigilantGroups(request, executionYear);

		for (VigilantGroup vigilantGroup : groups) {
		    writtenEvaluations.addAll(vigilantGroup.getAllAssociatedWrittenEvaluations());
		}
	    }
	    Collections.sort(writtenEvaluations, new ReverseComparator(WrittenEvaluation.COMPARATOR_BY_BEGIN_DATE));
	    request.setAttribute("writtenEvaluations", writtenEvaluations);
	}
    }

    private List<VigilantGroup> getVigilantGroups(HttpServletRequest request, ExecutionYear executionYear) {
	Person person = getLoggedPerson(request);
	ExamCoordinator examcoordinatior = person.getExamCoordinatorForGivenExecutionYear(ExecutionYear
		.readCurrentExecutionYear());
	Unit unit = examcoordinatior.getUnit();
	List<VigilantGroup> groups = unit.getVigilantGroupsForGivenExecutionYear(executionYear);
	return groups;
    }
}