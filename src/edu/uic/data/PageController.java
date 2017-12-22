package edu.uic.data;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import edu.uic.login.LoginForm;
import edu.uic.login.MessageModel;

/**
 * Controller Managed-Bean
 * 
 * This type of managed-bean participates in the "Controller" concern of the MVC design pattern. The
 * purpose of a controller bean is to execute some kind of business logic and return a navigation
 * outcome to the JSF navigation-handler. JSF controller-beans typically have JSF action methods.
 **/
@ManagedBean
@RequestScoped
public class PageController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{pageModel}")
    PageModel pageModel;

    @ManagedProperty(value = "#{pageForm}")
    PageForm pageForm;

    // Since LoginForm is session scopped I am injecting it into
    // all the controllers for the ease of connecting to database
    @ManagedProperty(value = "#{loginForm}")
    LoginForm loginForm;

    @ManagedProperty(value = "#{messageModel}")
    MessageModel messageModel;

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;
    }

    public PageForm getPageForm() {
        return pageForm;
    }

    public void setPageForm(PageForm pageForm) {
        this.pageForm = pageForm;
    }

    public void getColumns() {
        DataService dataService = new DataService();
        this.pageModel.setColumnList(dataService.getColumnNames(this.loginForm, this.messageModel,
                this.pageForm.getTableName()));
    }

    public void processBeforePageLoad(ComponentSystemEvent componentSystemEvent) {
        DataService dataService = new DataService();
        this.pageModel.setTableList(dataService.getTableNames(this.loginForm, this.messageModel));
    }

    public void getTables() {
        DataService dataService = new DataService();
        this.pageModel.setTableList(dataService.getTableNames(this.loginForm, this.messageModel));
    }

    public void executeQuery() {
        DataService dataService = new DataService();
        this.pageModel.setTableData(
                dataService.executeQuery(this.loginForm, this.messageModel, this.pageForm));
    }

    public void executeSelect() {
        DataService dataService = new DataService();
        this.pageModel.setTableData(
                dataService.executeSelect(this.loginForm, this.messageModel, this.pageForm));
    }
    
    public String loadTableData() {
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String tableName =  params.get("tableName");
        
        if (tableName!= null && !tableName.isEmpty()){
            DataService dataService = new DataService();
            this.pageForm = new PageForm();
            this.pageForm.setTableName(tableName);
            this.pageModel.setTableData(
                    dataService.executeSelect(this.loginForm, this.messageModel, this.pageForm));
        }
        
        return "data-interaction.xhtml";
    }

    public void executeUpdate() {
        DataService dataService = new DataService();
        dataService.executeUpdate(this.loginForm, this.messageModel, this.pageForm.getQuery());
    }
}
