<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml />
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<h2><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.exemptions" /></h2>

<p class="mtop15 mbottom05"><strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.person" /></strong></p>
<fr:view name="person" schema="person.view-with-name-and-idDocumentType-and-documentIdNumber">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2 thlight thright mtop05" />
		<fr:property name="rowClasses" value="tdhl1,," />
	</fr:layout>
</fr:view>

<p class="mbottom05"><strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.events"/></strong></p>
<logic:notEmpty name="eventsToApplyExemption">
	<fr:view name="eventsToApplyExemption" schema="AccountingEvent.view">
		<fr:layout name="tabular">
			
			<fr:property name="classes" value="tstyle4 mtop05" />
			
			<fr:property name="linkFormat(showExemptions)" value="/exemptionsManagement.do?method=showExemptions&eventId=${externalId}" />
			<fr:property name="key(showExemptions)" value="label.payments.exemptions" />
			<fr:property name="bundle(showExemptions)" value="ACADEMIC_OFFICE_RESOURCES" />
							
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<logic:empty name="eventsToApplyExemption">
	<p>
		<em>
			<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.exemptions.noEventsToApplyExemption" />
		</em>
	</p>
</logic:empty>

<bean:define id="personId" name="person" property="externalId" />
<fr:form action="<%="/payments.do?method=backToShowOperations&amp;personId=" + personId%>">
	<p>
		<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit">
			<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="button.back" />
		</html:submit>
	</p>
</fr:form>
