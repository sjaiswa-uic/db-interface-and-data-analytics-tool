package edu.uic.system.memo;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ComponentSystemEvent;

import edu.uic.data.DataService;
import edu.uic.data.PageForm;
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
public class SystemMemoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{systemMemoModel}")
    SystemMemoModel systemMemoModel;

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

    public SystemMemoModel getSystemMemoModel() {
        return systemMemoModel;
    }

    public void setSystemMemoModel(SystemMemoModel systemMemoModel) {
        this.systemMemoModel = systemMemoModel;
    }

    public void loadTableData(ComponentSystemEvent componentSystemEvent) {
        
            DataService dataService = new DataService();
            PageForm form  = new PageForm();
            form.setQuery("SELECT * FROM s17g307_system_memo ORDER BY WHEN_ DESC");
            this.systemMemoModel.setTableData(dataService.executeSelect(this.loginForm, this.messageModel, form));
    }
}
