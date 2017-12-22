package edu.uic.descriptivestat;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * @ Backing Managed Form Bean Description: This type of managed-bean participates in the "View"
 * concern of the MVC design pattern. But specifically dealing with transfering the user input data
 * fields from the Web UI into the controllers.
 * 
 **/
@ManagedBean
@RequestScoped
public class DescriptiveForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dataLabel;
    
    private String columnName;
    
    private String renderType;
    
    private List<String> columnList;
    
    private List<String> operations;
    
    public String getRenderType() {
        return renderType;
    }

    public void setRenderType(String renderType) {
        this.renderType = renderType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
    }
}
