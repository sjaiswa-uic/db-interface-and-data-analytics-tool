package edu.uic.login;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller Managed-Bean
 * 
 * This type of managed-bean participates in the "Controller" concern of the MVC design pattern. The
 * purpose of a controller bean is to execute some kind of business logic and return a navigation
 * outcome to the JSF navigation-handler. JSF controller-beans typically have JSF action methods.
 * 
 **/
@ManagedBean
@RequestScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_NAME = "root";

    private static final String PASSWORD = "smriti1";

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

    public String authenticate() {

        String returnViewName = "login";
        LoginService service = new LoginService();

        if (USER_NAME.equals(this.loginForm.getUsername())
                && PASSWORD.equals(this.loginForm.getPassword())) {

            populateUserDetails(this.loginForm);
            returnViewName = service.authenticate(this.loginForm, this.messageModel)
                    ? "data-interaction" : returnViewName;
        } else {
            if (USER_NAME.equals(this.loginForm.getUsername())
                    && !PASSWORD.equals(this.loginForm.getPassword())) {

                this.messageModel
                        .setStatus("Wrong passord. Enter Correct password to access database...");
            } else {
                this.messageModel.setStatus("*******Please Enter correct login Details!******");
            }
        }
        return returnViewName;
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        new LoginService().logout(this.loginForm, this.messageModel);;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        return "login.xhtml";
    }
    
    public void populateUserDetails(LoginForm loginForm){
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest)context.getRequest();
        
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        
        this.loginForm.setLoggedIn(true);
        this.loginForm.setIpAddress(remoteAddr);
        this.loginForm.setSessionId(((HttpSession)context.getSession(false)).getId());
    }
}
