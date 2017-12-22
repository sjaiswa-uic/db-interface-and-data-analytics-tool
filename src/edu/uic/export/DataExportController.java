package edu.uic.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;

import edu.uic.data.PageForm;
import edu.uic.dataimport.DataImportService;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;

@ManagedBean
@RequestScoped
public class DataExportController implements Serializable{
    
    private static final long serialVersionUID = 1L;

    // Since LoginForm is session scoped I am injecting it into
    // all the controllers for the ease of connecting to database
    @ManagedProperty(value = "#{loginForm}")
    LoginForm loginForm;

    @ManagedProperty(value = "#{messageModel}")
    MessageModel messageModel;


    @ManagedProperty(value = "#{pageForm}")
    PageForm pageForm;

    public PageForm getPageForm() {
        return pageForm;
    }

    public void setPageForm(PageForm pageForm) {
        this.pageForm = pageForm;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public void exportData(String fileType){
       
        if(this.pageForm.getTableName() == null ||
                pageForm.getTableName().length() < 1){
            this.messageModel.setStatus("Please Select a valid Table to Export Data");
            return;
        }
        
        DataExportService dataImportService = new DataExportService();
        File file = null;
        String contentType, headerFileName = ""; 
        
        if("xml".equalsIgnoreCase(fileType)){
            contentType = "text/xml";
            headerFileName = "attachment;filename=" + pageForm.getTableName() + ".xml";
            file = dataImportService.exportDataToXML(loginForm, messageModel, pageForm.getTableName());
        } else if ("xls".equalsIgnoreCase(fileType)){
            contentType = "text/xls";
            headerFileName = "attachment;filename=" + pageForm.getTableName() + ".xls";
            file = dataImportService.exportDataToExcel(loginForm, messageModel, pageForm.getTableName());
        } else {
            contentType = "text/csv";
            headerFileName = "attachment;filename=" + pageForm.getTableName() + ".csv";
            file = dataImportService.exportDataToCSV(loginForm, pageForm.getTableName());
        }
        FacesContext faces = FacesContext.getCurrentInstance();
        ExternalContext external = faces.getExternalContext();
        
        external.responseReset();
        external.setResponseContentType(contentType);
        external.setResponseHeader("Content-Disposition", headerFileName);
        
        try {
            external.getResponseOutputStream().write(
                    IOUtils.toByteArray(new FileInputStream(file)));
            external.getResponseOutputStream().flush();
            external.getResponseOutputStream().close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        faces.responseComplete();
    }
}
