package edu.uic.dataimport;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import edu.uic.data.DataService;
import edu.uic.data.TableData;
import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;


/*
 * Controller Managed-Bean
 * 
 * This type of managed-bean participates in the "Controller" concern of the MVC design pattern. The
 * purpose of a controller bean is to execute some kind of business logic and return a navigation
 * outcome to the JSF navigation-handler. JSF controller-beans typically have JSF action methods.
 * 
 */
@ManagedBean
@RequestScoped
public class DataImportController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{dataImportForm}")
    private DataImportForm dataImportForm;

    @ManagedProperty(value = "#{dataImportModel}")
    private DataImportModel dataImportModel;

    @ManagedProperty(value = "#{messageModel}")
    MessageModel messageModel;
    
    // Since LoginForm is session scopped I am injecting it into
    // all the controllers for the ease of connecting to database
    @ManagedProperty(value = "#{loginForm}")
    LoginForm loginForm;

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public DataImportForm getDataImportForm() {
        return dataImportForm;
    }

    public void setDataImportForm(DataImportForm dataImportForm) {
        this.dataImportForm = dataImportForm;
    }

    public DataImportModel getDataImportModel() {
        return dataImportModel;
    }

    public void setDataImportModel(DataImportModel dataImportModel) {
        this.dataImportModel = dataImportModel;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public String insertDataFile() throws SQLException {
        
        if(!validateSelections()){
            return "data-import-review";
        }
        DataInsertService insertService = new DataInsertService();
        DataImportModel model = (DataImportModel) getSessionScopedObject("reviewDataSet");
        boolean success = false;
        
        // Do the deed & insert all that data
        try {
            if (! insertService.doesTableExist(this.loginForm, model.getLabel())) {
                insertService.smartInsertDataSet(getSelectedEntries(), model, this.loginForm);
            }else {
                insertService.insertDataSet(getSelectedEntries(), model, this.loginForm);
            }
            success = true;
        } catch (Exception e) {
            model.setStatus("Error while trying to insert Dataset : " + e);
            return "data-import-review";
        }
        
        // Based on the success of insertion operation navigate the user to the appropriate page
        if (success){
            model.setStatus("A Table was created & inserted into the DB Successfully");
            this.setDataImportModel(model);
            return "data-import-success";
        } else {
            return "data-import-review";
        }
    }
    
    public String uploadFile() throws IOException {
        DataInsertService insertService = new DataInsertService();
        DataImportService service = new DataImportService();

        // first upload the file to project directory in tomcat runtime
        if(isValidInput()){
            service.writeFileToDisk(this.dataImportForm, this.dataImportModel);
        }else {
            return "data-import";
        }

        // prepare the file stats
        List<Row> rows;
        try {
            rows = service.transformFile(this.dataImportForm, this.loginForm);
            this.dataImportModel.setColumnCount(rows.get(0).getRowValues().size());
            this.dataImportModel.setRowCount(rows.size());
            this.dataImportModel.setStatus("File Uploaded Successfully");
            this.dataImportModel.setFileContentType(this.dataImportForm.getFileFormat());
            this.dataImportModel.setFileName(this.dataImportForm.getFileLabel());
            this.dataImportModel.setLabel(this.dataImportForm.getFileLabel());
            this.dataImportModel.setFileType(this.dataImportForm.getFileType());
            

            boolean doesTableExist = insertService.doesTableExist(this.loginForm, this.dataImportModel.getLabel());
            TableData tableData = new TableData();
            tableData.setRows(rows);
            this.dataImportModel.setTableData(
                    processHeader(tableData, doesTableExist, this.dataImportModel.getLabel()));
            
            if ("data".equals(this.dataImportModel.getFileType())) {
                this.dataImportModel.setTableName(insertService.getTableNameFromLabel(this.dataImportModel.getLabel()));
                setSessionScopedObject("reviewDataSet", this.dataImportModel);
                if(doesTableExist){
                    this.messageModel.setStatus("The table already Exists. "
                            + "Are you sure you want to insert this data into the already existing table.");
                }
                return "data-import-review";
            } else if ("metadata".equals(this.dataImportModel.getFileType())){
                if(doesTableExist){
                    this.messageModel.setStatus("Could not Create Table beacuse a Table with the same name already exists.");
                    return "data-import";
                }
                insertService.createTableFromMetadata(this.dataImportModel, this.loginForm);
                this.dataImportModel.setTableName(insertService.getTableNameFromLabel(this.dataImportModel.getLabel()));
                return "data-import-success";
            }
        } catch (Exception e) {
            this.messageModel.setStatus("Error while Importing File");
            return "data-import";
        }

        return "data-import-success";
    }

    public boolean validateSelections(){
        if (getSelectedEntries() == null || getSelectedEntries().length < 1){
            this.messageModel.setStatus("Please Select atleast 1 option to continue");
            return false; 
        }
        return true;
    }

    public boolean isValidInput() {
        if (this.dataImportForm.getData() == null || this.dataImportForm.getData().getSize() < 1) {
            this.messageModel.setStatus("Invalid Data File. Upload a valid File.");
            return false;
        }

        if(null == this.dataImportForm.getDatasetLabel() || null == this.dataImportForm.getFileLabel() || "".equals(this.dataImportForm.getDatasetLabel()) || "".equals(this.dataImportForm.getFileLabel())){
            this.messageModel.setStatus("Please Enter Label Name For Data Upload.");
            return false;
        }
        return true;
    }

    private Object getSessionScopedObject(String key){
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        return externalContext.getSessionMap().get(key);
    }
    
    private void setSessionScopedObject(String key, Object object){
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put(key, object);
    }
    
    private String[] getSelectedEntries(){
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return req.getParameterValues("selectedDataEntries");
    }
    
    private TableData processHeader(TableData tableData, boolean doesTableExist, String labelName) {
        DataService service = new DataService();
        DataInsertService insertService = new DataInsertService();
        
        if("Y".equalsIgnoreCase(this.dataImportForm.getHeaderIndicator())){
            tableData.setColumnNames((List<String>)(List)tableData.getRows().get(0).getRowValues());
            tableData.getRows().remove(0);
        }else {
            int size = tableData.getRows().get(0).getRowValues().size();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < size; i++ ){
                columnNames.add("Label" + i);
            }
            tableData.setColumnNames(columnNames);
        }
        
        if(doesTableExist) {
            List<String> columns = service.getColumnNames(
                    getLoginForm(), getMessageModel(), insertService.getTableNameFromLabel(labelName));
            columns.remove("ROW_INDEX");
            tableData.setColumnNames(columns);
        }
        return tableData;
    }
}
