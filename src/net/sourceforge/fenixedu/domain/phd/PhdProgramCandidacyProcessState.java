package net.sourceforge.fenixedu.domain.phd;

public enum PhdProgramCandidacyProcessState implements PhdProcessStateType {

    PRE_CANDIDATE,

    STAND_BY_WITH_MISSING_INFORMATION,

    STAND_BY_WITH_COMPLETE_INFORMATION,

    PENDING_FOR_COORDINATOR_OPINION,

    REJECTED,

    WAITING_FOR_SCIENTIFIC_COUNCIL_RATIFICATION,

    RATIFIED_BY_SCIENTIFIC_COUNCIL,

    CONCLUDED;

    @Override
    public String getName() {
	return name();
    }

}
