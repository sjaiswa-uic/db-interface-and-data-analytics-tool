package edu.uic.security;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.uic.login.LoginForm;

/**
 * Filter checks if LoginBean has loginIn property set to true. If it is not set then request is
 * being redirected to the login.xhml page.
 *
 */
@WebFilter(filterName ="SecurityFilter", urlPatterns = {"*.xhtml"} )
public class SecurityFilter implements Filter {

    public SecurityFilter(){}
    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    
        // check whether session variable is set
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        LoginForm loginBean = null;
        // Get the loginBean from session attribute
        if(session != null){ 
            loginBean = session.getAttribute("loginForm") != null 
                    ? (LoginForm) session.getAttribute("loginForm") : null ;
        }
        // For the first application request there is no loginBean in the session so user needs to
        // log in
        // For other requests loginBean is present but we need to check if user has logged in
        // successfully
        //  allow user to proccede if url is login.xhtml or user logged in or user is accessing any page in //public folder
        String reqURI = req.getRequestURI();
        if ( reqURI.indexOf("/login.xhtml") >= 0 || (loginBean != null && loginBean.isLoggedIn())
                || reqURI.contains("javax.faces.resource") ){
            chain.doFilter(request, response);
        } else {
            res.sendRedirect("login.xhtml");  
        }
    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }

}