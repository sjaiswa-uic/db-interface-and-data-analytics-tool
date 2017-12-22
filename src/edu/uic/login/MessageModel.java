package edu.uic.login;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * @ Backing Managed-Bean Description: This type of managed-bean participates in the "View" concern
 * of the MVC design pattern. The purpose of a backing-bean is to support UI logic, and has a 1::1
 * relationship with a JSF view, or a JSF form in a Facelet composition. Although it typically has
 * JavaBean-style properties with associated getters/setters, these are properties of the View --
 * not of the underlying application data model.
 * 
 **/
@ManagedBean
@RequestScoped
public class MessageModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;

    private Integer rowCount;

    private Integer columnCount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }
}
