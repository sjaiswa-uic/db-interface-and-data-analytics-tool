package edu.uic.data;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @ Backing Managed Form Bean Description: This type of managed-bean participates in the "View"
 * concern of the MVC design pattern. But specifically dealing with transfering the user input data
 * fields from the Web UI into the controllers.
 * 
 **/
@ManagedBean
@RequestScoped
public class PageForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;

    private List<String> columnList;

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }
}
