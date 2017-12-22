package edu.uic.data;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @ Backing Managed-Bean Description: This type of managed-bean participates in the "View" concern
 * of the MVC design pattern. The purpose of a backing-bean is to support UI logic, and has a 1::1
 * relationship with a JSF view, or a JSF form in a Facelet composition. Although it typically has
 * JavaBean-style properties with associated getters/setters, these are properties of the View --
 * not of the underlying application data model.
 * 
 **/
@ManagedBean
@ViewScoped
public class PageModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> tableList;

    private List<String> columnList;

    private TableData tableData;


    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    public List<String> getTableList() {
        return tableList;
    }

    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }
}
