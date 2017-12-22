package edu.uic.dataimport;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import edu.uic.data.TableData;

/**
 * @ Backing Managed-Bean Description: This type of managed-bean participates in the "View" concern
 * of the MVC design pattern. The purpose of a backing-bean is to support UI logic, and has a 1::1
 * relationship with a JSF view, or a JSF form in a Facelet composition. Although it typically has
 * JavaBean-style properties with associated getters/setters, these are properties of the View --
 * not of the underlying application data model.
 * 
 **/
@ManagedBean
@SessionScoped
public class DataImportModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private String status;

    private Integer rowCount;

    private Integer columnCount;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String fileContentType;

    private String filePath;
    
    private TableData tableData;
    
    private String tableName;
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
