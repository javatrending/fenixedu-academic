/*
 * Created on Jan 12, 2005
 *
 */
package net.sourceforge.fenixedu.dataTransferObject.projectsManagement;

import java.util.ResourceBundle;

import net.sourceforge.fenixedu.dataTransferObject.DataTranferObject;
import net.sourceforge.fenixedu.util.projectsManagement.ExcelStyle;
import net.sourceforge.fenixedu.util.projectsManagement.ReportType;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * @author Susana Fernandes
 * 
 */
public class InfoReportLine extends DataTranferObject implements IReportLine {

    public int getNumberOfColumns() {
	return 0;
    }

    public Double getValue(int column) {
	return null;
    }

    public void getHeaderToExcel(HSSFSheet sheet, ExcelStyle excelStyle, ReportType reportType) {
    }

    public void getLineToExcel(HSSFSheet sheet, ExcelStyle excelStyle, ReportType reportType) {
    }

    public void getTotalLineToExcel(HSSFSheet sheet, ExcelStyle excelStyle, ReportType reportType) {
    }

    protected String getString(String label) {
	return (ResourceBundle.getBundle("resources.ProjectsManagementResources", Language.getLocale())).getString(label);
    }
}
