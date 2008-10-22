package net.sourceforge.fenixedu.domain.student;

import net.sourceforge.fenixedu.dataTransferObject.candidacy.PrecedentDegreeInformationBean;
import net.sourceforge.fenixedu.domain.RootDomainObject;
import net.sourceforge.fenixedu.domain.candidacy.StudentCandidacy;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.organizationalStructure.UnitUtils;

import org.apache.commons.lang.StringUtils;

public class PrecedentDegreeInformation extends PrecedentDegreeInformation_Base {

    public PrecedentDegreeInformation() {
	super();
	setRootDomainObject(RootDomainObject.getInstance());
    }

    public PrecedentDegreeInformation(final StudentCandidacy studentCandidacy) {
	this();
	super.setStudentCandidacy(studentCandidacy);
    }

    public String getInstitutionName() {
	return hasInstitution() ? getInstitution().getName() : null;
    }

    public void edit(PrecedentDegreeInformationBean precedentDegreeInformationBean) {

	Unit institution = precedentDegreeInformationBean.getInstitution();
	if (institution == null && !StringUtils.isEmpty(precedentDegreeInformationBean.getInstitutionName())) {
	    institution = UnitUtils.readExternalInstitutionUnitByName(precedentDegreeInformationBean.getInstitutionName());
	    if (institution == null) {
		institution = Unit.createNewNoOfficialExternalInstitution(precedentDegreeInformationBean.getInstitutionName());
	    }
	}

	this.setInstitution(institution);
	this.setDegreeDesignation(precedentDegreeInformationBean.getDegreeDesignation());
	this.setConclusionGrade(precedentDegreeInformationBean.getConclusionGrade());
	this.setConclusionYear(precedentDegreeInformationBean.getConclusionYear());
	this.setCountry(precedentDegreeInformationBean.getCountry());
	this.setSchoolLevel(precedentDegreeInformationBean.getSchoolLevel());
	this.setOtherSchoolLevel(precedentDegreeInformationBean.getOtherSchoolLevel());
    }

    public void delete() {
	removeCountry();
	removeInstitution();
	removeStudent();
	removeStudentCandidacy();

	removeRootDomainObject();
	deleteDomainObject();
    }

}
