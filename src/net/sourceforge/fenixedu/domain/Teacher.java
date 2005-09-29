package net.sourceforge.fenixedu.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.teacher.professorship.ResponsibleForValidator;
import net.sourceforge.fenixedu.applicationTier.Servico.teacher.professorship.ResponsibleForValidator.InvalidCategory;
import net.sourceforge.fenixedu.applicationTier.Servico.teacher.professorship.ResponsibleForValidator.MaxResponsibleForExceed;
import net.sourceforge.fenixedu.dataTransferObject.credits.InfoCredits;
import net.sourceforge.fenixedu.domain.credits.util.InfoCreditsBuilder;
import net.sourceforge.fenixedu.domain.finalDegreeWork.IGroup;
import net.sourceforge.fenixedu.domain.finalDegreeWork.IGroupStudent;
import net.sourceforge.fenixedu.domain.finalDegreeWork.IProposal;
import net.sourceforge.fenixedu.domain.publication.IPublication;
import net.sourceforge.fenixedu.domain.publication.IPublicationTeacher;
import net.sourceforge.fenixedu.domain.publication.PublicationTeacher;
import net.sourceforge.fenixedu.util.PublicationArea;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

public class Teacher extends Teacher_Base {

    /***************************************************************************
     * BUSINESS SERVICES *
     **************************************************************************/

    public void addToTeacherInformationSheet(IPublication publication, PublicationArea publicationArea) {
        new PublicationTeacher(publication, this, publicationArea);
    }

    public void removeFromTeacherInformationSheet(IPublication publication) {
        Iterator<IPublicationTeacher> iterator = getTeacherPublications().iterator();

        while (iterator.hasNext()) {
            IPublicationTeacher publicationTeacher = iterator.next();
            if (publicationTeacher.getPublication().equals(publication)) {
                iterator.remove();
                publicationTeacher.delete();
                return;
            }
        }
    }

    public Boolean canAddPublicationToTeacherInformationSheet(PublicationArea area) {
        // NOTA : a linha seguinte cont�m um n�mero expl�cito quando n�o deve
        // isto deve ser mudado! Mas esta mudan�a implica tornar expl�cito o
        // conceito de Ficha de docente.
        return new Boolean(countPublicationsInArea(area) < 5);

    }

    public List responsibleFors() {
        List<IProfessorship> professorships = this.getProfessorships();
        List<IProfessorship> res = new ArrayList<IProfessorship>();

        for (IProfessorship professorship : professorships) {
            if (professorship.getResponsibleFor())
                res.add(professorship);
        }
        return res;
    }

    public IProfessorship responsibleFor(Integer executionCourseId) {
        List<IProfessorship> professorships = this.getProfessorships();

        for (IProfessorship professorship : professorships) {
            if (professorship.getResponsibleFor()
                    && professorship.getExecutionCourse().getIdInternal().equals(executionCourseId))
                return professorship;
        }
        return null;
    }

    public void updateResponsabilitiesFor(Integer executionYearId, List<Integer> executionCourses)
            throws MaxResponsibleForExceed, InvalidCategory {

        if (executionYearId == null || executionCourses == null)
            throw new NullPointerException();

        List<IProfessorship> professorships = this.getProfessorships();

        for (IProfessorship professorship : professorships) {
            IExecutionCourse executionCourse = professorship.getExecutionCourse();
            ResponsibleForValidator.getInstance().validateResponsibleForList(this, executionCourse,
                    professorship);
            if (executionCourse.getExecutionPeriod().getExecutionYear().getIdInternal().equals(
                    executionYearId)) {
                professorship.setResponsibleFor(executionCourses.contains(executionCourse
                        .getIdInternal()));
            }
        }
    }

    public IDepartment getWorkingDepartment() {

        IEmployee employee = this.getPerson().getEmployee();
        if (employee != null) {
            return employee.getDepartmentWorkingPlace();
        }
        return null;
    }

    public IDepartment getMailingDepartment() {

        IEmployee employee = this.getPerson().getEmployee();
        if (employee != null) {
            return employee.getDepartmentMailingPlace();
        }
        return null;
    }

    public List<IProposal> getFinalDegreeWorksByExecutionYear(IExecutionYear executionYear) {
        List<IProposal> proposalList = new ArrayList<IProposal>();
        for (Iterator iter = getAssociatedProposalsByOrientator().iterator(); iter.hasNext();) {
            IProposal proposal = (IProposal) iter.next();
            if (proposal.getExecutionDegree().getExecutionYear().equals(executionYear)) {
                // if it was attributed by the coordinator the proposal is
                // efective
                if (proposal.getGroupAttributed() != null) {
                    proposalList.add(proposal);
                }
                // if not, we have to verify if the teacher has proposed it to
                // any student(s) and if that(those) student(s) has(have) accepted it
                else {
                    IGroup attributedGroupByTeacher = proposal.getGroupAttributedByTeacher();
                    if (attributedGroupByTeacher != null) {                        
                        boolean result = false;
                        for (Iterator iterator = attributedGroupByTeacher.getGroupStudents().iterator(); iterator
                                .hasNext();) {
                            IGroupStudent groupStudent = (IGroupStudent) iterator.next();
                            IProposal studentProposal = groupStudent
                                    .getFinalDegreeWorkProposalConfirmation();
                            if (studentProposal != null && studentProposal.equals(proposal)) {
                                result = true;
                            } else {
                                result = false;
                            }
                        }
                        if (result) {
                            proposalList.add(proposal);
                        }
                    }
                }
            }
        }
        return proposalList;
    }

    public List<IExecutionCourse> getLecturedExecutionCoursesByExecutionYear(IExecutionYear executionYear) {
        List<IExecutionCourse> executionCourses = new ArrayList();
        for (Iterator iter = executionYear.getExecutionPeriods().iterator(); iter.hasNext();) {
            IExecutionPeriod executionPeriod = (IExecutionPeriod) iter.next();
            executionCourses.addAll(getLecturedExecutionCoursesByExecutionPeriod(executionPeriod));
        }
        return executionCourses;
    }

    public List<IExecutionCourse> getLecturedExecutionCoursesByExecutionPeriod(
            final IExecutionPeriod executionPeriod) {
        return (List<IExecutionCourse>) CollectionUtils.collect(getProfessorships(), new Transformer() {

            public Object transform(Object arg0) {
                IProfessorship professorship = (IProfessorship) arg0;
                return professorship.getExecutionCourse();
            }
        });
    }

    /***************************************************************************
     * OTHER METHODS *
     **************************************************************************/

    public String toString() {
        String result = "[Dominio.Teacher ";
        result += ", teacherNumber=" + getTeacherNumber();
        result += ", person=" + getPerson();
        result += ", category= " + getCategory();
        result += "]";
        return result;
    }

    public InfoCredits getExecutionPeriodCredits(IExecutionPeriod executionPeriod) {
        return InfoCreditsBuilder.build(this, executionPeriod);
    }

    /***************************************************************************
     * PRIVATE METHODS *
     **************************************************************************/

    private int countPublicationsInArea(PublicationArea area) {
        int count = 0;
        for (IPublicationTeacher publicationTeacher : getTeacherPublications()) {
            if (publicationTeacher.getPublicationArea().equals(area)) {
                count++;
            }
        }
        return count;
    }
}
